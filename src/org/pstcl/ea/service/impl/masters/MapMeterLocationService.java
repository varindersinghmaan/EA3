package org.pstcl.ea.service.impl.masters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.pstcl.ea.dao.IDailyTransactionDao;
import org.pstcl.ea.dao.ILocationEMFDao;
import org.pstcl.ea.dao.MapMeterLocationDao;
import org.pstcl.ea.entity.LocationMaster;
import org.pstcl.ea.entity.SubstationMaster;
import org.pstcl.ea.entity.mapping.LocationMFMap;
import org.pstcl.ea.entity.mapping.MapMeterLocation;
import org.pstcl.ea.entity.meterTxnEntity.DailyTransaction;
import org.pstcl.ea.entity.meterTxnEntity.DailyTransactionMappedSuper;
import org.pstcl.ea.model.EAFilter;
import org.pstcl.ea.model.SubstationMeter;
import org.pstcl.ea.model.mapping.LocationMeterMappingModel;
import org.pstcl.ea.service.impl.SubstationDataServiceImpl;
import org.pstcl.ea.service.impl.parallel.CalculationMappingUtil;
import org.pstcl.ea.util.DateUtil;
import org.pstcl.ea.util.EAUtil;
import org.pstcl.model.FilterModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@org.springframework.transaction.annotation.Transactional(value="sldcTxnManager")
public class MapMeterLocationService extends CalculationMappingUtil{

	@Autowired
	private SubstationDataServiceImpl substationDataService;


	@Autowired
	private MapMeterLocationDao mtrLocMapDao;

	@Autowired
	private ILocationEMFDao locEmfDao;

	@Autowired
	public
	IDailyTransactionDao dailyTransactionDao;



	public MapMeterLocation getMeterDetails(int id) {
		return mtrLocMapDao.findById(id);

	}

	public List<SubstationMeter> findSubstationEnergyMeters(FilterModel filterModel) {
		EAFilter eaModel = new EAFilter();
		eaModel.setCircle(filterModel.getSelectedCircle());
		eaModel.setDivision(filterModel.getSelectedDivision());
		eaModel.setSubstation(filterModel.getSelectedSubstation());
		List<SubstationMaster> submasters = substationDataService.getSubstationList(eaModel);
		List<SubstationMeter> substationMeterList = new ArrayList<SubstationMeter>();
		for (SubstationMaster submaster : submasters) {

			SubstationMeter substationMeter = new SubstationMeter();
			eaModel.setSubstation(submaster);
			List<MapMeterLocation> substationMeterLocMapList = mtrLocMapDao.findMeterLocationMapBySubstation(eaModel);
			substationMeter.setMtrLocMap(substationMeterLocMapList);
			substationMeter.setSubstationMaster(submaster);

			try {
				substationMeterList.add(substationMeter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return substationMeterList;
	}
	@org.springframework.transaction.annotation.Transactional(value="sldcTxnManager")
	public List<MapMeterLocation> getMeterLocMapByLocationID(String locationid)

	{
		List<MapMeterLocation> meterLocationMappingList = mtrLocMapDao.findMeterLocationMapByLoc(locationid);
		return meterLocationMappingList;
	}

	/**
	 * Check Save details of changed location meter mapping
	 * 
	 * @param locationMeterMappingModel
	 * @return
	 */
	@org.springframework.transaction.annotation.Transactional(value="sldcTxnManager")
	public LocationMeterMappingModel saveLocationMeterMapping(LocationMeterMappingModel locationMeterMappingModel) {
		LocationMeterMappingModel resultModel=new LocationMeterMappingModel();

		try {
		
			if (locationMeterMappingModel.getLocation() != null && locationMeterMappingModel.getStartDate() != null) {
				MapMeterLocation newMtrLocMap = new MapMeterLocation();
				newMtrLocMap.setLocationMaster(locationMeterMappingModel.getLocation());

				newMtrLocMap.setMeterMaster(locationMeterMappingModel.getMeterMaster());
				newMtrLocMap.setStartDate(locationMeterMappingModel.getStartDate());
				if (mtrLocMapDao.find(newMtrLocMap) == false) {
					mtrLocMapDao.save(newMtrLocMap, null);
					modifyTxnsDueToMeterMapping(newMtrLocMap,resultModel);
				}
			}

			resultModel.setMappingSuccesful(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
		resultModel.setMappingSuccesful(false);
		return resultModel;

	}


	private void modifyTxnsDueToMeterMapping(MapMeterLocation newMtrLocMap, LocationMeterMappingModel resultModel) {
		modifyDailyAsMeterMapping(newMtrLocMap, resultModel);
		
	}

	public LocationMeterMappingModel endLocationMeterMapping(LocationMeterMappingModel locationMeterMappingModel) {
		LocationMeterMappingModel resultModel=new LocationMeterMappingModel();
		MapMeterLocation oldMtrLocMap = locationMeterMappingModel.getOldMeterLocationMap();
		if (oldMtrLocMap != null) {
			if (locationMeterMappingModel.getEndDate() != null) {
				oldMtrLocMap.setEndDate(locationMeterMappingModel.getEndDate());
			}
			mtrLocMapDao.update(oldMtrLocMap, null);
			modifyTxnsDueToMeterMapping(oldMtrLocMap,resultModel);
		}
		resultModel.setMeterMaster(locationMeterMappingModel.getMeterMaster());
		resultModel.setMappingSuccesful(true);
		return resultModel;

	}

	public String validateDates(LocationMeterMappingModel locationMeterMap) {
		Date startDate = locationMeterMap.getStartDate();
		String error = null;
		List<MapMeterLocation> list= mtrLocMapDao.findMappingHistory(locationMeterMap.getMeterMaster(),locationMeterMap.getLocation());

		if(startDate==null)
		{
			error="Start Date can not be null";

		}
		else if(locationMeterMap.getLocation()==null)
		{
			error="Location can not be null";
		}
		else
		{
			for (MapMeterLocation meterLocationMap : list) {
				if(startDate.before(meterLocationMap.getEndDate())||startDate.before(meterLocationMap.getStartDate()))
				{
					error="Start Date at new location can not be lesser than end date of a previous location";
				}
			}

		}

		return error;
	}

	public String validateEndDate(LocationMeterMappingModel locationMeterMap) {

		Date endDate = locationMeterMap.getEndDate();
		Date current = new Date();
		String error = null;
		if (endDate == null) {
			error = "End date is Null";
		}
		else if (endDate.after(current)) {
			error = "Set End Date is greater than current Date";
		} 
		else if (endDate.before(locationMeterMap.getOldMeterLocationMap().getStartDate())) 
		{
			error = "Start Date of Meter At Old Location is After End Date at Old Location";
		}  
		return error;

	}

	protected void modifyDailyAsMeterMapping(MapMeterLocation meterLocationMap, LocationMeterMappingModel locationMeterMappingModel) {
		List<DailyTransaction> list=new ArrayList<DailyTransaction>();
		Date startDateOfMonth =	null;
		List<MapMeterLocation> mtrLocMapList = null;
		List<LocationMFMap> locationEMFList=null;
		List<DailyTransaction> dailyTransactions=null;
		String remarks=null;
		Integer transactionStatus=EAUtil.DAILY_TRANSACTION_MODFIED_DUE_TO_METER_MAPPING;

		if(null==meterLocationMap.getEndDate())
		{
			//When new mapping is added, transactions if any by the newly added meter are assigned to the new location
			startDateOfMonth =	DateUtil.startOfTheDay(meterLocationMap.getStartDate());
			mtrLocMapList =  mtrLocMapDao.getLocationByMeterAndDate(meterLocationMap.getMeterMaster(),null);
			locationEMFList=locEmfDao.findLocationEmfByLocAndDate(mtrLocMapList, null);
			dailyTransactions=dailyTransactionDao.getDailyTransactionsByMeterGreaterThanDate(meterLocationMap.getMeterMaster(),startDateOfMonth);
			remarks=" : Modified due to Meter Mapping";
			transactionStatus=EAUtil.DAILY_TRANSACTION_MODFIED_DUE_TO_METER_MAPPING;

		}
		else if(null!=meterLocationMap.getEndDate())
		{
			//When a mapping is ended, i.e. when a meter is removed, transactions if any by the meter are removed from the DailyTransaction Table and kept in DailyTransactionRemovedMeters
			startDateOfMonth=DateUtil.startOfTheDay(meterLocationMap.getEndDate());
			dailyTransactions=dailyTransactionDao.getDailyTransactionsByMeterGreaterThanDate(meterLocationMap.getMeterMaster(),startDateOfMonth);
			remarks=" : Removed due to Meter Mapping";
			transactionStatus=EAUtil.DAILY_TRANSACTION_MODFIED_DUE_TO_METER_MAPPING;
		}
		if(CollectionUtils.isNotEmpty(dailyTransactions))
		{
			for (DailyTransaction dailyTransaction : dailyTransactions) {
				setDailyTxnLocationAndMF(mtrLocMapList,locationEMFList, dailyTransaction);
				calculateImportExport(dailyTransaction);
				dailyTransaction.setRemarks(dailyTransaction.getRemarks()+remarks);
				dailyTransaction.setTransactionStatus(transactionStatus);
				list.add(dailyTransaction);
			}
			dailyTransactionDao.updateAll(list, null);
		}
		if(null!=locationMeterMappingModel)
		{
			locationMeterMappingModel.setDailyTransactions(list);
		}
	}


	public LocationMeterMappingModel removeMaps() {
		LocationMeterMappingModel resultModel= new LocationMeterMappingModel();
		MapMeterLocation mapMeterLocation=new MapMeterLocation();
		Calendar calendar=java.util.Calendar.getInstance();
		calendar.set(2019, 7, 1);
		mapMeterLocation.setEndDate(calendar.getTime());
		List<MapMeterLocation> list= mtrLocMapDao.findMapAfterDate(mapMeterLocation);
		for (MapMeterLocation mapMeterLocation2 : list) {
			modifyTxnsDueToMeterMapping(mapMeterLocation2, resultModel);
		}
		return	resultModel;
	}

	public LocationMeterMappingModel addMaps() {
		LocationMeterMappingModel resultModel= new LocationMeterMappingModel();
		MapMeterLocation mapMeterLocation=new MapMeterLocation();
		Calendar calendar=java.util.Calendar.getInstance();
		calendar.set(2019, 7, 1);
		mapMeterLocation.setStartDate(calendar.getTime());
		List<MapMeterLocation> list= mtrLocMapDao.findMapAfterDate(mapMeterLocation);
		for (MapMeterLocation mapMeterLocation2 : list) {
			modifyTxnsDueToMeterMapping(mapMeterLocation2, resultModel);
		}
		return resultModel;
	}


}
