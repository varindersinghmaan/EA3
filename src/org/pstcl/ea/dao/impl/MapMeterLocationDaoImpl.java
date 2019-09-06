package org.pstcl.ea.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.pstcl.ea.dao.MapMeterLocationDao;
import org.pstcl.ea.entity.EAUser;
import org.pstcl.ea.entity.LocationMaster;
import org.pstcl.ea.entity.MeterMaster;
import org.pstcl.ea.entity.SubstationMaster;
import org.pstcl.ea.entity.mapping.MapMeterLocation;
import org.pstcl.ea.model.EAFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;



//@Repository(value="meterLocationMapDao")
@Repository
@Transactional(value="sldcTxnManager")
public class MapMeterLocationDaoImpl implements MapMeterLocationDao {
	//map
	@Transactional(value="sldcTxnManager")
	protected Session getSession(){
		return sessionFactory.getCurrentSession();
	}

	@Autowired 
	@Qualifier("sldcSessionFactory")
	private SessionFactory sessionFactory;


	static final Logger logger = LoggerFactory.getLogger(MapMeterLocationDaoImpl.class);


	@Transactional(value="sldcTxnManager")
	protected Criteria createEntityCriteria(){
		return getSession().createCriteria(MapMeterLocation.class);
	}



	@Override
	@Transactional(value="sldcTxnManager")
	public MapMeterLocation findById(int id) {


		MapMeterLocation txn = getSession().get(MapMeterLocation.class, id);
		return txn;
	}


	@Override
	public void deleteById(String id) {
		Criteria crit = createEntityCriteria();
		crit.add(Restrictions.eq("id", id));
		MapMeterLocation txn = (MapMeterLocation)crit.uniqueResult();
		getSession().delete(txn);
	}

	@Override
	@Transactional(value="sldcTxnManager")
	public MapMeterLocation findById(String id) {
		Criteria crit = createEntityCriteria();

		if(id!=null) {
			crit.add((Restrictions.eq("id", id)));
		}

		return (MapMeterLocation) crit.uniqueResult();

	}

	@Override
	@Transactional(value="sldcTxnManager")
	public
	boolean find(MapMeterLocation newMtrLocMap) {
		Criteria crit = createEntityCriteria();
		crit.add(Restrictions.eq("startDate", newMtrLocMap.getStartDate()));
		crit.add((Restrictions.eq("locationMaster.locationId",newMtrLocMap.getLocationMaster().getLocationId())));
		crit.add(Restrictions.eq("meterMaster.meterSrNo", newMtrLocMap.getMeterMaster().getMeterSrNo()));
		List<MapMeterLocation> a = (List<MapMeterLocation>) crit.list();
		if(a!=null && a.size()>0)
			return true;
		return false;
	}

	@Override
	@Transactional(value="sldcTxnManager")
	public void save(MapMeterLocation txn,EAUser user) {
		Session session=sessionFactory.openSession();
		Transaction transaction=session.beginTransaction();
		getSession().persist(txn);
		transaction.commit();
		session.close();
	}

	@Override
	public void update(MapMeterLocation txn,EAUser user) {
		Session session=sessionFactory.openSession();
		Transaction transaction=session.beginTransaction();
		getSession().update(txn);
		transaction.commit();
		session.close();
	}



	@Override
	@Transactional(value="sldcTxnManager")
	public MapMeterLocation findMeterLocationMapByDate(String locationId,Date current) {
		Criteria crit = createEntityCriteria();

		if(locationId!=null) {
			crit.add((Restrictions.eq("locationMaster.locationId", locationId)));
		}



		crit.add(Restrictions.le("startDate",current));

		Criterion rest4= Restrictions.gt("endDate",current);
		Criterion rest5= Restrictions.isNull("endDate");
		crit.add(Restrictions.or( rest4,rest5));

		return (MapMeterLocation) crit.uniqueResult();

	}

	@Override
	@Transactional(value="sldcTxnManager")
	public List<MapMeterLocation> getMapByLocationAndDate(LocationMaster locationMaster, Date startDateOftheMonth) {
		Criteria crit = createEntityCriteria();


		if(null!=locationMaster)
		{
			crit.add((Restrictions.eq("locationMaster.locationId", locationMaster.getLocationId())));
		}
		else
		{
			crit.add(Restrictions.sqlRestriction("(1=0)"));

		}

		if(startDateOftheMonth!=null)
		{
			Criterion rest4= Restrictions.gt("endDate",startDateOftheMonth);
			Criterion rest5= Restrictions.isNull("endDate");
			crit.add(Restrictions.or( rest4,rest5));
		}
		return (List<MapMeterLocation>) crit.list();
	}


	@Override
	@Transactional(value="sldcTxnManager")
	public List<MapMeterLocation> getLocationByMeterAndDate(MeterMaster meterMaster, Date startDateOftheMonth) {
		Criteria crit = createEntityCriteria();

		if(null!=meterMaster)
		{
			crit.add(Restrictions.eq("meterMaster.meterSrNo", meterMaster.getMeterSrNo()));
		}
		else
		{
			crit.add(Restrictions.sqlRestriction("(1=0)"));

		}

		if(startDateOftheMonth!=null)
		{
			Criterion rest4= Restrictions.gt("endDate",startDateOftheMonth);
			Criterion rest5= Restrictions.isNull("endDate");
			crit.add(Restrictions.or( rest4,rest5));
		}
		return (List<MapMeterLocation>) crit.list();
	}


	@Override
	public
	List<MapMeterLocation> findMeterLocationMapByLoc(String locationId){
		Criteria crit = createEntityCriteria();

		if(locationId!=null) {
			crit.add((Restrictions.eq("locationMaster.locationId", locationId)));
		}


		return (List<MapMeterLocation>) crit.list();
	}

	@Override
	public List<MapMeterLocation> findMappingHistory(MeterMaster meterMaster,LocationMaster locationMaster){
		Criteria crit = createEntityCriteria();
		Criterion crit1 = null,crit2 = null;
		if(meterMaster!=null)
		{
			crit1=Restrictions.eq("meterMaster.meterSrNo", meterMaster.getMeterSrNo());
		}
		if(locationMaster!=null) {
			crit2=Restrictions.eq("locationMaster.locationId", locationMaster.getLocationId());
		}
		LogicalExpression orExp = Restrictions.or(crit1, crit2);
		crit.add(orExp);

		return (List<MapMeterLocation>) crit.list();
	}



	@Override
	@Transactional(value="sldcTxnManager")
	public List<MapMeterLocation> findMeterLocationMapBySubstation(EAFilter entity) {
		Criteria critLocationList = getSession().createCriteria(LocationMaster.class);
		if(null!=entity)
		{
			if(null!=entity.getSubstation())
			{
				critLocationList.add(Restrictions.eq("substationMaster.ssCode", entity.getSubstation().getSsCode()));
			}
			if(null!=entity.getDivision())
			{
				critLocationList.add(Restrictions.eq("divisionMaster.divCode", entity.getDivision().getDivCode()));
			}
			if(null!=entity.getCircle())
			{
				critLocationList.add(Restrictions.eq("circleMaster.crCode", entity.getCircle().getCrCode()));
			}
		}

		critLocationList.setProjection(Projections.property("locationId"));


		List <String> locationIdList=critLocationList.list();

		Criteria crit = createEntityCriteria();

		if(locationIdList!=null){
			if(locationIdList.size()>0)
			{
				crit.add((Restrictions.in("locationMaster.locationId", locationIdList)));
			}
			else
			{
				crit.add(Restrictions.sqlRestriction("(1=0)"));

			}
		}
		crit.addOrder(Order.asc("locationMaster.locationId"));
		return (List<MapMeterLocation>) crit.list();
	}

	private List<String> getLocationListBySubstation(SubstationMaster submaster) {
		Criteria critLocationList = getSession().createCriteria(LocationMaster.class);
		if(null!=submaster)
		{
			critLocationList.add(Restrictions.eq("substationMaster.ssCode", submaster.getSsCode()));
		}
		else
		{
			critLocationList.add(Restrictions.sqlRestriction("(1=0)"));

		}
		critLocationList.setProjection(Projections.property("locationId"));
		List <String> locationIdList=critLocationList.list();
		return locationIdList;
	}



	@Override
	public List<MapMeterLocation> findMapAfterDate(MapMeterLocation mapMeterLocation) {
		Criteria crit = createEntityCriteria();

		if(null!=mapMeterLocation)
		{
			if(null!=mapMeterLocation.getStartDate())
			{
				crit.add(Restrictions.le("startDate",mapMeterLocation.getStartDate()));
			}
			if(null!=mapMeterLocation.getEndDate())
			{
				crit.add(Restrictions.le("endDate",mapMeterLocation.getEndDate()));
			}
		}
		return (List<MapMeterLocation>) crit.list();
	}
}
