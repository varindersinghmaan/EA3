package org.pstcl.ea.service.impl.parallel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.pstcl.ea.dao.MapMeterLocationDao;
import org.pstcl.ea.entity.FileMaster;
import org.pstcl.ea.entity.LocationMaster;
import org.pstcl.ea.entity.MeterMaster;
import org.pstcl.ea.entity.mapping.MapMeterLocation;
import org.pstcl.ea.entity.meterTxnEntity.DailyTransaction;
import org.pstcl.ea.entity.meterTxnEntity.InstantRegisters;
import org.pstcl.ea.entity.meterTxnEntity.TamperLogTransaction;
import org.pstcl.ea.messaging.OutputMessage;
import org.pstcl.ea.model.CMRIFileDataModel;
import org.pstcl.ea.model.FileFilter;
import org.pstcl.ea.model.FileModel;
import org.pstcl.ea.model.LocationFileModel;
import org.pstcl.ea.model.LocationSurveyDataModel;
import org.pstcl.ea.model.reporting.FileParameterModel;
import org.pstcl.ea.model.reporting.ReportParametersModel;
import org.pstcl.ea.service.impl.EnergyAccountsService;
import org.pstcl.ea.service.impl.lossreport.IlossReportService;
import org.pstcl.ea.util.DateUtil;
import org.pstcl.ea.util.EAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;


@Service("dataService")
@EnableAsync
public class DataService extends EnergyAccountsService{

	@Autowired
	private ApplicationContext context;

	@Autowired
	@org.springframework.beans.factory.annotation.Qualifier(value = "eaThreadPoolTaskExecutor")
	private ThreadPoolTaskExecutor taskExecutor; 

	@Autowired
	MapMeterLocationDao mtrLocMapDao;



	@Autowired
	IlossReportService lossReportService;



	public FileModel getCorruptRepoFiles(Integer month, Integer year) {

		FileFilter filter=new FileFilter();
		if(null!=month&&null!=year)
		{
			filter.setTransactionDateFrom(DateUtil.startDateTimeForDailySurveyRecs(month, year));
			filter.setTransactionDateTo(DateUtil.endDateTimeForDailySurveyRecs(month, year));
		}
		List<FileMaster> fileMasters=fileMasterDao.findCorruptFiles(filter);
		FileModel fileModel=new FileModel();
		fileModel.setFilesUploadedDetail(fileMasters);
		return fileModel;
	}


	public LocationFileModel getFilesForLocation(FileParameterModel parametersModel) {

		LocationFileModel locationFileModel=new LocationFileModel();
		locationFileModel.setLocationMaster(parametersModel.getLocation());
		FileFilter filter=new FileFilter();
		filter.setTransactionDateFrom(DateUtil.startDateTimeForDailySurveyRecs(parametersModel.getReportMonth()+1,parametersModel.getReportYear()));
		filter.setTransactionDateTo(DateUtil.endDateTimeForDailySurveyRecs(parametersModel.getReportMonth()+1,parametersModel.getReportYear()));
		filter.setLocation(parametersModel.getLocation());
		filter.setFileActionStatus(EAUtil.FILE_ACTION__APPROVED_AE);
		List<FileMaster> fileMasters=fileMasterDao.filterFiles(filter);
		locationFileModel.setFileMasters(fileMasters);
		return locationFileModel;

	}




	public FileModel getPendingRepoFiles(Integer month, Integer year) {

		FileFilter filter=new FileFilter();
		filter.setTransactionDateFrom(DateUtil.startDateTimeForDailySurveyRecs(month, year));
		filter.setTransactionDateTo(DateUtil.endDateTimeForDailySurveyRecs(month, year));
		filter.setProcessingStatus(EAUtil.FILE_ZIP_EXTRACTED);
		filter.setFileActionStatus(EAUtil.FILE_ACTION__APPROVED_AE);
		List<FileMaster> fileMasters=fileMasterDao.filterFiles(filter);
		FileModel fileModel=new FileModel();
		fileModel.setFilesUploadedDetail(fileMasters);
		return fileModel;
	}









	private void processFileSerial(FileMaster textFile) {
		DataReaderThread dataReaderThread= context.getBean(DataReaderThread.class);

		//dataReaderThread.setTextFile(textFile);
		CMRIFileDataModel cmriFileDataModel=new CMRIFileDataModel();
		cmriFileDataModel.setFileMaster(textFile);

		dataReaderThread.extractTransactionDataFromFile(cmriFileDataModel);

		dataReaderThread.saveLoadDailyFileDataToDB(cmriFileDataModel);
	}




	public CMRIFileDataModel processRepoFileSerial(Integer id) {


		FileMaster fileMaster=fileMasterDao.findById(id);
		CMRIFileDataModel cmriDataModel=new CMRIFileDataModel();
		processFileSerial(fileMaster);
		Integer year = 2018;
		Integer monthOfYear = 9;

		if(null!=fileMaster.getTransactionDate())
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(fileMaster.getTransactionDate());
			year = cal.get(Calendar.YEAR);
			monthOfYear = cal.get(Calendar.MONTH)-1;
		}

		List<DailyTransaction> dailyTransactions=dailyTransactionDao.getDailyTransactionsByMonth(fileMaster.getMeterMaster(),  monthOfYear,year);

		cmriDataModel.setDailyTransactions(dailyTransactions);

		return cmriDataModel;
	}



	@Autowired
	private SimpMessagingTemplate template;

	public String processZipFiles(ReportParametersModel parametersModel)
	{
		Calendar currentMonth= Calendar.getInstance();
		Calendar processingRequestMonth= Calendar.getInstance();

		processingRequestMonth.set(parametersModel.getReportYear(), parametersModel.getReportMonth(), 15);
		if((processingRequestMonth.get(Calendar.MONTH)-currentMonth.get(Calendar.MONTH))==0
				&&((processingRequestMonth.get(Calendar.YEAR)-currentMonth.get(Calendar.YEAR)==0)))
		{


			this.template.convertAndSend("/topic/greetings", new OutputMessage( HtmlUtils.htmlEscape("Starting Processing for " +parametersModel.getReportMonth()+","+parametersModel.getReportYear() + " started processing!") ));

			taskExecutor.execute(new Runnable() {

				@Override
				public void run() {

					Integer month=parametersModel.getReportMonth();
					Integer year2=parametersModel.getReportYear();
					FileFilter filter=new FileFilter();
					filter.setTransactionDateFrom(DateUtil.startDateTimeForDailySurveyRecs(month, year2));
					filter.setTransactionDateTo(DateUtil.endDateTimeForDailySurveyRecs(month, year2));
					filter.setProcessingStatus(EAUtil.FILE_ZIP_EXTRACTED);


					filter.setFileActionStatus(EAUtil.FILE_ACTION__APPROVED_AE);
					List<FileMaster> fileMasters=fileMasterDao.filterFiles(filter);
					//process files in asynchronous mode
					for (FileMaster fileMaster : fileMasters) {
						try {
							processFileAsync(fileMaster);
						}catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			} );
			return null;
		}
		else
		{
			String msg="Can not process data for " +parametersModel.getReportMonth()+","+parametersModel.getReportYear() + ". Contact System Administrator!";
			this.template.convertAndSend("/topic/alerts", new OutputMessage( HtmlUtils.htmlEscape(msg) ));
			return msg;
		}
	}






	private void processFileAsync(FileMaster fileDetails) {
		taskExecutor.execute(new Runnable() {

			@Override
			public void run() {

				DataReaderThread dataReaderThread= context.getBean(DataReaderThread.class);

				CMRIFileDataModel cmriFileDataModel=new CMRIFileDataModel();
				cmriFileDataModel.setFileMaster(fileDetails);

				dataReaderThread.extractTransactionDataFromFile(cmriFileDataModel);
				dataReaderThread.saveLoadDailyFileDataToDB(cmriFileDataModel);

			}
		} );
	}





	public CMRIFileDataModel viewRepoFileData(Integer id) {

		FileMaster fileMaster=fileMasterDao.findById(id);
		DataReaderThread dataReaderThread= context.getBean(DataReaderThread.class);

		CMRIFileDataModel cmriFileDataModel=new CMRIFileDataModel();
		cmriFileDataModel.setFileMaster(fileMaster);
		dataReaderThread.extractTransactionDataFromFile(cmriFileDataModel);

		return	cmriFileDataModel;
	}


	public FileModel getPendingRepoFiles(ReportParametersModel parametersModel) {
		// TODO Auto-generated method stub
		return getPendingRepoFiles(parametersModel.getReportMonth(),parametersModel.getReportYear());
	}

	public ReportParametersModel initialiseMonthYearForFileDashboard(ReportParametersModel parametersModel) {


		if(EAUtil.EA_MONTHLY_FILES_ACTION.equals(parametersModel.getReportCategory()))
		{
			if(EAUtil.EA_ACTION_PROCESS_FILES.equalsIgnoreCase(parametersModel.getReportType()))
			{
				if(null!=parametersModel.getReportYear()&&null!= parametersModel.getReportMonth())
				{
					initialiseMonthYearToPreviousMonth(parametersModel);
					//SUBTRACTING 1 because JAVA DATE API takes month from 0 to 11 and JAVASCRIPT sends from 1 to 12
					Calendar currentMonth= Calendar.getInstance();
					Calendar processingRequestMonth= Calendar.getInstance();
					processingRequestMonth.set(parametersModel.getReportYear(), parametersModel.getReportMonth(), 15);
					if((processingRequestMonth.get(Calendar.MONTH)-currentMonth.get(Calendar.MONTH))!=0
							&&((processingRequestMonth.get(Calendar.YEAR)-currentMonth.get(Calendar.YEAR)!=0)))
					{
						this.template.convertAndSend("/topic/greetings", new OutputMessage( HtmlUtils.htmlEscape("Only the files generated in current month can be processed!") ));

					}

				}
				else
				{
					this.template.convertAndSend("/topic/greetings", new OutputMessage( HtmlUtils.htmlEscape("Month Selection Error") ));
					this.template.convertAndSend("/topic/greetings", new OutputMessage( HtmlUtils.htmlEscape("Please select a valid month") ));

				}

			}
		}
		else
		{
			initialiseMonthYearToPreviousMonth(parametersModel);
		}
		return parametersModel;
	}

















}
