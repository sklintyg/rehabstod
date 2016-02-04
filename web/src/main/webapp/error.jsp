<%@ page import="org.springframework.core.env.Environment" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="sv">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="ROBOTS" content="nofollow, noindex"/>
<title>Rehabstöd</title>
<!-- build:css({build/.tmp,src/main/webapp}) app/app.css -->
<!-- injector:css -->
<link rel="stylesheet" href="app/app.css">
<!-- endinjector -->
<!-- endbuild -->
<style>
  h1 {
    font-family: 'Helvetica Neue', helvetica, arial, sans-serif;
    font-size: 24px;
    font-weight: bold;
    color: #008391;
  }
</style>
</head>
<body>
  <div class="container">
    <div class="row">
      <div class="content">
        <c:choose>
          <c:when test="${param.reason eq \"logout\"}">
            <h1 class="page-header">Du är utloggad</h1>
            <p>Din webbläsare kan hålla kvar information även efter utloggningen. Du bör därför stänga samtliga öppna webbläsarfönster.</p>
          </c:when>

          <c:when test="${param.reason eq \"login.failed\"}">
            <h1 class="page-header">Rehabstöd</h1>
            <div class="alert alert-danger">Inloggningen misslyckades. Gå tillbaka till <a href="/">startsidan</a>.</div>
          </c:when>

          <c:when test="${param.reason eq \"denied\"}">
            <h1 class="page-header">Rehabstöd</h1>
            <div class="alert alert-danger">Åtkomst nekad. Gå tillbaka till <a href="/">startsidan</a>.</div>
          </c:when>

          <c:when test="${param.reason eq \"notfound\"}">
            <h1 class="page-header">Rehabstöd</h1>
            <div class="alert alert-danger">Sidan finns inte. Gå tillbaka till <a href="/">startsidan</a>.</div>
          </c:when>

          <c:otherwise>
            <h1 class="page-header">Rehabstöd</h1>
            <div class="alert alert-danger">Ett tekniskt fel har uppstått. Gå tillbaka till <a href="/">startsidan</a>.</div>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>
</body>
</html>
