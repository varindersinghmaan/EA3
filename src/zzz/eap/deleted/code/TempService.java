package zzz.eap.deleted.code;

import java.util.List;

import javax.inject.Qualifier;

import org.pstcl.ea.dao.ILocationMasterDao;
import org.pstcl.ea.entity.LocationMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

//@Service
public class TempService {

	@Autowired
	public ILocationMasterDao locationMasterDao;
	
	@Autowired
	@org.springframework.beans.factory.annotation.Qualifier(value = "eaThreadPoolTaskExecutor")
	public ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	public ApplicationContext context;

	public void calcDTForLocations()
	{
		List<LocationMaster> locationMasterList=locationMasterDao.findAllLocationMasters();
		for (LocationMaster locationMaster : locationMasterList) {
			
			taskExecutor.execute(new Runnable() {

				@Override
				public void run() {

					TempServiceThread tempService= context.getBean(TempServiceThread.class);

					tempService.saveDailyTransactions(locationMaster);
				}
			} );
		}
	}
	
	

}
