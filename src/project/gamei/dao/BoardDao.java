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
import project.gamei.dto.BoardDto;

public class BoardDao {
	public static final int SUCCESS = 1;
	public static final int FAIL = 0;
	private DataSource ds;
	private static BoardDao INSTANCE = new BoardDao();

	public static BoardDao getInstance() {
		return INSTANCE;
	}

	public BoardDao() {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/Oracle11g");
		} catch (NamingException e) {
			System.out.println(e.getMessage());
		}
	}

	// (1) 특정 gid의 게시글 목록을 출력함. 회원의 닉네임과 프로필사진, 레벨 정보 포함하고 게임명 / 게임아이콘, 댓글 갯수를 포함해야 함. 페이징 처리 - 게시글은 10개씩.
	public ArrayList<BoardDto> listBoard(String gid, int startRow, int endRow) {
		ArrayList<BoardDto> list = new ArrayList<BoardDto>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM (SELECT ROWNUM RN, A.* " + 
				"				FROM (SELECT G.GNAME, G.GICON, M.MNICKNAME, M.MPHOTO, M.MLEVEL, M.MEMAIL, " + 
				"				 B.* , (SELECT COUNT(*) FROM BOARD_COMMENT WHERE BNO=B.BNO) CNT " +
				"                 FROM MEMBER M, BOARD B, GAME G " +
				"				WHERE B.MID=M.MID AND B.GID=G.GID AND G.GID = ? ORDER BY BGROUP DESC, BSTEP) A) " + 
				"				WHERE RN BETWEEN ? AND ?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, gid);
			pstmt.setInt(2, startRow);
			pstmt.setInt(3, endRow);
			rs = pstmt.executeQuery();
			while (rs.next()) {				
				String gname = rs.getString("gname");
				String gicon = rs.getString("gicon");
				String mnickname = rs.getString("mnickname");
				String mphoto = rs.getString("mphoto");
				int mlevel = rs.getInt("mlevel");
				String memail = rs.getString("memail");				
				int bno = rs.getInt("bno");				
				String btitle = rs.getString("btitle");				
				String bcontent = rs.getString("bcontent");				
				Timestamp brdate = rs.getTimestamp("brdate");				
				String bimg = rs.getString("bimg");				
				String bip = rs.getString("bip");	
				int bgroup = rs.getInt("bgroup");
				int bstep = rs.getInt("bstep");
				int bindent = rs.getInt("bindent");
				int bhit = rs.getInt("bhit");
				String mid = rs.getString("mid");
				int cnt = rs.getInt("cnt");
				list.add(new BoardDto(gname, gicon, bno, btitle, bcontent, brdate, bimg, bip, bgroup, bstep, bindent, bhit, gid, mid, mphoto, mnickname, mlevel, memail, cnt));				
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
	
	// (2) 특정 gid의 게시글의 총 숫자가 몇 개인지를 셈. 페이징을 위한 count
	public int boardCntByGid(String gid) {
		int boardCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT COUNT(*) CNT FROM BOARD WHERE GID = ?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, gid);
			rs = pstmt.executeQuery();			
			rs.next();
			boardCnt = rs.getInt("CNT");
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
		return boardCnt;
	}
	
	// (3) 게시판 검색. 제목 검색 기능.
	public ArrayList<BoardDto> listBoardBySearchTitle(String searchWord, String gid, int startRow, int endRow) {
		ArrayList<BoardDto> list = new ArrayList<BoardDto>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM (SELECT ROWNUM RN, A.*" + 
				"				FROM (SELECT G.GNAME, G.GICON, M.MNICKNAME, M.MPHOTO, M.MLEVEL, M.MEMAIL," + 
				"				B.*, (SELECT COUNT(*) FROM BOARD_COMMENT WHERE BNO = B.BNO) CNT FROM MEMBER M, BOARD B, GAME G" + 
				"				WHERE B.MID=M.MID AND B.GID=G.GID AND G.GID = ? ORDER BY BRDATE DESC) A) " + 
				"				WHERE RN BETWEEN ? AND ? AND BTITLE LIKE '%'||?||'%'";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, gid);
			pstmt.setInt(2, startRow);
			pstmt.setInt(3, endRow);			
			pstmt.setString(4, searchWord);
			rs = pstmt.executeQuery();
			while (rs.next()) {				
				String gname = rs.getString("gname");
				String gicon = rs.getString("gicon");
				String mnickname = rs.getString("mnickname");
				String mphoto = rs.getString("mphoto");
				int mlevel = rs.getInt("mlevel");
				String memail = rs.getString("memail");				
				int bno = rs.getInt("bno");				
				String btitle = rs.getString("btitle");				
				String bcontent = rs.getString("bcontent");				
				Timestamp brdate = rs.getTimestamp("brdate");				
				String bimg = rs.getString("bimg");				
				String bip = rs.getString("bip");	
				int bgroup = rs.getInt("bgroup");
				int bstep = rs.getInt("bstep");
				int bindent = rs.getInt("bindent");
				int bhit = rs.getInt("bhit");
				String mid = rs.getString("mid");	
				int cnt = rs.getInt("cnt");
				list.add(new BoardDto(gname, gicon, bno, btitle, bcontent, brdate, bimg, bip, bgroup, bstep, bindent, bhit, gid, mid, mphoto, mnickname, mlevel, memail, cnt));				
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
	
	// (4) 타이틀로 검색했을 때의 페이지 수를 셈. 페이징 처리를 위함
	public int boardCntByGidSearchByTitle(String searchWord, String gid) {
		int boardCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT COUNT(*) CNT FROM BOARD WHERE GID = ? AND BTITLE LIKE '%'||?||'%'";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, gid);			
			pstmt.setString(2, searchWord);
			rs = pstmt.executeQuery();			
			rs.next();
			boardCnt = rs.getInt("CNT");
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
		return boardCnt;
	}
	
	
	// (5) 글 작성자로 검색기능.
	public ArrayList<BoardDto> listBoardBySearchWriter(String searchWord, String gid, int startRow, int endRow) {
		ArrayList<BoardDto> list = new ArrayList<BoardDto>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM (SELECT ROWNUM RN, A.* " + 
				"				FROM (SELECT G.GNAME, G.GICON, M.MNICKNAME, M.MPHOTO, M.MLEVEL, M.MEMAIL, " + 
				"				B.*, (SELECT COUNT(*) FROM BOARD_COMMENT WHERE BNO = B.BNO) CNT FROM MEMBER M, BOARD B, GAME G " + 
				"				WHERE B.MID=M.MID AND B.GID=G.GID AND G.GID = ? ORDER BY BRDATE DESC) A) " + 
				"				WHERE RN BETWEEN ? AND ? AND MNICKNAME LIKE '%'||?||'%'";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, gid);
			pstmt.setInt(2, startRow);
			pstmt.setInt(3, endRow);			
			pstmt.setString(4, searchWord);
			rs = pstmt.executeQuery();
			while (rs.next()) {				
				String gname = rs.getString("gname");
				String gicon = rs.getString("gicon");
				String mnickname = rs.getString("mnickname");
				String mphoto = rs.getString("mphoto");
				int mlevel = rs.getInt("mlevel");
				String memail = rs.getString("memail");				
				int bno = rs.getInt("bno");				
				String btitle = rs.getString("btitle");				
				String bcontent = rs.getString("bcontent");				
				Timestamp brdate = rs.getTimestamp("brdate");				
				String bimg = rs.getString("bimg");				
				String bip = rs.getString("bip");	
				int bgroup = rs.getInt("bgroup");
				int bstep = rs.getInt("bstep");
				int bindent = rs.getInt("bindent");
				int bhit = rs.getInt("bhit");
				String mid = rs.getString("mid");		
				int cnt = rs.getInt("cnt");
				list.add(new BoardDto(gname, gicon, bno, btitle, bcontent, brdate, bimg, bip, bgroup, bstep, bindent, bhit, gid, mid, mphoto, mnickname, mlevel, memail, cnt));				
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
	
	// (6) 글 작성자로 검색했을 때의 페이지 수를 셈. 페이징 처리를 위함
	public int boardCntByGidSearchByWriter(String searchWord, String gid) {
		int boardCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT COUNT(*) CNT FROM BOARD, MEMBER WHERE GID = ? AND BOARD.MID=MEMBER.MID AND MNICKNAME LIKE '%'||?||'%'";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, gid);			
			pstmt.setString(2, searchWord);
			rs = pstmt.executeQuery();			
			rs.next();
			boardCnt = rs.getInt("CNT");
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
		return boardCnt;
	}
	
	// (6-1) 게시글 상세보기시 해당 게시글의 조회수를 +1 up
	public void hitup(int bno) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "UPDATE BOARD SET BHIT = BHIT +1 WHERE BNO = ?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bno);
			pstmt.executeUpdate();
			System.out.println(bno + "번글 조회수 up");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println(bno + "번 글 조회수 up 실패");
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
	}
		
	// (7) 게시글 상세보기를 위해, 특정 BNO의 DTO를 가져옴. 게임 정보와 회원 정보를 포함.
	public BoardDto getBoardContent(String gid, int bno) {
		BoardDto dto = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT B.*, G.GID, G.GNAME, G.GICON, M.MNICKNAME, M.MPHOTO, M.MEMAIL, M.MLEVEL "
				+ "FROM BOARD B,GAME G, MEMBER M WHERE G.GID=B.GID AND M.MID=B.MID "
				+ "AND G.GID=? AND B.BNO =?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, gid);
			pstmt.setInt(2, bno);
			rs = pstmt.executeQuery();
			if (rs.next()) {					
				String btitle = rs.getString("btitle");
				String bcontent = rs.getString("bcontent");
				Timestamp brdate = rs.getTimestamp("brdate");
				String bimg = rs.getString("bimg");					
				int bgroup = rs.getInt("bgroup");
				int bstep = rs.getInt("bstep");
				int bindent = rs.getInt("bindent");		
				String mid = rs.getString("mid");
				String bip = rs.getString("bip");
				int bhit = rs.getInt("bhit");
				String gname = rs.getString("gname");
				String gicon = rs.getString("gicon");
				String mnickname = rs.getString("mnickname");
				String mphoto = rs.getString("mphoto");
				String memail = rs.getString("memail");
				int mlevel = rs.getInt("mlevel");
				dto = new BoardDto(bno, btitle, bcontent, brdate, bimg, bgroup, bstep, bindent, gid, mid, bip, bhit, gname, gicon, mnickname, mphoto, memail, mlevel);
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
	
	// (8) 특정 게시판에 원글을 작성.
	public int writeBoard(String gid, String mid, BoardDto dto) {
		int result = FAIL;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO BOARD "
				+ "(BNO, BTITLE, BCONTENT, BIMG, BGROUP, BSTEP, BINDENT, GID, MID, BIP) " 
				+ "VALUES (BOARD_SEQ.NEXTVAL, ?, ?, ?, BOARD_SEQ.CURRVAL, 0, 0, ?, ?, ?)";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getBtitle());			
			pstmt.setString(2, dto.getBcontent());
			pstmt.setString(3, dto.getBimg());
			pstmt.setString(4, gid);
			pstmt.setString(5, mid);
			pstmt.setString(6, dto.getBip());
			result = pstmt.executeUpdate();
			System.out.println(gid + "게시판에 글쓰기 성공");
		} catch (SQLException e) {
			System.out.println(e.getMessage() + gid + "게시판에 글쓰기 실패 - " + dto);
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
	
	// (9) 특정 게시글을 수정. bno와 dto가 필요.
	public int modifyBoard(int bno, BoardDto dto) {
		int result = FAIL;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "UPDATE BOARD " + 
				"SET BTITLE = ?, BCONTENT = ?, BIMG = ?, BIP=? " + 
				"WHERE BNO = ?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getBtitle());			
			pstmt.setString(2, dto.getBcontent());
			pstmt.setString(3, dto.getBimg());
			pstmt.setString(4, dto.getBip());
			pstmt.setInt(5, bno);			
			result = pstmt.executeUpdate();
			System.out.println(bno + "번 게시글 글수정 완료");
		} catch (SQLException e) {
			System.out.println(e.getMessage() + bno + "번 게시글 글수정 실패 - " + dto);
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
	
	// (10) 특정 게시물 삭제. bno가 필요.
	public int deleteBoard(int bno) {
		int result = FAIL;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM BOARD WHERE BNO= ? ";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bno);
			result = pstmt.executeUpdate();
			System.out.println(result == SUCCESS ? "글삭제완료" : "글 번호오류");
		} catch (SQLException e) {
			System.out.println(e.getMessage() + "글 삭제 실패");
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
	
	// (11) 답변글 작성 전, bgroup이 같고 bstep이 원글보다 큰 (답변글인) 게시글들의 bstep과 bindent를 조정함.
	private void preReplyBoardStep(int bgroup, int bstep) {
		Connection        conn  = null;
		PreparedStatement pstmt = null;
		String sql = "UPDATE BOARD SET BSTEP = BSTEP + 1 WHERE BGROUP=? AND BSTEP>?";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, bgroup);
			pstmt.setInt(2, bstep);
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
	
	// (12) 답변글을 작성.
	public int replyBoard(String gid, String mid, BoardDto dto) {
		int result = FAIL;
		preReplyBoardStep(dto.getBgroup(), dto.getBstep());
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO BOARD "
				+ "(BNO, BTITLE, BCONTENT, BIMG, BGROUP, BSTEP, BINDENT, GID, MID, BIP) " 
				+ "VALUES (BOARD_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getBtitle());			
			pstmt.setString(2, dto.getBcontent());
			pstmt.setString(3, dto.getBimg());
			pstmt.setInt(4, dto.getBgroup());
			pstmt.setInt(5, dto.getBstep()+1);
			pstmt.setInt(6, dto.getBindent()+1);
			pstmt.setString(7, gid);
			pstmt.setString(8, mid);
			pstmt.setString(9, dto.getBip());
			result = pstmt.executeUpdate();
			System.out.println(gid + "게시판에 답변글작성 완료");
		} catch (SQLException e) {
			System.out.println(e.getMessage() + gid + "게시판에 답변글작성 실패 - " + dto);
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