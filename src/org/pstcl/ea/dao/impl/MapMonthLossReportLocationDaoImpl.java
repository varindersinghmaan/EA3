package org.pstcl.ea.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.pstcl.ea.dao.MapMonthLossReportLocationDao;
import org.pstcl.ea.entity.EAUser;
import org.pstcl.ea.entity.LocationMaster;
import org.pstcl.ea.entity.mapping.MapMonthLossReportLocation;
import org.pstcl.ea.entity.mapping.MapMonthLossReportLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MapMonthLossReportLocationDaoImpl implements MapMonthLossReportLocationDao {

	static final Logger logger = LoggerFactory.getLogger(MapMonthLossReportLocationDaoImpl.class);

	@Transactional(value = "sldcTxnManager")
	protected Session getSession() {

		return sessionFactory.getCurrentSession();
	}

	@Autowired
	@Qualifier("sldcSessionFactory")
	private SessionFactory sessionFactory;

	@Transactional(value = "sldcTxnManager")
	protected Criteria createEntityCriteria() {
		Criteria crit= getSession().createCriteria(MapMonthLossReportLocation.class);
		crit.addOrder(Order.asc("lossReportOrder"));
		return crit;
	}

	@Override
	@Transactional(value = "sldcTxnManager")
	public MapMonthLossReportLocation findById(int id) {

		MapMonthLossReportLocation txn = getSession().get(MapMonthLossReportLocation.class, id);
		return txn;
	}

	@Override
	@Transactional(value = "sldcTxnManager")
	public void deleteById(int id) {
		Criteria crit = createEntityCriteria();
		crit.add(Restrictions.eq("id", id));
		MapMonthLossReportLocation txn = (MapMonthLossReportLocation) crit.uniqueResult();
		getSession().delete(txn);
	}



	@Override
	@Transactional(value = "sldcTxnManager")
	public void saveOrUpdate(MapMonthLossReportLocation txn, EAUser user) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		session.saveOrUpdate(txn);
		transaction.commit();
		session.close();
	}



	@Override
	@Transactional(value = "sldcTxnManager")
	public List<MapMonthLossReportLocation> findAllByMonthAndYear(int month, int year) {
		Criteria crit = createEntityCriteria();
		crit.add(Restrictions.eq("month", month));
		crit.add(Restrictions.eq("year", year));
		return crit.list();
	}


	@Override
	@Transactional(value = "sldcTxnManager")
	public List<LocationMaster> findLocationProjectionByMonthAndYear(int month, int year) {
		Criteria crit = createEntityCriteria();
		crit.add(Restrictions.eq("month", month));
		crit.add(Restrictions.eq("year", year));
		crit.setProjection(Projections.property("location"));
		return crit.list();
	}
	@Override
	public List<MapMonthLossReportLocation> findAllByCriteria(MapMonthLossReportLocation filter) {
		Criteria crit = createEntityCriteria();
		if(null!=filter)
		{
			if(filter.getMonth()!=null)
			{
				crit.add(Restrictions.eq("month", filter.getMonth()));
			}
			if(filter.getYear()!=null)
			{
				crit.add(Restrictions.eq("year", filter.getYear()));

			}
			if(filter.getLossReportCriteria()!=null)
			{
				crit.add(Restrictions.eq("lossReportCriteria",filter.getLossReportCriteria()));

			}
		}
		else
		{
			return null;
		}
		List<MapMonthLossReportLocation>  list= crit.list();
		return list;
	}




	@Override
	public MapMonthLossReportLocation findByFilterCriteria(MapMonthLossReportLocation filter) {
		Criteria crit = createEntityCriteria();
		if(filter.getMonth()!=null&&filter.getLocation()!=null&&filter.getYear()!=null)
		{
			crit.add(Restrictions.eq("month", filter.getMonth()));
			crit.add(Restrictions.eq("year", filter.getYear()));
			crit.add(Restrictions.eq("location",filter.getLocation().getLocationId()));
		}
		else
		{
			return null;
		}
		return (MapMonthLossReportLocation) crit.uniqueResult();

	}

	@Override
	public Integer getMaxLossReportOrder(MapMonthLossReportLocation filter) {
		Criteria crit = createEntityCriteria();
		if(filter.getMonth()!=null&&filter.getLossReportCriteria()!=null&&filter.getYear()!=null)
		{
			crit.add(Restrictions.eq("month", filter.getMonth()));
			crit.add(Restrictions.eq("year", filter.getYear()));
			crit.add(Restrictions.eq("lossReportCriteria",filter.getLossReportCriteria()));
			crit.setProjection(Projections.max("lossReportOrder"));
		}
		return (Integer) crit.uniqueResult();
	}

}
