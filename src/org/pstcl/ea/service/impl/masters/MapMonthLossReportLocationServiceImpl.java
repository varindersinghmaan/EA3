package org.pstcl.ea.service.impl.masters;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.pstcl.ea.dao.ILocationMasterDao;
import org.pstcl.ea.dao.MapMonthLossReportLocationDao;
import org.pstcl.ea.entity.LocationMaster;
import org.pstcl.ea.entity.mapping.MapMonthLossReportLocation;
import org.pstcl.ea.model.reporting.ReportParametersModel;
import org.pstcl.ea.service.impl.DateServiceUtil;
import org.pstcl.ea.util.EAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value="sldcTxnManager")
public class MapMonthLossReportLocationServiceImpl extends DateServiceUtil {

	@Autowired
	private MapMonthLossReportLocationDao mapMonthLossReportLocationDao;

	public MapMonthLossReportLocation addLocationStartingWithMonth(
			MapMonthLossReportLocation mapMonthLossReportLocation, Boolean include) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 14);

		Calendar lossReportInclusionMonth = Calendar.getInstance();
		lossReportInclusionMonth.set(mapMonthLossReportLocation.getYear(), mapMonthLossReportLocation.getMonth(), 15);

		for (; calendar.compareTo(lossReportInclusionMonth) < 0; calendar.add(Calendar.MONTH, 1)) {

			if (include) {
				addLocationForMonth(mapMonthLossReportLocation, calendar.get(Calendar.MONTH),
						calendar.get(Calendar.YEAR));
			} else {
				removeLocationForMonth(mapMonthLossReportLocation, calendar.get(Calendar.MONTH),
						calendar.get(Calendar.YEAR));

			}
		}
		return mapMonthLossReportLocationDao.findByFilterCriteria(mapMonthLossReportLocation);
	}

	private void addLocationForMonth(MapMonthLossReportLocation mapMonthLossReportLocation, Integer month,
			Integer year) {
		if (null != mapMonthLossReportLocation) {
			if (null != mapMonthLossReportLocation.getLocation()) {
				MapMonthLossReportLocation filter = new MapMonthLossReportLocation();
				filter.setLocation(mapMonthLossReportLocation.getLocation());
				filter.setMonth(month);
				filter.setYear(year);

				MapMonthLossReportLocation entity = mapMonthLossReportLocationDao.findByFilterCriteria(filter);
				if (entity == null) {
					entity = new MapMonthLossReportLocation();
					entity.setLocation(mapMonthLossReportLocation.getLocation());
				}
				entity.setLossReportCriteria(mapMonthLossReportLocation.getLossReportCriteria());
				entity.setLossReportInclusion(EAUtil.LOSS_REPORT_LOC_INCLUDED_X);

				if (null == mapMonthLossReportLocation.getLossReportOrder()) {
					filter.setLossReportCriteria(mapMonthLossReportLocation.getLossReportCriteria());
					mapMonthLossReportLocation
					.setLossReportOrder(mapMonthLossReportLocationDao.getMaxLossReportOrder(filter));
				} else {

				}
				entity.setLossReportOrder(mapMonthLossReportLocation.getLossReportOrder());
				mapMonthLossReportLocationDao.saveOrUpdate(mapMonthLossReportLocation, null);
			}
		}
	}

	private void removeLocationForMonth(MapMonthLossReportLocation mapMonthLossReportLocation, Integer month,
			Integer year) {
		MapMonthLossReportLocation filter = new MapMonthLossReportLocation();
		filter.setLocation(mapMonthLossReportLocation.getLocation());
		filter.setMonth(month);
		filter.setYear(year);

		MapMonthLossReportLocation entity = mapMonthLossReportLocationDao.findByFilterCriteria(filter);
		if (entity == null) {
			entity = new MapMonthLossReportLocation();
			entity.setLocation(mapMonthLossReportLocation.getLocation());
		}
		entity.setLossReportCriteria(null);
		entity.setLossReportInclusion(null);
		entity.setLossReportOrder(null);

		mapMonthLossReportLocationDao.saveOrUpdate(mapMonthLossReportLocation, null);
	}

	public List<MapMonthLossReportLocation> listLocationsForMonth(ReportParametersModel parametersModel) {

		MapMonthLossReportLocation filter = new MapMonthLossReportLocation();
		filter.setLossReportCriteria(parametersModel.getReportType());
		filter.setMonth(parametersModel.getReportMonth());
		filter.setYear(parametersModel.getReportYear());
		return mapMonthLossReportLocationDao.findAllByCriteria(filter);

	}



	public void createLossReportLocationsForCurrentMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
		Calendar cuurentDate = Calendar.getInstance();
		createLossReportLocationsForMonth(calendar,cuurentDate);
	}



	@Autowired
	private ILocationMasterDao locationMasterDao;


	public void createMapLocationsFromLocationMaster() {


		Calendar targetMonth = Calendar.getInstance();
		targetMonth.set(Calendar.MONTH, 5);
		targetMonth.set(Calendar.YEAR, 2019);
		Boolean dataExists=checkLossReportLocationsForMonth(targetMonth);
		if(!dataExists)
		{
			List<LocationMaster> list = locationMasterDao.findAllLocationMasters();
			Integer month= targetMonth.get(Calendar.MONTH);
			Integer year=targetMonth.get(Calendar.YEAR);
			for (LocationMaster locationMaster : list) {
				MapMonthLossReportLocation currentMonthEntity=new MapMonthLossReportLocation(null,month,year,locationMaster,locationMaster.getLossReportCriteria(),locationMaster.getLossReportInclusion(),locationMaster.getLossReportOrder());;

				currentMonthEntity.setId(null);

				mapMonthLossReportLocationDao.saveOrUpdate(currentMonthEntity, null);
			}
		}
		targetMonth.set(Calendar.MONTH, 6);
		targetMonth.set(Calendar.YEAR, 2019);
		
		Calendar calendar = Calendar.getInstance();
		Calendar sourceMonth =null;
		for (; targetMonth.compareTo(calendar) < 0; targetMonth.add(Calendar.MONTH, 1)) {
			sourceMonth=(Calendar) targetMonth.clone();
			sourceMonth.add(Calendar.MONTH,-1);
			createLossReportLocationsForMonth(sourceMonth,targetMonth);	
		}

	}

	
	
	

	@Transactional(value="sldcTxnManager")
	public void createLossReportLocationsForMonth(Calendar sourceMonth,Calendar targetMonth) {
		if(!checkLossReportLocationsForMonth(targetMonth))
		{
			MapMonthLossReportLocation filter = new MapMonthLossReportLocation();
			
			filter.setMonth(sourceMonth.get(Calendar.MONTH));
			filter.setYear(sourceMonth.get(Calendar.YEAR));

			System.out.println("Source month:"+sourceMonth.getTime()+     "     "+filter);
			System.out.println("Target month:"+targetMonth.getTime()+     "     ");

			List<MapMonthLossReportLocation> list = mapMonthLossReportLocationDao.findAllByCriteria(filter);
			Integer month= targetMonth.get(Calendar.MONTH);
			Integer year=targetMonth.get(Calendar.YEAR);
			for (MapMonthLossReportLocation previousMonthEntity : list) {

				MapMonthLossReportLocation currentMonthEntity=new MapMonthLossReportLocation(null,month,year,previousMonthEntity.getLocation(),previousMonthEntity.getLossReportCriteria(),previousMonthEntity.getLossReportInclusion(),previousMonthEntity.getLossReportOrder());;
				mapMonthLossReportLocationDao.saveOrUpdate(currentMonthEntity, null);
			}
		}
	}

	private Boolean checkLossReportLocationsForMonth(Calendar monthToVerify) {
		MapMonthLossReportLocation filter = new MapMonthLossReportLocation();

		filter.setMonth(monthToVerify.get(Calendar.MONTH));
		filter.setYear(monthToVerify.get(Calendar.YEAR));
		List<MapMonthLossReportLocation> list = mapMonthLossReportLocationDao.findAllByCriteria(filter);
		if(CollectionUtils.isNotEmpty(list))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
