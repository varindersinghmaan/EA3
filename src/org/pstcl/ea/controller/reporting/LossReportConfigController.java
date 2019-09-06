package org.pstcl.ea.controller.reporting;

import java.util.List;

import javax.validation.Valid;

import org.pstcl.ea.dao.MapMonthLossReportLocationDao;
import org.pstcl.ea.entity.mapping.MapMonthLossReportLocation;
import org.pstcl.ea.model.reporting.ReportParametersModel;
import org.pstcl.ea.service.impl.masters.MapMonthLossReportLocationServiceImpl;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LossReportConfigController {

	@Autowired
	MapMonthLossReportLocationServiceImpl mapMonthLossReportLocationService;


	
	
	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = { "/createMapLocationsFromLocationMaster" }, method = { RequestMethod.GET })
	public String createMapLocationsFromLocationMaster(@RequestParam(value = "page",required = false) Integer page,final ModelMap model) {
		mapMonthLossReportLocationService.createMapLocationsFromLocationMaster();
		return "reporting/lossReportLocations";
	}
	
	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = { "/lossReportLocations" }, method = { RequestMethod.GET })
	public String reportDashboard1(@RequestParam(value = "page",required = false) Integer page,final ModelMap model) {
		return "reporting/lossReportLocations";
	}

	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = "/filterLossReportLocations", method = RequestMethod.POST)
	public ModelAndView getDashboardReport(@Valid @RequestBody ReportParametersModel parametersModel , Errors errors,
			ModelMap modelMap) {

		parametersModel=initialiseMonthYear(parametersModel);
		List<MapMonthLossReportLocation> lossReportLocations=mapMonthLossReportLocationService.listLocationsForMonth(parametersModel);
		modelMap.addAttribute("lossReportLocations", lossReportLocations);
		initialiseModelMonthYear(parametersModel,modelMap);

		return new ModelAndView("reporting/lossReportLocationsInsert", modelMap);


	}
	
	private void initialiseModelMonthYear(ReportParametersModel parametersModel, ModelMap modelMap) {
		modelMap.addAttribute("reportMonthYearDate", DateUtil.convertMonthYearToDate(parametersModel.getReportMonth(), parametersModel.getReportYear()));
		modelMap.addAttribute("monthOfReport", parametersModel.getReportMonth());
		modelMap.addAttribute("yearOfReport", parametersModel.getReportYear());
	}
	private ReportParametersModel initialiseMonthYear(ReportParametersModel parametersModel)
	{
		parametersModel=mapMonthLossReportLocationService.initialiseMonthYear(parametersModel);
		if(null==parametersModel.getReportType())
		{
			parametersModel.setReportType(EAUtil.LOSS_REPORT_CRITERIA_G_T);
		}
		return parametersModel;
	}
}
