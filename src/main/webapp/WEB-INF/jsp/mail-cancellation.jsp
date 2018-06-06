<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<!doctype html>
<html>
<head>
<meta charset="utf-8">
<title>Welcome to TelAppoint's Online NotificationDesk</title>
<link href="static/css/global.css" rel="stylesheet" type="text/css">
</head>
<body>
	<div id="wrapper">
		<!-- Header starts here -->
		<header id="branding">

			<c:if test="${!imagExists}">
			<h1 class="logo">
				<img src="<%=request.getContextPath()%>/static/images/${clientCode}.jpg"
					width="249" height="90">
			</h1>			
			</c:if>		
			<c:if test="${!imagExists}">
			<br><br>		
			</c:if>		
		</header>
		<!-- Header ends here -->
		<!-- Page section starts -->

			<!-- Page Header ends -->

			<!-- Page Container starts -->
			<div id="PageContainer">
				<!-- Page Content starts -->
				<div id="PageContentFull">

					<c:choose>
						<c:when
							test="${emailSMSStatusResponse!=null && status eq \"Success\"}">
							<h1 class="required">${emailSMSStatusResponse.pageEmailHeader}</h1>
							<div class="content">
								<p>${ emailSMSStatusResponse.pageContent}</p>
							</div>
						</c:when>
						<c:when
							test="${status eq \"failure\"}">
							<h1 class="required">${emailSMSStatusResponse.pageEmailHeader}</h1>
							<div class="content">
								<p>${emailSMSStatusResponse.pageContent}</p>
							</div>
						</c:when>
					</c:choose>
					<!-- Page Content ends -->

				</div>
			</div>

			<!-- Page Container ends -->
			<!-- Footer starts -->
			<footer id="footer">
				<div class="footerContent">
					<p>
						&copy; 2013. TelAppoint. All Rights Reserved<br /> Version 6.0
					</p>
				</div>
				<div class="footerLinks">
					Powered By<br> <a href="https://www.telappoint.com"><img
						src="<%=request.getContextPath()%>/static/images/TelAppoint-logo.png"
						width="225" height="57" alt="TelAppoint"></a>
				</div>
			</footer>
			<!-- Footer ends -->
		</div>
		<!-- Page section ends -->
	</div>
</body>
</html>

