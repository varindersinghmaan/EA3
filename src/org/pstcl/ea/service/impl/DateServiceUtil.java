package org.pstcl.ea.service.impl;

import java.util.Calendar;

import org.pstcl.ea.messaging.OutputMessage;
import org.pstcl.ea.model.reporting.ReportParametersModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.util.HtmlUtils;



public class DateServiceUtil {

	@Autowired
	private SimpMessagingTemplate template;

	public ReportParametersModel initialiseMonthYear(ReportParametersModel parametersModel) {

		Calendar calendar= Calendar.getInstance();

		if(null!=parametersModel.getReportYear()&&null!= parametersModel.getReportMonth())
		{
			//SUBTRACTING 1 because JAVA DATA API takes month from 0 to 11 and JAVASCRIPT sends from 1 to 12
			parametersModel.setReportMonth(parametersModel.getReportMonth()-1);
			calendar.set(parametersModel.getReportYear(), parametersModel.getReportMonth(), 15);
		}
		calendar.add(Calendar.MONTH, -1);
		if (parametersModel.getReportYear() == null) {
			parametersModel.setReportYear(calendar.get(Calendar.YEAR));
		}
		if (parametersModel.getReportMonth() == null) {
			parametersModel.setReportMonth(calendar.get(Calendar.MONTH));
		}
		return parametersModel;
	}

	public ReportParametersModel initialiseMonthYearToPreviousMonth(ReportParametersModel parametersModel) {

		Calendar calendar= Calendar.getInstance();

		if(null!=parametersModel.getReportYear()&&null!= parametersModel.getReportMonth())
		{
			//SUBTRACTING 1 because JAVA DATA API takes month from 0 to 11 and JAVASCRIPT sends from 1 to 12
			parametersModel.setReportMonth(parametersModel.getReportMonth()-1);
			calendar.set(parametersModel.getReportYear(), parametersModel.getReportMonth(), 15);
		}
		//files contain data of previous month, thus file generated in Sept will have Aug data and should be shown when user searches for Aug.
		calendar.add(Calendar.MONTH, 1);
		parametersModel.setReportYear(calendar.get(Calendar.YEAR));
		parametersModel.setReportMonth(calendar.get(Calendar.MONTH));
		return parametersModel;
	}


}
