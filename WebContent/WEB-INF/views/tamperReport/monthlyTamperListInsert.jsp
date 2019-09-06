<%@page import="org.pstcl.ea.util.EAUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<div class="card container">
<div id="da111ilyTxnTable_wrapper" class="table-responsive container">

	<span class="lead">Tamper Report Data for the month of <fmt:formatDate
			value="${reportMonthYearDate}" pattern="MMM,yyyy" />
	</span>

	<table id="reportdatatable"
		class="table table-striped table-bordered table-hover">

		<thead>


			<tr>




				<th>Sr. No.</th>

				<th>Location</th>
				<th>Tamper Count</th>
				<th>Station Name</th>
				<th>Division Name</th>
				<th>Circle Name</th>
				<th>Boundary Type</th>
				<th>Device Type</th>
				<th>Feeder Name</th>
				<th>Meter</th>


			</tr>
		</thead>
		<tbody>
			<c:forEach items="${ tamperDetailsProjectionModel}"
				var="tamperDetails" varStatus="indexStatus">

				<tr>
					<td>${indexStatus.index +1}</td>

					<td><a
						href="javascript:window.location='getLocationTampers-${tamperDetails.locationMaster.locationId}?month=${monthOfReport}&year=${yearOfReport}'">

							${tamperDetails.locationMaster.locationId} </a></td>
					<td>${tamperDetails.count}</td>

					<td>${tamperDetails.locationMaster.substationMaster.stationName}</td>
					<td>${tamperDetails.locationMaster.substationMaster.divisionMaster.divisionname}</td>

					<td>${tamperDetails.locationMaster.circleMaster.circleName}</td>
					<td>${tamperDetails.locationMaster.boundaryTypeMaster.boundaryType}
					</td>
					<td>${tamperDetails.locationMaster.deviceTypeMaster.deviceType}</td>

					<td>${tamperDetails.locationMaster.feederName}</td>
					<td>Location:${tamperDetails.locationMaster.feederName}
						${tamperDetails.locationMaster.locationId}</td>

				</tr>

			</c:forEach>
		</tbody>
	</table>

</div>
</div>

