<%@ page contentType="text/html;charset=UTF-8" language="java"
	session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html lang="sv">
<head>
<title>Privatläkarportalen - Health Check</title>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<meta name="ROBOTS" content="nofollow, noindex" />

<link rel="stylesheet" href="/bower_components/bootstrap/dist/css/bootstrap.min.css" />
<link rel="stylesheet"href="/bower_components/bootstrap/dist/css/bootstrap-theme.min.css">
</head>
<body>
	<div class="container">
		<div class="page-header">
			<h1>Privatläkarportalen - HealthCheck</h1>
		</div>

		<c:set var="dbStatus" value="${healthcheck.checkDB()}" />
		<c:set var="hsaStatus" value="${healthcheck.checkHSA()}" />
		<c:set var="uptime" value="${healthcheck.checkUptimeAsString()}" />
		<c:set var="nbrUsers" value="${healthcheck.checkNbrOfUsers()}" />
		<c:set var="nbrUsedHsaId" value="${healthcheck.checkNbrOfUsedHsaId()}" />

		<div class="table-responsive">
			<table class="table table-bordered table-striped">
				<thead>
					<tr>
						<th>Check</th>
						<th>Tid</th>
						<th>Status</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>Koppling databas</td>
						<td id="dbMeasurement">${dbStatus.measurement}ms</td>
						<td id="dbStatus" class="${dbStatus.ok ? "text-success" : "text-danger"}">${dbStatus.ok ? "OK" : "FAIL"}</td>
					</tr>
					<tr>
						<td>Koppling HSA</td>
						<td id="hsaMeasurement">${hsaStatus.measurement}ms</td>
						<td id="hsaStatus" class="${hsaStatus.ok ? "text-success" : "text-danger"}">${hsaStatus.ok ? "OK" : "FAIL"}</td>
					</tr>
					<tr>
						<td>Antal inloggade användare:</td>
						<td id="nbrUsersMeasurement" colspan="2">${nbrUsers.measurement}</td>
					</tr>
					<tr>
						<td>Antal använda HSA-id</td>
						<td id="nbrUsedHsaId" colspan="2">${nbrUsedHsaId.measurement}</td>
					</tr>
					<tr>
						<td>Applikationens upptid</td>
						<td id="uptime" colspan="2">${uptime}</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>
