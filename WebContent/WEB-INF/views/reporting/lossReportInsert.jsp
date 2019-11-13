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


<div class="container-fluid">


	<span class="lead">Report Data for ${lossReportModel.criteria}
		for the month of <fmt:formatDate value="${reportMonthYearDate}"
			pattern="MMM,yyyy" />
	</span>
	<div class="card"></div>
	<div id="da111ilyTxnTable_wrapper" class="table-responsive ">


		<table id="dailyTxnTable"
			class="table table-striped table-bordered table-hover">

			<thead>
				<tr>


					<th>Serial No</th>
					<th>Location Id</th>
					<th>Utility Name</th>
					<th>Boundary Type</th>
					<th>Station Name</th>
					<th>Line/Transformer Name</th>
					<th>Voltage Level</th>
					<th>Meter Serial No.</th>
					<th>Meter Category</th>
					<th>External MF</th>
					<th>Sign</th>
					<th style="align-content: right;">Import Wh</th>


					<th style="align-content: flex-end;">Export Wh</th>


					<th>Energy Imported at Boundary point (MWh)</th>
					<th>Energy Exported at Boundary point (MWh)</th>


					
					<th width="100">Remarks (Missing Import Export Data)</th>


				</tr>
			</thead>
			<tbody>
				<c:forEach items="${lossReportModel.lossReportEntities}"
					var="lossReportEntity" varStatus="indexStatus">




					<c:choose>
						<c:when test="${ lossReportEntity.location != null}">

							<c:choose>
								<c:when
									test="${ lossReportEntity.exportWHFCount < lossReportEntity.daysInMonthCount}">
									<tr class="bg-warning">
										<td><i class="fa fa-warning"
											style="font-size: 18px; color: red;">

												${lossReportEntity.location.lossReportOrder} </i></td>
								</c:when>
								<c:otherwise>
									<tr>
										<td>${lossReportEntity.location.lossReportOrder}</td>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<tr>
								<td></td>
						</c:otherwise>
					</c:choose>
					<td><c:if test="${ lossReportEntity.location != null}">

							<a  target="_blank" 
								href="getReportTransactions-${lossReportEntity.location.locationId}?month=${monthOfReport}&year=${yearOfReport}">

								${lossReportEntity.location.locationId} </a>
							<a  target="_blank" 
								href="getLocationTampers-${lossReportEntity.location.locationId}?month=${monthOfReport}&year=${yearOfReport}">

								(Tamper Log) </a>
<br>
							<a  target="_blank" 
								href="locationHome-${lossReportEntity.location.locationId}">

								Location Details</a>

						</c:if> <c:if test="${ lossReportEntity.location == null}">
									TOTAL
									</c:if></td>



					<td>${lossReportEntity.location.utiltiyName}</td>
					<td>${lossReportEntity.location.boundaryTypeMaster.boundaryType}</td>
					<td>${lossReportEntity.location.substationMaster.stationName}</td>
					<td>${lossReportEntity.location.feederName}</td>
					<td>${lossReportEntity.location.voltageLevel}</td>
					<td>${lossReportEntity.meterSrNo}</td>

					<td>${lossReportEntity.meterCategory}</td>
					<td>${lossReportEntity.externalMF}</td>
					<td>${lossReportEntity.netWHSign}</td>


					<th style="text-align: right">${lossReportEntity.importWHFSum}</th>
					<th style="text-align: right">${lossReportEntity.exportWHFSum}</th>

					<th style="text-align: right">${lossReportEntity.importBoundaryPtMWH}</th>
					<th style="text-align: right">${lossReportEntity.exportBoundaryPtMWH}</th>

				

					<td width="400">
					<c:if test="${ lossReportEntity.exportWHFCount < lossReportEntity.daysInMonthCount}">
					Missing Data:(${lossReportEntity.daysInMonthCount-lossReportEntity.exportWHFCount}) Days 
					</c:if>
					</td>

					</tr>


				</c:forEach>
			</tbody>
		</table>
	</div>
</div>




