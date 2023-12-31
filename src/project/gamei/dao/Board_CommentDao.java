package project.gamei.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import project.gamei.dto.Board_CommentDto;

public class Board_CommentDao {
	public final static int SUCCESS = 1;
	public final static int FAIL = 0;
	private DataSource ds;
	private static Board_CommentDao INSTANCE = new Board_CommentDao();
	
	public static Board_CommentDao getInstance() {
		return INSTANCE;
	}
	
	public Board_CommentDao() {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/Oracle11g");
		} catch (NamingException e) {
			System.out.println(e.getMessage());
		}
	}	
	// (1) 특정 게시글에 있는 댓글 내용을 출력. 원글의 bno가 필요.
	public ArrayList<Board_CommentDto> listComment(int bno, int startRow, int endRow) {
		ArrayList<Board_CommentDto> list = new ArrayList<Board_CommentDto>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM (SELECT ROWNUM RN, A.* FROM "
				+ "(SELECT M.MNICKNAME, M.MPHOTO, M.MLEVEL, M.MEMAIL, BC.* FROM MEMBER M, BOARD_COMMENT BC "
				+ "WHERE M.MID = BC.MID AND BC.BNO = ? ORDER BY BC.BCGROUP, BC.BCSTEP) A) "
				+ "WHERE RN BETWEEN ? AND ?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bno);
			pstmt.setInt(2, startRow);
			pstmt.setInt(3, endRow);
			rs = pstmt.executeQuery();
			while (rs.next()) {			
				String mnickname = rs.getString("mnickname");
				String mphoto = rs.getString("mphoto");
				int mlevel = rs.getInt("mlevel");
				String memail = rs.getString("memail");				
				int bcno = rs.getInt("bcno");								
				String bctext = rs.getString("bctext");
				String bcip = rs.getString("bcip");
				int bcgroup = rs.getInt("bcgroup");
				int bcstep = rs.getInt("bcstep");
				int bcindent = rs.getInt("bcindent");				
				String mid = rs.getString("mid");	
				Timestamp bcrdate = rs.getTimestamp("bcrdate");
				list.add(new Board_CommentDto(mnickname, mphoto, mlevel, memail, bcno, bctext, bcip, bcgroup, bcstep, bcindent, bno, mid, bcrdate));		
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return list;
	}
	
	// (2) 특정 게시글 내에 있는 댓글이 몇 개인지를 count
	public int boardCommentCnt(int bno) {
		int commentCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT COUNT(*) CNT FROM BOARD_COMMENT WHERE BNO = ?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bno);
			rs = pstmt.executeQuery();			
			rs.next();
			commentCnt = rs.getInt("CNT");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return commentCnt;
	}	
	// (3) 댓글 작성
	public int writeComment(int bno, Board_CommentDto dto) {
		int result = FAIL;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO BOARD_COMMENT (BCNO, BCTEXT, BCIP, BCGROUP, BCSTEP, BCINDENT, BNO, MID) " + 
				"VALUES (BOARD_COMMENT_SEQ.NEXTVAL, ?, ?, BOARD_COMMENT_SEQ.CURRVAL, 0, 0, ?, ?)";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getBctext());			
			pstmt.setString(2, dto.getBcip());			
			pstmt.setInt(3, bno);
			pstmt.setString(4, dto.getMid());			
			result = pstmt.executeUpdate();
			System.out.println(bno + "게시글에 댓글 작성 완료");
		} catch (SQLException e) {
			System.out.println(e.getMessage() + bno + "게시글에 글쓰기 실패 - " + dto);
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}		
		return result;
	}
	
	// (4) 댓글 삭제
	public int deleteComment(int bcno) {
		int result = FAIL;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM BOARD_COMMENT WHERE BCNO=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bcno);			
			result = pstmt.executeUpdate();
			System.out.println(bcno + "댓글 삭제됨");
		} catch (SQLException e) {
			System.out.println(e.getMessage() + bcno + "댓글 삭제 실패");
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}		
		return result;
	}	
	
	// (4-2) 관리자모드에서 신고된 댓글을 삭제하거나, 댓글이 있는 게시글을 삭제하는 용도의 메소드
	public int deleteAllComment(int bno) {
		int result = FAIL;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM BOARD_COMMENT WHERE BNO=?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bno);			
			result = pstmt.executeUpdate();			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}		
		return result;
	}	
	
	
	// (5) 댓글의 댓글 작성 사전작업
	private void preReplyCommentStep(int bcgroup, int bcstep) {
		Connection        conn  = null;
		PreparedStatement pstmt = null;
		String sql = "UPDATE BOARD_COMMENT SET BCSTEP = BCSTEP + 1 WHERE BCGROUP=? AND BCSTEP>?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bcgroup);
			pstmt.setInt(2, bcstep);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage() + " preReplyStep에서 오류");
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn  != null) conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} 
		}	
	}
	// (6) 댓글의 group, step, indent를 알기 위해, 원본 댓글의 정보를 가져온다. 원본 댓글의 bcno가 필요.
	public Board_CommentDto getOriginReplyInfo(int bcno) {
		Board_CommentDto dto = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM BOARD_COMMENT WHERE BCNO = ?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bcno);			
			rs = pstmt.executeQuery();
			if (rs.next()) {														
				String bctext = rs.getString("bctext");
				String bcip = rs.getString("bcip");
				int bcgroup = rs.getInt("bcgroup");
				int bcstep = rs.getInt("bcstep");
				int bcindent = rs.getInt("bcindent");
				int bno = rs.getInt("bno");
				String mid = rs.getString("mid");	
				Timestamp bcrdate = rs.getTimestamp("bcrdate");
				dto = new Board_CommentDto(bctext, bcip, bcgroup, bcstep, bcindent, bno, mid, bcrdate);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}		
		return dto;
	}	
	
	// (7) 댓글 작성
	public int replyComment(int bno, Board_CommentDto dto) {
		int result = FAIL;
		preReplyCommentStep(dto.getBcgroup(), dto.getBcstep());
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO BOARD_COMMENT (BCNO, BCTEXT, BCIP, BCGROUP, BCSTEP, BCINDENT, BNO, MID) " 
				+ "VALUES (BOARD_COMMENT_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);			
			pstmt.setString(1, dto.getBctext());
			pstmt.setString(2, dto.getBcip());		
			pstmt.setInt(3, dto.getBcgroup());
			pstmt.setInt(4, dto.getBcstep() + 1);
			pstmt.setInt(5, dto.getBcindent()+ 1);
			pstmt.setInt(6, bno);
			pstmt.setString(7, dto.getMid());			
			result = pstmt.executeUpdate();
			System.out.println(bno + "게시글에 댓글 답변글 작성 완료");
		} catch (SQLException e) {
			System.out.println(e.getMessage() + bno + "게시판에 답변글작성 실패 - " + dto);
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return result;
	}	
}
