<%@ page contentType="text/html;charset=UTF-8" language="java"
	session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html lang="sv">
<head>
<title>Rehabstöd - Health Check</title>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<meta name="ROBOTS" content="nofollow, noindex" />

<link rel="stylesheet" href="/bower_components/bootstrap/dist/css/bootstrap.min.css" />
<link rel="stylesheet"href="/bower_components/bootstrap/dist/css/bootstrap-theme.min.css">
</head>
<body>
	<div class="container">
		<div class="page-header">
			<h1>Rehabstöd - HealthCheck</h1>
		</div>

		<c:set var="hsaStatus" value="${healthcheck.checkHSA()}" />
		<c:set var="amqStatus" value="${healthcheck.checkActiveMQ()}" />
		<c:set var="logQueueStatus" value="${healthcheck.checkPdlLogQueue()}" />
		<c:set var="logAggregatedQueueStatus" value="${healthcheck.checkPdlAggregatedLogQueue()}" />
		<c:set var="itStatus" value="${healthcheck.checkIntygstjansten()}" />
		<c:set var="uptime" value="${healthcheck.checkUptimeAsString()}" />
		<c:set var="nbrUsers" value="${healthcheck.checkNbrOfUsers()}" />


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
						<td>Koppling NTjP (HSA, LogSender)</td>
						<!-- <td>Ej implementerat</td>     -->
						<td id="hsaMeasurement">${hsaStatus.measurement}ms</td>
						<td id="hsaStatus" class="${hsaStatus.ok ? "text-success" : "text-danger"}">${hsaStatus.ok ? "OK" : "FAIL"}</td>
					</tr>
					<tr>
						<td>Koppling Intygstjänsten</td>
						<td id="itMeasurement">${itStatus.measurement}ms</td>
						<td id="itStatus" class="${itStatus.ok ? "text-success" : "text-danger"}">${itStatus.ok ? "OK" : "FAIL"}</td>
					</tr>
					<tr>
						<td>Koppling ActiveMQ</td>
						<td id="amqMeasurement">${amqStatus.measurement}ms</td>
						<td id="amqStatus" class="${amqStatus.ok ? "text-success" : "text-danger"}">${amqStatus.ok ? "OK" : "FAIL"}</td>
					</tr>
					<tr>
						<td>Köstatus logging.queue</td>
						<td id="logQueueMeasurement" colspan="2">${logQueueStatus.measurement} st</td>
					</tr>
					<tr>
						<td>Köstatus aggregated.logging.queue</td>
						<td id="logAggregatedQueueMeasurement" colspan="2">${logAggregatedQueueStatus.measurement} st</td>
					</tr>

					<tr>
						<td>Antal inloggade användare:</td>
						<td id="nbrUsersMeasurement" colspan="2">${nbrUsers.measurement}</td>
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
