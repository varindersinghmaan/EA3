package org.pstcl.ea.model.reporting;

public class ReportParametersModel {
	private String locationId;
	private String reportType;
	private Integer reportCategory;
	private Integer reportMonth;
	private Integer reportYear;
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public Integer getReportMonth() {
		return reportMonth;
	}
	public void setReportMonth(Integer month) {
		this.reportMonth = month;
	}
	public Integer getReportYear() {
		return reportYear;
	}
	public void setReportYear(Integer year) {
		this.reportYear = year;
	}
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public Integer getReportCategory() {
		return reportCategory;
	}
	public void setReportCategory(Integer reportCategory) {
		this.reportCategory = reportCategory;
	}
	

}
