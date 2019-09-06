<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<script type="text/javascript">
	function viewDetails(modalid) {
		$(modelid).modal('toggle');

	}
	function getLocationFileDetails(locationid,month,year) {
		
		var reportParamsModel = {
				"reportMonth" : month,
				"reportYear" : year,
				"location" : {"locationId":locationid}
				
			};
//		"location" : locationid
		$.ajax({
			url : "getLocationFileDetails",
			type : "POST",
			data : JSON.stringify(reportParamsModel),
			contentType : 'application/json',
			success : function(response) {
				$('#myModalDiv').html(response);

				$('#myModal').modal('toggle');
				
			},
			error : function(data, status, er) {
				alert("Error" + data + er);
			}
		});

	}
</script>



<script type="text/javascript">
		$(document).ready(function() {

			$('.modal-content').resizable({
				//alsoResize: ".modal-dialog",
				minHeight : 300,
				minWidth : 600
			});
			$('.modal-dialog').draggable();
		});
	</script>





<div class="container-fluid">
	<c:if test="${not empty success}">
		<div class="alert alert-success lead">${success}</div>
	</c:if>
	<span class="lead">Locations for which no data is available for
		the month of <fmt:formatDate value="${reportMonthYearDate}"
			pattern="MMM,yyyy" />
	</span>
	<div class="card"></div>
	<div class="table-responsive">

		<table id="reportdatatable"
			class="table  table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th>Sr. No.</th>
					<th style="width: 150px;">Substation</th>

					<th style="width: 150px;">Location</th>
					<th style="width: 150px;">Meter Details</th>
					<th>Boundary</th>
					<th>File Details</th>

					<sec:authorize
						access="hasAnyRole('ROLE_SLDC_USER','ROLE_SLDC_ADMIN')">
						<th>Add Data Manually</th>
					</sec:authorize>


				</tr>
			</thead>
			<tbody>
				<c:forEach items="${pendingLocList}" var="locDetails"
					varStatus="indexStatus">
					<tr>
						<td>${indexStatus.index+1 }</td>
						<td>${locDetails.locationMaster.substationMaster.stationName}</td>
						<td>${locDetails.locationMaster.locationId}</td>

						<td><%@include file="locationMeterDetails.jsp"%>
						<td>${locDetails.locationMaster.boundaryTypeMaster.boundaryType}</td>
						<td>
							<button
								onclick="getLocationFileDetails('${locDetails.locationMaster.locationId}',${monthOfReport},${yearOfReport})">View
								CMRI Files</button>
						</td>


						<sec:authorize
							access="hasAnyRole('ROLE_SLDC_USER','ROLE_SLDC_ADMIN')">
							<td><a title="${locDetails.locationMaster.locationId}"
								href="javascript:window.location='addPendingLocData-${locDetails.locationMaster.locationId}?month=${monthOfReport}&year=${yearOfReport}'">
									Add Data </a></td>
						</sec:authorize>
					</tr>


				</c:forEach>
			</tbody>
		</table>



	</div>
</div>

<div class="modal fade" id="myModal">
	<div class="modal-dialog modal-lg">
		<div class="modal-content" style="width: 1000px; align-self: center;">

			<!-- Modal Header -->
			<div class="modal-header">
				<h4 class="modal-title">Files</h4>
				<button type="button" class="close" data-dismiss="modal">&times;</button>
			</div>

			<!-- Modal body -->
			<div class="modal-body">
				<div id="myModalDiv"></div>
			</div>


			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
			</div>

		</div>
	</div>
</div>

