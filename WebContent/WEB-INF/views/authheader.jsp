<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@include file="header.jsp"%>

<div id="myDiv" class="container-fluid">
	<div id="app" class="container">

		<nav class="navbar navbar-expand-lg navbar-light bg-faded  fixed-top top-navbar">
			<a class="navbar-brand" href="javascript:window.location='home'">
				<img alt="PSTCL"
				src="<c:url value='/static/img/pstcl.png' />">
			</a>

			<button class="navbar-toggler navbar-toggler-right" type="button"
				data-toggle="collapse" data-target="#navbarNavAltMarkup"
				aria-controls="navbarNavAltMarkup" aria-expanded="false"
				aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>
			<div id="navbarNavAltMarkup" class="navbar-collapse collapse">


				<ul class="navbar-nav  mr-auto">
					<li><a class="nav-link"
						href="javascript:window.location='home'"><span
							class="glyphicon glyphicon-plus"></span>Home</a></li>
					<li><a class="nav-link"
						href="javascript:window.location='substationMaster'"><span
							class="glyphicon glyphicon-plus"></span>Energy Meter Master</a></li>
					<sec:authorize
						access="hasAnyRole('ROLE_SLDC_USER','ROLE_SLDC_ADMIN')">

						<li class="nav-item dropdown"><a
							class="nav-link dropdown-toggle" data-toggle="dropdown"
							id="Preview" href="#" role="button" aria-haspopup="true"
							aria-expanded="false"> Meter Mapping </a>
							<div class="dropdown-menu" aria-labelledby="Preview">

								<a class="dropdown-item"
									href="javascript:window.location='addMeter'"> <label
									class="col"><b>Add New Meter</b></label>
								</a> <a class="dropdown-item"
									href="javascript:window.location='addLocation'"> <label
									class="col"><b>Add New Location</b></label>
								</a> <a class="dropdown-item"
									href="javascript:window.location='selectMonthForReportLocations'">
									<label class="col"><b>Add Locations For Monthly
											calculations</b></label>
								</a>

							</div></li>

					</sec:authorize>

				</ul>
				<ul class="navbar-nav">
					<li><span class="navbar-text">
								Logged in User:
								<sec:authentication property="principal.username" />
					</span></li>

					<li class="nav-item"><a class="nav-link"
						href="<c:url value="/logout" />"><span
							class="glyphicon glyphicon-log-out"></span> Logout </a></li>
				</ul>
			</div>

		</nav>
	</div>
</div>