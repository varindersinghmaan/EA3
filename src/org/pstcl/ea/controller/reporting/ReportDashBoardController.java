package org.pstcl.ea.controller.reporting;

import java.util.Date;

import javax.validation.Valid;

import org.pstcl.ea.model.reporting.ReportParametersModel;
import org.pstcl.ea.service.impl.SubstationDataServiceImpl;
import org.pstcl.ea.service.impl.lossreport.ReportService;
import org.pstcl.ea.service.impl.lossreport.TamperInstantRegisterService;
import org.pstcl.ea.util.DateUtil;
import org.pstcl.ea.util.EAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class ReportDashBoardController {

	@Autowired
	private TamperInstantRegisterService tamperAndIRService;
	

	@Autowired
	private ReportService reportService;

	

	@Autowired
	private SubstationDataServiceImpl substationDataService;

	
	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = { "/reportDashboard" }, method = { RequestMethod.GET })
	public String reportDashboard1(@RequestParam(value = "page",required = false) Integer page,final ModelMap model) {
		
		
		if(null!=page)
		{
			if(1==page)
			{
				model.addAttribute("reportType",EAUtil.EA_MONTHLY_REPORT_PENDING_METERS);
			
			}
			else if(2==page)
			{
				model.addAttribute("reportType",EAUtil.EA_MONTHLY_REPORT_TAMPER_COUNT);
			}
			else if(3==page)
			{
				model.addAttribute("reportType",EAUtil.EA_MONTHLY_REPORT_INSTANT_REGISTERS);
			}
		}
		
		return "reporting/reportDashboard";
	}

	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = "/getDashboardReport", method = RequestMethod.POST)
	public ModelAndView getDashboardReport(@Valid @RequestBody ReportParametersModel parametersModel , Errors errors,
			ModelMap modelMap) {

		parametersModel=initialiseMonthYear(parametersModel);
		ModelAndView mv=null;
		if(EAUtil.EA_MONTHLY_REPORT_TAMPER_COUNT.equals(parametersModel.getReportCategory()))
		{
			mv=getTamperLossReport(parametersModel,modelMap);
		}
		else if(EAUtil.EA_MONTHLY_REPORT_TAMPERS.equals(parametersModel.getReportCategory()))
		{
			mv=getAllLocationTampers(parametersModel, modelMap);
		}
		else if(EAUtil.EA_MONTHLY_REPORT_INSTANT_REGISTERS.equals(parametersModel.getReportCategory()))
		{
			mv=getIRDetails(parametersModel, modelMap);
		}
		else if(EAUtil.EA_MONTHLY_REPORT_PENDING_METERS.equals(parametersModel.getReportCategory()))
		{
			mv=getPendingLossReportLocation(parametersModel, modelMap);
		}


		return mv;


	}

	private ModelAndView getTamperLossReport(ReportParametersModel parametersModel , 
			ModelMap modelMap) {


		modelMap.addAttribute("tamperDetailsProjectionModel",
				tamperAndIRService.getTamperDetailsProjectionReport(parametersModel));
		initialiseModelMonthYear(parametersModel, modelMap);
		return new ModelAndView("tamperReport/monthlyTamperListInsert",modelMap);
	}


	private ModelAndView getAllLocationTampers(ReportParametersModel parametersModel , 
			ModelMap modelMap) {


		initialiseModelMonthYear(parametersModel, modelMap);

		modelMap.addAttribute("locationSurveyDataModel", tamperAndIRService.getTamperReportTransactions(parametersModel));

		return new ModelAndView("tamperReport/locationTampersInsert",modelMap);
	}

	private void initialiseModelMonthYear(ReportParametersModel parametersModel, ModelMap modelMap) {
		modelMap.addAttribute("reportMonthYearDate", DateUtil.convertMonthYearToDate(parametersModel.getReportMonth(), parametersModel.getReportYear()));
		modelMap.addAttribute("monthOfReport", parametersModel.getReportMonth());
		modelMap.addAttribute("yearOfReport", parametersModel.getReportYear());
	}



	private ModelAndView getIRDetails(ReportParametersModel parametersModel , 
			ModelMap modelMap) {

		initialiseModelMonthYear(parametersModel, modelMap);
		modelMap.addAttribute("iRDetails", tamperAndIRService.getIRDetail( parametersModel));
		return new ModelAndView( "instantRegisters/iRDetails",modelMap);
	}


	private ReportParametersModel initialiseMonthYear(ReportParametersModel parametersModel)
	{
		parametersModel=tamperAndIRService.initialiseMonthYear(parametersModel);
		return parametersModel;
	}

	
	
	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	public ModelAndView getPendingLossReportLocation(
			ReportParametersModel parametersModel , 
			ModelMap modelMap) {

		initialiseModelMonthYear(parametersModel, modelMap);
		modelMap.addAttribute("pendingLocList", substationDataService.getPendingLossReportLocation(parametersModel));
		return new ModelAndView(  "pendingDataLocations/pendingLossReportLocationsInsert",modelMap);
	}
	
	
	@PreAuthorize("hasRole('ROLE_SS_JE') or hasRole('ROLE_SS_AE') or hasRole('ROLE_SR_XEN') or hasRole('ROLE_SE')")
	public String getPendingLossReportLocationPM(@RequestParam(value = "month") Integer month,
			@RequestParam(value = "year") Integer year,ModelMap modelMap) {
		modelMap.addAttribute("pendingLocList", substationDataService.getPendingPMLocations(null,month,year));
		modelMap.addAttribute("monthOfReport",DateUtil.convertMonthYearToDate(month, year) );
		return "pendingDataLocations/pendingLossReportLocationsInsert";
	}

	

	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = "/getLocationInstantRegisters-{locationId}", method = RequestMethod.GET)
	public String getLocationInstantRegisters(@PathVariable String locationId,@RequestParam(value = "month") Integer month,
			@RequestParam(value = "year") Integer year,ModelMap modelMap) {

		modelMap.addAttribute("monthOfReport",DateUtil.convertMonthYearToDate(month, year) );
		modelMap.addAttribute("month",month);
		modelMap.addAttribute("year",year);
		modelMap.addAttribute("irDetail", tamperAndIRService.getIRDetails(locationId,month,year));

		return "instantRegisters/locationIRDetails";
	}

	//leevansha start//
	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = "/getLocationTampers-{locationId}", method = RequestMethod.GET)
	public String getLocationTampers(@PathVariable String locationId,@RequestParam(value = "month") Integer month,
			@RequestParam(value = "year") Integer year,ModelMap modelMap) {

		modelMap.addAttribute("monthOfReport",DateUtil.convertMonthYearToDate(month, year) );

		modelMap.addAttribute("month",month);
		modelMap.addAttribute("year",year);

		modelMap.addAttribute("locationSurveyDataModel", tamperAndIRService.getTamperReportTransactions(locationId,month,year));

		return "tamperReport/locationTampers";
	}

}
