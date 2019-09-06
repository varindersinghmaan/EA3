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
			url : "dsdfjk",
			type : "POST",
			data : JSON.stringify(reportParamsModel),
			contentType : 'application/json',
			success : function(response) {
				$('#myModalDiv').html(response);

				$('#myModal').modal('toggle');
				
			},
			error : function(data, status, er) {
				alert("Functionality is under constrution" );
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
	
	<div class="card">
	<div class="table-responsive">

		<table id="reportdatatable"
			class="table  table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th style="width: 150px;">Substation</th>
					<th style="width: 150px;">Location</th>

					<th>Feeder Name</th>
					<th>Report Criteria</th>
					<th>Report Sr No</th>
					<th>Action</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${lossReportLocations}" var="lossReportLocation"
					varStatus="indexStatus">
					<tr>
						<td>${lossReportLocation.location.substationMaster.stationName}</td>
						<td>${lossReportLocation.location.locationId}</td>
						<td>${lossReportLocation.location.feederName}</td>
						<td>${lossReportLocation.lossReportCriteria}</td>
						<td>${lossReportLocation.lossReportOrder}</td>


						<td><c:choose>
								<c:when test="${null eq lossReportLocation.lossReportCriteria}">
									<button
										onclick="getLocationFileDetails('${locDetails.locationMaster.locationId}',${monthOfReport},${yearOfReport})">Add
										Location</button>

								</c:when>
								<c:otherwise>
									<button
										onclick="getLocationFileDetails('${locDetails.locationMaster.locationId}',${monthOfReport},${yearOfReport})">Remove
										Location</button>
								</c:otherwise>
							</c:choose></td>


					</tr>


				</c:forEach>
			</tbody>
		</table>

</div>

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

