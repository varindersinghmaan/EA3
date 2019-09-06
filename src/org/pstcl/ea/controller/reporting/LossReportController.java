package org.pstcl.ea.controller.reporting;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.pstcl.ea.model.reporting.ReportParametersModel;
import org.pstcl.ea.service.impl.lossreport.ExcelGeneratorService2;
import org.pstcl.ea.service.impl.lossreport.IlossReportService;
import org.pstcl.ea.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.sun.xml.internal.ws.policy.sourcemodel.ModelNode;

import jdk.internal.org.objectweb.asm.tree.IntInsnNode;


@Controller
@RequestMapping("/")
public class LossReportController {

	@Autowired
	private IlossReportService lossReportService;
	

	@Autowired
	private ExcelGeneratorService2 excelGeneratorService;

	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = {"/lossReportDashBoard", "/lossReportDyn" }, method = { RequestMethod.GET })
	public String reportsHome(final ModelMap model) {
		return "reporting/lossReportDashboard";
	}
	





	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = "/lossReportExl", method = RequestMethod.POST)
	public ResponseEntity<InputStreamResource> lossReportExl(ReportParametersModel parametersModel , BindingResult bindingResult,
			ModelMap modelMap) throws IOException{
		ByteArrayInputStream in = excelGeneratorService.getReportExcel(parametersModel);
		// return IOUtils.toByteArray(in);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=LossReportPstcl.xlsx");

		headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		return ResponseEntity
				.ok()
				.headers(headers)
				.body(new InputStreamResource(in));
	}

	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = "/lossReportDashBoard", method = RequestMethod.POST)
	public ModelAndView lossReportDyn(@Valid @RequestBody ReportParametersModel parametersModel , Errors errors,
			ModelMap modelMap) {

		parametersModel=initialiseMonthYear(parametersModel);

		if  (StringUtils.isNotEmpty(parametersModel.getReportType())) {
			return getReportDetails(parametersModel, modelMap);
		} else {
			return getLossReport(parametersModel, modelMap);
		
		}
	}
	
	private ReportParametersModel initialiseMonthYear(ReportParametersModel parametersModel)
	{
	

		parametersModel=lossReportService.initialiseMonthYear(parametersModel);
		return parametersModel;
	}


	private ModelAndView getReportDetails(ReportParametersModel reportParametersModel, ModelMap modelMap) {
		
		modelMap.addAttribute("lossReportModel", lossReportService.getReport(reportParametersModel.getReportType(), reportParametersModel.getReportMonth(), reportParametersModel.getReportYear()));
		initializeModelMonthYear(reportParametersModel, modelMap);

		return new ModelAndView("reporting/lossReportInsert",modelMap);
	}

	
	private ModelAndView getLossReport(ReportParametersModel reportParametersModel, ModelMap modelMap) {
		
		
		modelMap.addAttribute("consolidatedLossReportModel",
				lossReportService.getConsolidatedMonthlyLossReport(reportParametersModel.getReportMonth(), reportParametersModel.getReportYear(),false));
		initializeModelMonthYear(reportParametersModel, modelMap);
		return new ModelAndView("reporting/lossReportConsolidatedInsert",modelMap);
	}









	private void initializeModelMonthYear(ReportParametersModel parametersModel, ModelMap modelMap) {
		modelMap.addAttribute("reportMonthYearDate", DateUtil.convertMonthYearToDate(parametersModel.getReportMonth(), parametersModel.getReportYear()));
		modelMap.addAttribute("monthOfReport", parametersModel.getReportMonth());
		modelMap.addAttribute("yearOfReport", parametersModel.getReportYear());
		}


}
