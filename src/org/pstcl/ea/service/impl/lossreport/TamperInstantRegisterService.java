package org.pstcl.ea.service.impl.lossreport;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.pstcl.ea.dao.IInstantRegistersDao;
import org.pstcl.ea.dao.ILocationMasterDao;
import org.pstcl.ea.dao.ITamperLogDao;
import org.pstcl.ea.entity.LocationMaster;
import org.pstcl.ea.entity.meterTxnEntity.InstantRegisters;
import org.pstcl.ea.model.LocationSurveyDataModel;
import org.pstcl.ea.model.reporting.ReportParametersModel;
import org.pstcl.ea.model.reporting.TamperDetailsProjectionEntity;
import org.pstcl.ea.service.impl.DateServiceUtil;
import org.pstcl.ea.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TamperInstantRegisterService extends DateServiceUtil {

	@Autowired
	ITamperLogDao tamperLogDao;
	
	@Autowired
	IInstantRegistersDao instantRegistersDao;
	
	@Autowired
	protected ILocationMasterDao locationMasterDao;
	
	

	@Autowired
	protected ITamperLogDao tamperLogTransactionDao;
	/* (non-Javadoc)
	 * @see org.pstcl.ea.service.impl.IlossReport#getIRDetails(java.lang.String, int, int)
	 */


	public InstantRegisters getIRDetails(String locationId,int month,int year){
		InstantRegisters ir = instantRegistersDao.findInstantRegistersByDayAndLocation(locationId, month, year);
		return ir;
	}

	public List<InstantRegisters> getIRDetailForMonth(Integer month,Integer year){
		List<InstantRegisters> irList = instantRegistersDao.findAllByMonth( month, year);
		return irList;
	}

	public InstantRegisters getIRDetails(ReportParametersModel parametersModel) {
		// TODO Auto-generated method stub
		return getIRDetails(parametersModel.getLocationId(),parametersModel.getReportMonth(),parametersModel.getReportYear());
	}

	
	//tamper loss month year report 
	/* (non-Javadoc)
	 * @see org.pstcl.ea.service.impl.IlossReport#getTamperDetailsProjectionReport(int, int)
	 */

	public List<TamperDetailsProjectionEntity> getTamperDetailsProjectionReport(int month,int year){
		Date startDate=DateUtil.startDateTimeForDailySurveyRecs(month, year);
		Date endDate=DateUtil.endDateTimeForDailySurveyRecs(month, year);
		return getTamperDetailsProjectionDateRangeReport(startDate, endDate);	
	}

	//tamper loss date range report
	/* (non-Javadoc)
	 * @see org.pstcl.ea.service.impl.IlossReport#getTamperDetailsProjectionDateRangeReport(java.util.Date, java.util.Date)
	 */

	public List<TamperDetailsProjectionEntity> getTamperDetailsProjectionDateRangeReport(Date startDate, Date endDate){
		List<TamperDetailsProjectionEntity> results =  tamperLogDao.getTamperLogTransactionsCountByDateRange(startDate, endDate);
		return results;
	}
	


	public LocationSurveyDataModel getTamperReportTransactions(ReportParametersModel parametersModel) {

		return getTamperReportTransactions(parametersModel.getLocationId(),parametersModel.getReportMonth(),parametersModel.getReportYear());

	}


	public LocationSurveyDataModel getTamperReportTransactions(String locationId, int month, int year) {

		LocationSurveyDataModel locationSurveyDataModel=new LocationSurveyDataModel();
		if(locationId!=null) {
			LocationMaster locationMaster=locationMasterDao.findById(locationId);
			locationSurveyDataModel.setLocationMaster(locationMaster);
			locationSurveyDataModel.setTamperLogTransactions(tamperLogTransactionDao.getTamperLogTransactionsByMonth(locationMaster, month, year));
		}
		else {
			locationSurveyDataModel.setTamperLogTransactions(tamperLogTransactionDao.getTamperLogTransactionsByMonth(null,month, year));
		}
		return locationSurveyDataModel;
	}

	public List<TamperDetailsProjectionEntity>  getTamperDetailsProjectionReport(ReportParametersModel parametersModel) {
		// TODO Auto-generated method stub
		return getTamperDetailsProjectionReport(parametersModel.getReportMonth(),parametersModel.getReportYear());
	}

	public List<InstantRegisters>  getIRDetail(ReportParametersModel parametersModel) {
		// month for IR is the month in which the file was read from the meter. Thus for a loss report of JAN, IR will be read in early FEB. Thus date for IR should lie in FEB
		//adding month to date for the same reason
		Calendar calendar= Calendar.getInstance();
		calendar.set(parametersModel.getReportYear(), parametersModel.getReportMonth(), 15);
		calendar.add(Calendar.MONTH, 1);
		Integer month=calendar.get(Calendar.MONTH);
		Integer year=calendar.get(Calendar.YEAR);
		
		return getIRDetailForMonth(month,year);
	}

}
