package org.pstcl.ea.dao;

import java.util.List;

import org.pstcl.ea.model.entity.EAUser;
import org.pstcl.ea.model.entity.InstantRegisters;
import org.pstcl.ea.model.entity.MeterMaster;


public interface IInstantRegistersDao {
	
	void save(InstantRegisters instantRegisters, EAUser user);
	void update(InstantRegisters txn, EAUser user);
	List<InstantRegisters> findAllByMonthAndLocation( Integer month, Integer year);
	InstantRegisters findInstantRegistersByDayAndLocation(String locationId, Integer month, Integer year);
	void deleteById(String id);
	void save(List<InstantRegisters> instantRegisters, EAUser loggedInUser);
	InstantRegisters findById(int id);
	InstantRegisters findInstantRegistersByMeterAndMonth(MeterMaster meterMaster, Integer month, Integer year);
}
