<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="sv" id="rhsErrorApp" ng-app="rhsErrorApp">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=Edge"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="ROBOTS" content="nofollow, noindex"/>
<title>Rehabstöd</title>
<!-- build:css({build/.tmp,src/main/webapp}) app/app.css -->
<!-- injector:css -->
<link rel="stylesheet" href="/app/app.css">
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

<!-- Angular stuff only for making Protractor behave -->
<script type="text/javascript" src="/bower_components/angular/angular.min.js"></script>
<script type="text/javascript">
  angular.module('rhsErrorApp', [
    'rhsErrorApp.controllers'
  ]);
  angular.module('rhsErrorApp.controllers', []).
  controller('errorController', function($scope) {

  });
</script>
</head>
<body ng-controller="errorController">
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

          <c:when test="${param.reason eq \"inactivity-timeout\"}">
            <h1 class="page-header">Rehabstöd</h1>
            <div class="alert alert-warning">Du har blivit automatiskt utloggad eftersom du varit inaktiv en längre tid. Gå tillbaka till <a href="/">startsidan</a> för att logga in igen.</div>
          </c:when>

          <c:when test="${param.reason eq \"denied\"}">
            <h1 class="page-header">Rehabstöd</h1>
            <div class="alert alert-danger">Åtkomst nekad. Gå tillbaka till <a href="/">startsidan</a>.</div>
          </c:when>

          <c:when test="${param.reason eq \"notfound\"}">
            <h1 class="page-header">Rehabstöd</h1>
            <div class="alert alert-danger">Sidan finns inte. Gå tillbaka till <a href="/">startsidan</a>.</div>
          </c:when>

          <c:when test="${param.reason eq \"login.medarbetaruppdrag\"}">
            <h1 class="page-header">Rehabstöd - medarbetaruppdrag saknas</h1>
            <div id="error-medarbetaruppdrag-saknas" class="alert alert-danger">Det krävs minst ett giltigt medarbetaruppdrag med ändamål 'Vård och behandling' för att använda Rehabstöd.</div>
          </c:when>

          <c:when test="${param.reason eq \"login.hsaerror\"}">
            <h1 class="page-header">Rehabstöd - tekniskt fel</h1>
            <div id="error-tekniskt-fel" class="alert alert-danger">Tyvärr har ett tekniskt problem uppstått i Rehabstöd. <a href="/index.html">Försök gärna igen</a> för att se om felet är tillfälligt. Kontakta annars i första hand din lokala IT-avdelning och i andra hand <a href="http://www.inera.se/felanmalan" target="_blank">Ineras kundservice</a>.
            </div>
          </c:when>
          <c:when test="${param.reason eq \"login.saknar-hsa-rehabroll\"}">
            <h1 class="page-header">Rehabstöd - Behörighet saknas</h1>
            <div id="error-tekniskt-fel" class="alert alert-danger">För att logga in som Rehabkoordinator krävs att du har den rollen för enheten i HSA.</div>
            </div>
          </c:when>
          <c:when test="${param.reason eq \"exporterror\"}">
            <h1 class="page-header">Rehabstöd - export misslyckades</h1>
            <div id="error-export-fel" class="alert alert-danger">Tyvärr uppstod ett fel vid skapandet av exporten. Du kan försöka utföra exporten igen för att se om felet är tillfälligt. Kontakta annars i första hand din lokala IT-avdelning och i andra hand <a href="http://www.inera.se/felanmalan" target="_blank">Ineras kundservice</a>.
            </div>
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
