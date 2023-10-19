<%@page import="java.util.Date"%>
<%@page import="javafx.collections.transformation.SortedList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var = "conPath" value="${pageContext.request.contextPath }"/>    
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Game-i</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <script type="text/javascript" src="http://code.jquery.com/jquery-latest.js"></script>    
    <link href="${conPath }/css/main.css" rel = "stylesheet">
    <link rel="icon" type="image/x-icon" href="${conPath }/img/logo4.gif" sizes="144x144">
    <script src = "${conPath }/main/js/main.js"></script>        
</head>
<body>
<script>
  $(document).ready(function() {
    $(".navbar-toggler").click(function() {
      $(".collapse.navbar-collapse").toggleClass("show");
    });
  });  
</script>
<script>
	function noImage(imageElement) {
	 	imageElement.src = "${conPath }/img/noimg.jpg";
	}
</script>
<c:if test = "${not empty loginResult }">	
	<script>
		alert('${loginResult}');	
	</script>
	</c:if>
<c:if test = "${not empty loginErrorMsg }">
	<script>
		alert('${loginErrorMsg}');
		history.back();
	</script>
</c:if>		
<c:if test = "${not empty logoutMsg }">
	<script>
		alert('${logoutMsg }');		
	</script>	
</c:if>	
<jsp:include page="header.jsp"/>
<jsp:include page="rightArea.jsp"/>

  <!-- 좌측 평점목록 -->
  <div id = "left_content">
  	<div class = "article">
  		<table>
	  		<tr>
	  			<c:forEach var = "articlesDto" items="${articles }">
	  			<td><a href = "${conPath }/review.do?gid=${articlesDto.link1 }"><img src = "${conPath }/img/${articlesDto.img1 }" width = "248"></a></td>	  			
	  			</c:forEach>
	  		</tr>
	  		<tr>
	  			<td><br></td>	  				
	  		</tr>	
	  	</table>
	</div> 	
  	<div class = "mainTitle">
  	<h2>&nbsp; &nbsp; 게임 리스트 </h2>
  	</div>
  	<form action = "${conPath }/main.do" class = "listSort">    
	    <select id = "selectBox" name = "sortBy">
      	<option value = "">정렬방식</option>
      	<c:if test = "${sortBy eq 'new' }">
      	<option value = "new" selected = "selected"> 출시일순 </option>
      	<option value = "highScore"> 평점 높은 순</option>
      	</c:if>      
      	<c:if test = "${sortBy eq 'highScore' or empty sortBy }">
      	<option value = "new"> 출시일순 </option>
      	<option value = "highScore" selected = "selected"> 평점 높은 순</option>
      	</c:if>
    	</select>
    	<input class = "sortSubmit" type = "submit" value = "적용">
    </form>    
    <c:set var = "idx" value = "1"/>
    <c:forEach var = "sortedList" items="${listSortby }">        	
    <div class="card mb-1 bg-dark container card1 card-container" style="max-width: 1000px;">
        <div class="row g-0">
          <div class="col-md-3">          	
          	<c:if test = "${empty pageNum or pageNum eq 1}">
            <img class = "rank" src = "${conPath }/img/rank${idx }.png" alt = "rank">
            </c:if>
            <img src="${conPath }/img/${sortedList.gicon }" class="img-fluid rounded-start" alt="thumnail" onerror="noImage(this)">
          </div>
          <div class="col-md-8">
            <div class="card-body">
              <h5 class="card-title" style = "color:aqua;">&nbsp;&nbsp;${sortedList.gname }</h5>              
              <h6 style = "color:white;">&nbsp;&nbsp;(평점: <span style = color:red;>${sortedList.avg }</span>점
              <c:forEach begin="1" end="${fn:substringBefore(sortedList.avg, '.')}">
                <img src = "${conPath }/img/star_on.png" height="15px">
              </c:forEach> 
              <c:forEach begin="1" end ="${5 - fn:substringBefore(sortedList.avg, '.')}">
				<img src = "${conPath }/img/star_out.png" height="15px">
			  </c:forEach>
				)</h6>
              <p class="card-text1 card-text" style = "color:white;">장르: ${sortedList.ggenre } / 개발사: ${sortedList.gpub }</p>
              <c:set var = "currentDate" value = "<%=new Date()%>"/>
              <c:set var = "timeGap" value = "${sortedList.gpdate.time - currentDate.time }"/>
              <c:choose>              
              	<c:when test = "${timeGap >= 0}">
             	 <p class="card-text2 card-text" style = "color:white;">출시일: ${sortedList.gpdate } (출시까지 ${timeGap/86400000}일 남음)</p>              
              	</c:when> 
              	<c:when test = "${timeGap < 0}">
             	 <p class="card-text2 card-text" style = "color:white;">출시일: ${sortedList.gpdate } (출시됨)</p>              
              	</c:when>
              </c:choose>	
              <p class="card-text3 card-text card-intro" style = "color:white;">${sortedList.gdesc }</p>
              &nbsp;&nbsp;<a href="${conPath }/review.do?gid=${sortedList.gid }" class="btn btn-primary">리뷰/평가</a> <a href="${conPath }/boardList.do?gid=${sortedList.gid }" class="btn btn-primary">게시판</a>
            </div>
          </div>
        </div>
      </div>   
      <c:set var="idx" value = "${idx +1 }"/>
      </c:forEach>       
   </div>       
	<div id = "bottom">      
  		<div class = "paging">
    	<table>
    		<tr>
    		<c:if test = "${startPage > BLOCKSIZE }">
    		<td><a href = "${conPath }/main.do?sortBy=${sortBy }&pageNum=${startPage -1 }">[이전]</a></td>
    		</c:if>
    		<td></td>
    		<c:forEach var = "i" begin = "${startPage }" end = "${endPage }">
    			<c:if test = "${i eq pageNum }">
    				<td><b style = "color: red;">${i }</b></td>
    			</c:if>
    			<c:if test = "${i != pageNum }">
    				<td><a href = "${conPath }/main.do?sortBy=${sortBy }&pageNum=${i }" style = "color: white; padding-left: 10px;">${i }</a></td>
   				</c:if> 				
    		</c:forEach>
    		<c:if test = "${endPage < pageCnt }">
    		<td><a href = "${conPath }/main.do?sortBy=${sortBy }&pageNum=${endPage +1 }">[다음]</a></td>
    		</c:if>
    		</tr>
  		</table>  
  		</div>   
	</div>  
    <script>                
        window.addEventListener('DOMContentLoaded', function() {                    
            var cardText3Elements = document.querySelectorAll('p[class="card-intro"]');                            
            cardText3Elements.forEach(function(element) {                
                element.textContent = element.textContent.trim();
            });
        });
    </script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>	    
</body>
</html>
<jsp:include page="footer.jsp"/>