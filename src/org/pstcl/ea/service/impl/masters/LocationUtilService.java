
package org.pstcl.ea.service.impl.masters;

import java.util.List;

import org.pstcl.ea.dao.IBoundaryTypeMasterDao;
import org.pstcl.ea.dao.IDeviceTypeMasterDao;
import org.pstcl.ea.dao.ILocationMasterDao;
import org.pstcl.ea.dao.IMeterMasterDao;
import org.pstcl.ea.dao.MapMeterLocationDao;
import org.pstcl.ea.dao.SubstationUtilityDao;
import org.pstcl.ea.entity.CircleMaster;
import org.pstcl.ea.entity.DivisionMaster;
import org.pstcl.ea.entity.LocationMaster;
import org.pstcl.ea.entity.MeterMaster;
import org.pstcl.ea.entity.SubstationMaster;
import org.pstcl.ea.entity.mapping.MapMeterLocation;
import org.pstcl.ea.model.MastersForLocationEntry;
import org.pstcl.model.FilterModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("restService")
@Transactional(value="sldcTxnManager")
public class LocationUtilService {

	@Autowired
	IDeviceTypeMasterDao deviceTypeMasterDao;
	

	@Autowired
	MapMeterLocationDao mtrLocMapDao;

	@Autowired
	ILocationMasterDao locationMasterDao;
	
	
	@Autowired
	IBoundaryTypeMasterDao boundaryTypeMasterDao;
	


	

	@Autowired
	protected IMeterMasterDao meterDao;

	@Autowired
	SubstationUtilityDao ssUtilDao;



	

	public CircleMaster findCircleById(Integer code) {
		return this.ssUtilDao.findCircleByID(code);
	}

	public DivisionMaster findDivisionById(Integer code) {
		return this.ssUtilDao.findDivisionByID(code);
	}

	public SubstationMaster findSubstationById(Integer code) {
		return this.ssUtilDao.findSubstationByID(code);
	}

	public List<CircleMaster> getCircleList(FilterModel locationModel) {

		return this.ssUtilDao.listCircles(locationModel);
	}

	public List<DivisionMaster> getDivisionList(FilterModel locationModel) {
		return this.ssUtilDao.listDivisions(locationModel);
	}

	public List<SubstationMaster> getSubstationList(FilterModel locationModel) {
		return this.ssUtilDao.listSubstations(locationModel);
	}

	public List<LocationMaster> getLocationList(FilterModel locationModel) {
		return this.ssUtilDao.listLocations(locationModel);
	}

	
	/**
	 * Generate interlinked select options on selecting circle,substation ,division
	 * @param circle
	 * @param divCode
	 * @param substationCode
	 * @param Locationid
	 * @return
	 */
	public FilterModel getLocationModel(Integer circle, Integer divCode, Integer substationCode, String Locationid) {
		FilterModel locationModel = new FilterModel();
		if (circle != null) {
			locationModel.setSelectedCircle(findCircleById(circle));
		}
		if (divCode != null) {
			locationModel.setSelectedDivision(findDivisionById(divCode));
		}
		if (substationCode != null) {
			locationModel.setSelectedSubstation(findSubstationById(substationCode));
		}
		if (Locationid != null) {
			locationModel.setSelectedLocation(findLocationBYId(Locationid));
		}
		locationModel.setCircleList(getCircleList(locationModel));
		locationModel.setDivisionList(getDivisionList(locationModel));
		locationModel.setSubstationList(getSubstationList(locationModel));
		locationModel.setLocationList(getLocationList(locationModel));
		return locationModel;
	}

	public LocationMaster findLocationBYId(String locationid) {
		return this.ssUtilDao.findLocationByID(locationid);
	}

	public List<MapMeterLocation> findLocations(MeterMaster meterMaster) {
		return mtrLocMapDao.getLocationByMeterAndDate(meterMaster,null);
	}
	
	


	@Transactional(value="sldcTxnManager")
	public List<MapMeterLocation> findLocations(MeterMaster meterMaster,LocationMaster locationMaster ) {
		return mtrLocMapDao.findMappingHistory(meterMaster, locationMaster);
	}


	public LocationMaster getMeterDeatils(String locationid) {
		return locationMasterDao.findById(locationid);
	}
	

	/**
	 * Service to generate options in adding - location form 
	 * @return
	 */
	public MastersForLocationEntry getLocationMasterListModel() {
		// TODO Auto-generated method stub
		MastersForLocationEntry list = new MastersForLocationEntry();
		list.setUtiltiyName(locationMasterDao.findDistinctUtiltiyName());
		list.setVoltageLevel(locationMasterDao.findDistinctVoltageLevel());
		list.setBoundaryTypeMaster(boundaryTypeMasterDao.findAllUsers());
		list.setDeviceTypeMaster(deviceTypeMasterDao.findAllDeviceTypes());
		return list;
	}
	





}
