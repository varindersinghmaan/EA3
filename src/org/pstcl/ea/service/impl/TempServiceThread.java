package org.pstcl.ea.service.impl;

import java.util.List;

import org.pstcl.ea.dao.IDailyTransactionDao;
import org.pstcl.ea.dao.ILocationEMFDao;
import org.pstcl.ea.dao.ILocationMasterDao;
import org.pstcl.ea.dao.MapMeterLocationDao;
import org.pstcl.ea.entity.LocationMaster;
import org.pstcl.ea.entity.mapping.LocationMFMap;
import org.pstcl.ea.entity.mapping.MapMeterLocation;
import org.pstcl.ea.entity.meterTxnEntity.DailyTransaction;
import org.pstcl.ea.service.impl.parallel.CalculationMappingUtil;
import org.pstcl.ea.util.EAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.icu.math.BigDecimal;
@Component
@Scope("prototype")

public class TempServiceThread {
	@Autowired
	public IDailyTransactionDao dailyTransactionDao;
	@Autowired
	public MapMeterLocationDao mtrLocMappingDao;
	@Autowired
	public ILocationEMFDao locEmfDao;

	public TempServiceThread() {
	}

	@Transactional(value="sldcTxnManager")
	public void saveDailyTransactions(LocationMaster locationMaster) {
		//CalculationMappingUtil is not a typical service class because it is never instantiated other than this.
		//USed as a part of DataReaderThread
		CalculationMappingUtil calculationMappingUtil=new CalculationMappingUtil();
		List<DailyTransaction> dailyTransactions=dailyTransactionDao.findAllAnyLocation(locationMaster);


		List<MapMeterLocation> mtrLocMapList = mtrLocMappingDao.getMapByLocationAndDate(locationMaster, null);

		List<LocationMFMap> locationEMFList=locEmfDao.findLocationEmfByLocAndDate(mtrLocMapList, null); 

		for (DailyTransaction dailyTransaction : dailyTransactions) {

			//dailyTransaction.setTransactionStatus(EAUtil.DAILY_TRANSACTION_ADDED_MANUALLY);
			//calculationMappingUtil.setDailyTxnLocationAndMF(mtrLocMapList, locationEMFList, dailyTransaction);
			java.math.BigDecimal netwh=	dailyTransaction.getNetMWH();
			calculationMappingUtil.calculateImportExport(dailyTransaction);
			if(netwh.compareTo(dailyTransaction.getNetMWH())!=0)
			{
				dailyTransaction.setTransactionStatus(EAUtil.DAILY_TRANSACTION_ADDED_MANUALLY+10);
			}
		}
		dailyTransactionDao.save(dailyTransactions, null);

	}
}