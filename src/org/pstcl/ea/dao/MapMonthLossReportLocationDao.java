package org.pstcl.ea.dao;

import java.util.List;

import org.pstcl.ea.entity.EAUser;
import org.pstcl.ea.entity.LocationMaster;
import org.pstcl.ea.entity.mapping.MapMonthLossReportLocation;

public interface MapMonthLossReportLocationDao  {

	MapMonthLossReportLocation findById(int id);

	void deleteById(int id);

	void saveOrUpdate(MapMonthLossReportLocation txn, EAUser user);

	List<LocationMaster> findLocationProjectionByMonthAndYear(int month, int year);

	List<MapMonthLossReportLocation> findAllByCriteria(MapMonthLossReportLocation filter);

	List<MapMonthLossReportLocation> findAllByMonthAndYear(int month, int year);

	MapMonthLossReportLocation findByFilterCriteria(MapMonthLossReportLocation filter);

	Integer getMaxLossReportOrder(MapMonthLossReportLocation filter);


}
