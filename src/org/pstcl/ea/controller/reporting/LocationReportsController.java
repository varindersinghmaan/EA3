package org.pstcl.ea.controller.reporting;

import javax.validation.Valid;

import org.pstcl.ea.model.reporting.ReportParametersModel;
import org.pstcl.ea.service.impl.lossreport.ReportService;
import org.pstcl.ea.service.impl.lossreport.TamperInstantRegisterService;
import org.pstcl.ea.util.DateUtil;
import org.pstcl.ea.util.EAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class LocationReportsController {

	@Autowired
	private TamperInstantRegisterService tamperService;

	@Autowired
	private ReportService lossReportService1;

	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = "/locationReports", method = RequestMethod.POST)
	public ModelAndView locationReports(@Valid @RequestBody ReportParametersModel parametersModel, Errors errors,
			ModelMap modelMap) {

		parametersModel = initialiseMonthYear(parametersModel);

		ModelAndView mv = null;
		if (EAUtil.EA_LOCATION_REPORT_INSTANT_REGISTERS.equalsIgnoreCase(parametersModel.getReportType())) {
			mv = getInstantModelView(modelMap, parametersModel);
		} else if (EAUtil.EA_LOCATION_REPORT_TAMPERS.equalsIgnoreCase(parametersModel.getReportType())) {
			mv = getTamperModelView(modelMap, parametersModel);
		} else {
			mv = getEnergyModelView(modelMap, parametersModel);

		}

		return mv;

	}

	private ReportParametersModel initialiseMonthYear(ReportParametersModel parametersModel) {
		parametersModel = tamperService.initialiseMonthYear(parametersModel);
		return parametersModel;
	}

	private ModelAndView getEnergyModelView(ModelMap modelMap, ReportParametersModel parametersModel) {

		intilizeModelMonthYear(modelMap, parametersModel);

		modelMap.addAttribute("locationSurveyDataModel", lossReportService1.getReportTransactions(parametersModel));

		return new ModelAndView("reporting/locationImportExportInsert", modelMap);

	}

	private ModelAndView getTamperModelView(ModelMap modelMap, ReportParametersModel parametersModel) {
		intilizeModelMonthYear(modelMap, parametersModel);

		modelMap.addAttribute("locationSurveyDataModel", tamperService.getTamperReportTransactions(parametersModel));

		return new ModelAndView("tamperReport/locationTampersInsert", modelMap);

	}

	private ModelAndView getInstantModelView(ModelMap modelMap, ReportParametersModel parametersModel) {
		intilizeModelMonthYear(modelMap, parametersModel);

		modelMap.addAttribute("irDetail", tamperService.getIRDetails(parametersModel));
		return new ModelAndView("instantRegisters/locationIRDetailsInsert", modelMap);

	}

	private void intilizeModelMonthYear(ModelMap modelMap, ReportParametersModel parametersModel) {
		modelMap.addAttribute("monthOfReport",
				DateUtil.convertMonthYearToDate(parametersModel.getReportMonth(), parametersModel.getReportYear()));
		modelMap.addAttribute("month", parametersModel.getReportMonth());
		modelMap.addAttribute("year", parametersModel.getReportYear());
	}

}
