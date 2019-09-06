package org.pstcl.ea.dao;

import java.util.Date;
import java.util.List;

import org.pstcl.ea.entity.EAUser;
import org.pstcl.ea.entity.LocationMaster;
import org.pstcl.ea.entity.MeterMaster;
import org.pstcl.ea.entity.SubstationMaster;
import org.pstcl.ea.entity.mapping.MapMeterLocation;
import org.pstcl.ea.model.EAFilter;

public interface MapMeterLocationDao {

	//ss

	MapMeterLocation findById(int id);

	void deleteById(String id);

	void save(MapMeterLocation txn, EAUser user);

	void update(MapMeterLocation txn, EAUser user);


	List<MapMeterLocation> getLocationByMeterAndDate(MeterMaster meterMaster, Date current);

	MapMeterLocation findMeterLocationMapByDate(String locationId, Date current);



	List<MapMeterLocation> findMeterLocationMapByLoc(String locationId);

	MapMeterLocation findById(String id);

	boolean find(MapMeterLocation newMtrLocMap);


	List<MapMeterLocation> findMappingHistory(MeterMaster meterMaster, LocationMaster locationMaster);

	List<MapMeterLocation> getMapByLocationAndDate(LocationMaster locationMaster, Date startDateOftheMonth);

	//List<MeterLocationMap> findMeterLocationMapBySubstation(SubstationMaster submaster);

	List<MapMeterLocation> findMeterLocationMapBySubstation(EAFilter entity);

	List<MapMeterLocation> findMapAfterDate(MapMeterLocation mapMeterLocation);

	

	


	
}
