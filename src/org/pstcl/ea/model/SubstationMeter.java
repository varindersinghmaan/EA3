package org.pstcl.ea.model;

import java.util.List;

import org.pstcl.ea.entity.SubstationMaster;
import org.pstcl.ea.entity.mapping.MapMeterLocation;

public class SubstationMeter {

	private SubstationMaster substationMaster;
	private List<MapMeterLocation> mtrLocMap;
	public List<MapMeterLocation> getMtrLocMap() {
		return mtrLocMap;
	}
	public void setMtrLocMap(List<MapMeterLocation> mtrLocMap) {
		this.mtrLocMap = mtrLocMap;
	}
	public SubstationMaster getSubstationMaster() {
		return substationMaster;
	}
	public void setSubstationMaster(SubstationMaster substationMaster) {
		this.substationMaster = substationMaster;
	}
	
}
