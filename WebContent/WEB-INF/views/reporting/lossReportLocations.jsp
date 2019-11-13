
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
	function applyFilters() {
		showProgressBar();

		$('#reportDiv').css('display', 'none');

		if ($("#reportDate").val() == "") {
			var newdate = new Date();
			newdate.setDate(new Date().getDate() - 5); // minus the date
			document.getElementById("reportDate").value = newdate.getFullYear()
					+ "-" + ("0" + (newdate.getMonth() + 1)).slice(-2);

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
			"reportType" : $('input[name=reportType]:checked').val()
		};
		return reportParamsModel;

	}

	function loadReport() {
		var reportParamsModel = applyFilters();

		$.ajax({
			url : "${ctx}/filterLossReportLocations",
			type : "POST",
			data : JSON.stringify(reportParamsModel),
			contentType : 'application/json',
			success : function(response) {
				$("#reportDiv").html(response);
				$('#reportDiv').css('display', 'block');
				hideProgressBar();

				initDataTables();

			},
			error : function(data, status, er) {
				alert("OBJECT Error" + data + er);
			}
		});

	}

	function populateTableContainer(data, status) {
		$("#reportDiv").html(data);
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

			$('#reportdatatable').DataTable( {
				  "ordering": false
			});

		}

		
		function setDefaultMonth() {
			var newdate = new Date();
			newdate.setDate(new Date().getDate() - 37); // minus the date
			document.getElementById("reportDate").value = newdate.getFullYear()
					+ "-" + ("0" + (newdate.getMonth() + 1)).slice(-2);

		}
	</script>
	<script type="text/javascript">
		$(document).ready(function() {
			setDefaultMonth();
			$('#loadReportButton').click();
			initDataTables();

		});
	</script>

	<div>



		<div class="form-group row">
			<div class="form-control col-md-3">
				Month <input id="reportDate" type="month"
					class="form-control input-sm" />
			</div>
			<div class="form-control col-sm-9 ">
				Report Type:
				<div id="intervalDiv" class="btn-group btn-group-toggle"
					data-toggle="buttons">


					<label class="btn btn-secondary"> <input type="radio"
						name="reportType" id="option3"
						value="<%=EAUtil.LOSS_REPORT_CRITERIA_G_T%>" autocomplete="off">
						(G-T)
					</label> <label class="btn btn-secondary"> <input type="radio"
						name="reportType" id="option3"
						value="<%=EAUtil.LOSS_REPORT_CRITERIA_I_T_%>" autocomplete="off">
						(I-T)
					</label> <label class="btn btn-secondary"> <input type="radio"
						name="reportType" id="option3"
						value="<%=EAUtil.LOSS_REPORT_CRITERIA_T_D_220_66_%>"
						autocomplete="off"> (T-D)(220/66)
					</label> <label class="btn btn-secondary"> <input type="radio"
						name="reportType" id="option3"
						value="<%=EAUtil.LOSS_REPORT_CRITERIA_T_D_132_11_%>"
						autocomplete="off"> (T-D)(132/11)
					</label> <label class="btn btn-secondary"> <input type="radio"
						name="reportType" id="option3"
						value="<%=EAUtil.LOSS_REPORT_CRITERIA_T_D_132_33_%>"
						autocomplete="off"> (T-D)(132/33)
					</label> <label class="btn btn-secondary"> <input type="radio"
						name="reportType" id="option3"
						value="<%=EAUtil.LOSS_REPORT_CRITERIA_T_D_132_66%>"
						autocomplete="off"> (T-D)(132/66)
					</label> <label class="btn btn-secondary"> <input type="radio"
						name="reportType" id="option3"
						value="<%=EAUtil.LOSS_REPORT_CRITERIA_INDEPENDENT_%>"
						autocomplete="off"> (T-D)(Independent)
					</label>


				</div>
			</div>


			<div class="float-right">
				<input type="button" onclick="loadReport()" id="loadReportButton"
					value="Load Report" class="btn btn-primary btn-sm" /> <a
					class="btn btn-light btn-sm float-right"
					href="javascript:window.location='lossReportLocations'"><span
					class="glyphicon glyphicon-plus"></span>Clear</a>
			</div>

		</div>

		<div id="reportDiv" class="container"></div>
	</div>

</body>
</html>