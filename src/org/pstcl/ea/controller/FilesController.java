package org.pstcl.ea.controller;

import java.util.List;

import javax.validation.Valid;

import org.pstcl.ea.entity.mapping.MapMonthLossReportLocation;
import org.pstcl.ea.model.FileModel;
import org.pstcl.ea.model.reporting.FileParameterModel;
import org.pstcl.ea.model.reporting.ReportParametersModel;
import org.pstcl.ea.service.impl.SubstationDataServiceImpl;
import org.pstcl.ea.service.impl.UploadingService;
import org.pstcl.ea.service.impl.lossreport.IlossReportService;
import org.pstcl.ea.service.impl.parallel.DataService;
import org.pstcl.ea.util.DateUtil;
import org.pstcl.ea.util.EAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class FilesController {

	@Autowired
	private UploadingService restService;


	@Autowired
	private DataService dataService;

	@Autowired
	private SubstationDataServiceImpl substationDataServiceImpl;

	@Autowired
	private IlossReportService lossReportService;
	
	
	
	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = { "/fileDashboard" }, method = { RequestMethod.GET })
	public String reportDashboard1(@RequestParam(value = "page",required = false) Integer page,final ModelMap model) {
		return "fileDetails/filesDashboard";
	}

	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = "/getFilesList", method = RequestMethod.POST)
	public ModelAndView getDashboardReport(@Valid @RequestBody ReportParametersModel parametersModel , Errors errors,
			ModelMap modelMap) {

		parametersModel=initialiseMonthYear(parametersModel);
		initialiseModelMonthYear(parametersModel,modelMap);
		
		ModelAndView mv=null;
		if(null==parametersModel.getReportCategory())
		{
			mv=  new ModelAndView( "fileDetails/processingFilesStatusInsert",modelMap);
		}
		else if(EAUtil.EA_MONTHLY_REPORT_ALL_FILES.equals(parametersModel.getReportCategory()))
		{
			mv=getFilesInRepo(parametersModel,modelMap);
			
				}
		else if(EAUtil.EA_MONTHLY_PENDING_FILES.equals(parametersModel.getReportCategory()))
		{
			mv=pendingRepoFiles(parametersModel,modelMap);
			
			}
		else if(EAUtil.EA_MONTHLY_FILES_ACTION.equals(parametersModel.getReportCategory()))
		{
			if(EAUtil.EA_ACTION_PROCESS_FILES.equalsIgnoreCase(parametersModel.getReportType()))
			{
				mv=processFiles(parametersModel,modelMap);
			}
		}
		
		return mv;


	}
	
	private void initialiseModelMonthYear(ReportParametersModel parametersModel, ModelMap modelMap) {
		modelMap.addAttribute("reportMonthYearDate", DateUtil.convertMonthYearToDate(parametersModel.getReportMonth(), parametersModel.getReportYear()));
		modelMap.addAttribute("monthOfReport", parametersModel.getReportMonth());
		modelMap.addAttribute("yearOfReport", parametersModel.getReportYear());
	}
	private ReportParametersModel initialiseMonthYear(ReportParametersModel parametersModel)
	{
		parametersModel=dataService.initialiseMonthYearForFileDashboard(parametersModel);
		return parametersModel;
	}


	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = "/getLocationFileDetails", method = RequestMethod.POST)
	public String getLocationFileDetails(@Valid @RequestBody FileParameterModel parametersModel , Errors errors,
			ModelMap modelMap,BindingResult bindingResult)  {
		modelMap.addAttribute("locDetails", dataService.getFilesForLocation(parametersModel));
		return "snippets/filesSnippet";
	}













	

	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = "/processFiles", method = RequestMethod.GET)
	private ModelAndView processFiles(ReportParametersModel parametersModel,ModelMap modelMap) {
		dataService.processZipFiles(parametersModel);
	
		return  new ModelAndView( "fileDetails/processingFilesStatusInsert",modelMap);
	}








	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN') or hasRole('ROLE_SE')")
	//@RequestMapping(value = "/pendingRepoFiles", method = RequestMethod.GET)
	private ModelAndView pendingRepoFiles(ReportParametersModel parametersModel,ModelMap modelMap) {
		modelMap.addAttribute("fileModel", dataService.getPendingRepoFiles(parametersModel));
			return new ModelAndView("fileDetails/uploadedFilesDetailInsert",modelMap);
	}




	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN') or hasRole('ROLE_SE')")
//	@RequestMapping(value = "/filesInRepoS", method = RequestMethod.GET)
	private ModelAndView getFilesInRepo(ReportParametersModel reportParametersModel, ModelMap modelMap) {
		modelMap.addAttribute("fileModel", substationDataServiceImpl.getFileInRepo(reportParametersModel));
		return new ModelAndView("fileDetails/uploadedFilesDetailInsert",modelMap);
	}



//
//	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
//	@RequestMapping(value = "/processFileTamper-{id}", method = RequestMethod.GET)
//	public String processFileTamper(@PathVariable Integer id,ModelMap modelMap) {
//		modelMap.addAttribute("cmriModel", dateService.processRepoFileForTamper(id));
//		return "processingFilesStatus";
//	}

	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = "/processRepoFile-{id}", method = RequestMethod.GET)
	public String uploadFile(@PathVariable Integer id,ModelMap modelMap) {
		modelMap.addAttribute("cmriModel", dataService.processRepoFileSerial(id));
		return "processingFilesStatus";
	}


	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = "/uploadMultiZip", method = RequestMethod.GET)
	public String uploadMultiZip(ModelMap modelMap) {
		FileModel fileModel=new FileModel();
		modelMap.addAttribute("fileModel", fileModel);

		return "addCMRIMultiZip";
	}

	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = "/uploadMultiZip", method = RequestMethod.POST)
	public String uploadMultiZip(FileModel fileModel, BindingResult result, ModelMap modelMap) {
		modelMap.addAttribute("fileModel", restService.processMultiZipUploadedFile(fileModel));

		return "fileDetails/pmUploadedFilesDetail";
	}


	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = "/uploadTxtFile", method = RequestMethod.POST)
	public String submit(FileModel fileModel, BindingResult result, ModelMap modelMap) {
		modelMap.addAttribute("file", fileModel.getFile());

		modelMap.addAttribute("fileModel", restService.processUploadedFile(fileModel));
		return "fileDetails/pmUploadedFilesDetail";
	}

	@PreAuthorize("hasRole('ROLE_SLDC_USER') or hasRole('ROLE_SLDC_ADMIN')")
	@RequestMapping(value = "/uploadTxtFile", method = RequestMethod.GET)
	public String uploadTxnFile( ModelMap modelMap) {
		FileModel fileModel=new FileModel();
		modelMap.addAttribute("fileModel", fileModel);
		return "addMeterDataText";
	}






	//uploadFile
}
