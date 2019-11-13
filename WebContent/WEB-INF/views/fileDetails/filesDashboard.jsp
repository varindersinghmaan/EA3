
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
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>

<c:set var="ctx" value="${pageContext.servletContext.contextPath}" />


<script type="text/javascript">
	function connect() {

		var socket = new SockJS('${ctx}/gs-guide-websocket');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function(frame) {
			setConnected(true);
			console.log('Connected: ' + frame);
			stompClient.subscribe('/topic/greetings', function(greeting) {
				showGreeting(JSON.parse(greeting.body).content);
			});
		});

		var socket2 = new SockJS('${ctx}/gs-guide-websocket');
		stompClient2 = Stomp.over(socket2);
		stompClient2.connect({}, function(frame) {
			setConnected(true);
			console.log('Connected: ' + frame);
			stompClient2.subscribe('/topic/alerts', function(greeting) {
				showAlert(JSON.parse(greeting.body).content);
			});
		});
		
	}

	var stompClient = null;
	var stompClient2 = null;

	function setConnected(connected) {

		if (connected) {
			$("#conversation").show();
		} else {
			$("#conversation").hide();
		}
		$("#greetings").html("");
	}

	function disconnect() {
		if (stompClient !== null) {
			stompClient.disconnect();
		}
		setConnected(false);
		console.log("Disconnected");
	}

	function sendName() {
		stompClient.send("/app/hello", {}, JSON.stringify({
			'name' : $("#name").val()
		}));
	}

	function showAlert(message) {

		//$("<tr><td>prependTo</td></tr>").prependTo("table > tbody");
		alert( "Alert! "+message );

	}
	function showGreeting(message) {

		//$("<tr><td>prependTo</td></tr>").prependTo("table > tbody");
		$("#greetings").prepend(
				"<tr><td>" + new Date() + "</td><td>" + message + "</td></tr>");

	}
</script>

<script type="text/javascript">
	function applyFilters() {
		showProgressBar();

		
		if ($("#reportDate").val() == "") {
			setDefaultMonth();
		}

		if ($("#reportDate").val() != "") {
			var reportDateArray = $("#reportDate").val().split("-");
			var month = reportDateArray[1];
			var year = reportDateArray[0];

		} else {
			var month = null;
			var year = null;

		}

		var reportParamsModel = {
			"reportMonth" : month,
			"reportYear" : year,
			"reportCategory" : $('input[name=reportCategory]:checked').val()
		};
		return reportParamsModel;

	}

	function loadReport() {
		var reportParamsModel = applyFilters();

		$.ajax({
			url : "${ctx}/getFilesList",
			type : "POST",
			data : JSON.stringify(reportParamsModel),
			contentType : 'application/json',
			success : function(response) {

				$('#reportDiv').css('display', 'none');
				
				hideProgressBar();

				$("#reportDiv").html(response);
				$('#reportDiv').css('display', 'block');

				initDataTables();

			},
			error : function(data, status, er) {
				hideProgressBar();
				alert(" Error" + data + er);
			}
		});

	}

	function populateTableContainer(data, status) {
		$("#reportDiv").html(data);
	}
</script>
<script type="text/javascript">
	function confirm() {
		$('#myModal').modal('toggle');

	}

	function processFiles() {
		$('#myModal').modal('toggle');
		$("#processFiles").prop("disabled", true);
		var reportParamsModel = applyFilters();
		reportParamsModel.reportType = $("#reportFilesAction").val();
		reportParamsModel.reportCategory = $("#reportCateogoryForFilesAction")
				.val();
		$.ajax({
			url : "${ctx}/getFilesList",
			type : "POST",
			data : JSON.stringify(reportParamsModel),
			contentType : 'application/json',
			success : function(response) {
				hideProgressBar();

				$("#reportDiv").html(response);
				$('#reportDiv').css('display', 'block');

				connect();

			},
			error : function(data, status, er) {
				alert("OBJECT Error" + data + er);
			}
		});

	}
</script>
<style>
.btn-primary:not (:disabled ):not (.disabled ).active, .btn-primary:not
	 (:disabled ):not (.disabled ):active, .show>.btn-primary.dropdown-toggle
	{
	color: #fff;
	background-color: #6d2814;
	border-color: #add02e;
}

.btn-primary {
	color: #fff;
	background-color: #007bff;
	border-color: #20f116;
}

.form-group {
	padding: 15px 15px 15px 15px;
}
</style>


</head>
<body>
	<%@include file="../authheader.jsp"%>
	<%@include file="../dataTablesHeader.jsp"%>
	<%@include file="../html/customProgressBarModal.html"%>
	<script type="text/javascript">
		function initDataTables() {

			$('#reportdatatable').DataTable({
				dom : 'Bfrtip',
				"paging" : false,
				"ordering" : false,
				buttons : [ 'excelHtml5', 'csvHtml5', {
					extend : 'pdfHtml5',
					orientation : 'landscape',
					pageSize : 'LEGAL'
				} ],
				orientation : 'landscape',
				pageSize : 'A4'
			});
		}
		function setDefaultMonth() {
			var newdate = new Date();
			newdate.setDate(new Date().getDate() - 30); // minus the date
			document.getElementById("reportDate").value = newdate.getFullYear()
					+ "-" + ("0" + (newdate.getMonth() + 1)).slice(-2);

		}
	</script>
	<script type="text/javascript">
		$(document).ready(function() {
			connect();

			setDefaultMonth();
			$('#loadReportButton').click();

		});
	</script>

	<div>



		<div class="form-group row">
			<sec:authorize
				access="hasAnyRole('ROLE_SLDC_USER','ROLE_SLDC_ADMIN')">


				<input type="hidden" id="reportFilesAction"
					value="<%=EAUtil.EA_ACTION_PROCESS_FILES%>">
				<input type="hidden" id="reportCateogoryForFilesAction"
					value="<%=EAUtil.EA_MONTHLY_FILES_ACTION%>">
			</sec:authorize>
			<div class="form-control col-md-3">
				Month <input id="reportDate" type="month"
					class="form-control input-sm" />
			</div>
			<div class="form-control col-sm-6 ">
				Report Type:
				<div id="intervalDiv" class="btn-group btn-group-toggle"
					data-toggle="buttons">


					<label class="btn btn-secondary"> <input type="radio"
						name="reportCategory" id="option2"
						value="<%=EAUtil.EA_MONTHLY_PENDING_FILES%>" autocomplete="off">
						Pending-Files
					</label> <label class="btn btn-secondary"> <input type="radio"
						name="reportCategory" id="option4"
						value="<%=EAUtil.EA_MONTHLY_REPORT_ALL_FILES%>" autocomplete="off">
						All Files
					</label> <label class="btn btn-secondary"> <input type="radio"
						name="reportCategory" id="option4" checked="checked" value=""
						autocomplete="off"> Current System Status
					</label>

				</div>

			</div>
			<div class="form-control col-sm-3 ">

				<input type="button" onclick="confirm()" id="processFiles"
					value="Process Files" class="btn btn-danger btn-md" />
			</div>


			<div class="float-right">
				<input type="button" onclick="loadReport()" id="loadReportButton"
					value="Load Report" class="btn btn-primary btn-sm" /> <a
					class="btn btn-light btn-sm float-right"
					href="javascript:window.location='reportDashboard'"><span
					class="glyphicon glyphicon-plus"></span>Clear</a>
			</div>

		</div>

		<div id="reportDiv" class="container"></div>

		<div class="modal fade" id="myModal">
			<div class="modal-dialog modal-lg">
				<div class="modal-content"
					style="width: 1000px; align-self: center;">

					<!-- Modal Header -->
					<div class="modal-header">
						<h4 class="modal-title">Files</h4>
						<button type="button" class="close" data-dismiss="modal">&times;</button>
					</div>

					<!-- Modal body -->
					<div class="modal-body">
						<div id="myModalDiv">
							<div class="alert alert-warning" role="alert">
								This action will process all the files that have not been
								processed yet. Are you <b>Sure</b> you want to process files?
								<div class="row">
									<div class="form-control col-sm-6">
										<button onclick="processFiles()" id="processFiles"
											class="close">Yes,Process Files</button>
									</div>
									<div class="form-control col-sm-6">
										<button class="close" data-dismiss="modal">Cancel</button>
									</div>
								</div>
							</div>
						</div>
					</div>


					<!-- Modal footer -->
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary"
							data-dismiss="modal">Close</button>
					</div>

				</div>
			</div>
		</div>
	</div>

</body>
</html>