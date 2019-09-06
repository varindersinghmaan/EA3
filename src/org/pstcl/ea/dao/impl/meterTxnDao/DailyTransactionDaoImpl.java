package org.pstcl.ea.dao.impl.meterTxnDao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.transform.Transformers;
import org.pstcl.ea.dao.IDailyTransactionDao;
import org.pstcl.ea.entity.EAUser;
import org.pstcl.ea.entity.LocationMaster;
import org.pstcl.ea.entity.MeterMaster;
import org.pstcl.ea.entity.meterTxnEntity.DailyTransaction;
import org.pstcl.ea.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;





@Repository("dailyTransactionDao")
//@org.springframework.transaction.annotation.Transactional(value="sldcTxnManager")
public class DailyTransactionDaoImpl  implements IDailyTransactionDao {



	@Autowired 
	@Qualifier("sldcSessionFactory")
	private SessionFactory sessionFactory;


	@Transactional(value="sldcTxnManager")
	protected Session getSession(){

		return sessionFactory.getCurrentSession();
	}

	static final Logger logger = LoggerFactory.getLogger("DailyTransactionFileLogger");


	@Override
	public DailyTransaction findById(int id) {
		DailyTransaction txn = getSession().get(DailyTransaction.class,id);

		return txn;
	}



	@Transactional(value="sldcTxnManager")
	protected Criteria createEntityCriteria(){
		return getSession().createCriteria(DailyTransaction.class);
	}




	@Override
	public void deleteById(Integer id) {
		Criteria crit = createEntityCriteria();
		crit.add(Restrictions.eq("txnId", id));
		DailyTransaction txn = (DailyTransaction)crit.uniqueResult();
		getSession().delete(txn);
	}



	@Override
	public void update(DailyTransaction txn,EAUser user) {
		Session session=sessionFactory.openSession();
		Transaction transaction=session.beginTransaction();

		session.persist(txn);
		transaction.commit();
		session.close();
	}





	@Override
	@Transactional(value="sldcTxnManager")
	public DailyTransaction findByLocationDateFilter(DailyTransaction dailyTransaction) {
		Criteria crit = createEntityCriteria();
		crit.add(Restrictions.eq("location.locationId", dailyTransaction.getLocation().getLocationId()));
		crit.add(Restrictions.eq("transactionDate",dailyTransaction.getTransactionDate()));
		return (DailyTransaction) crit.uniqueResult();
	}






	@Override
	@Transactional(value="sldcTxnManager")
	public DailyTransaction findByLocationDateCombo(LocationMaster meter, Date txnDate) {
		Criteria crit = createEntityCriteria();
		java.sql.Date date=new java.sql.Date(txnDate.getTime());
		crit.add(Restrictions.eq("location.locationId", meter.getLocationId()));
		crit.add(Restrictions.eq("transactionDate",date));
		return (DailyTransaction) crit.uniqueResult();
	}







	@Override
	public void updateAll(List<DailyTransaction> dailyTransactions, EAUser loggedInUser) {

		Session session=sessionFactory.openSession();
		Transaction transaction=session.beginTransaction();
		for (DailyTransaction dailyTransaction : dailyTransactions) {
			try
			{
				if (null != dailyTransaction) {
					session.saveOrUpdate(dailyTransaction);
				}

			}
			catch(ConstraintViolationException dupExp)
			{
				logger.error(dupExp.getMessage());
				logger.error(dailyTransaction.toString());
				transaction.rollback();
				session.close();
				System.out.println("Came Back from Exception");

			}
			catch (Exception e) {
				logger.error(e.getMessage());
				logger.error(dailyTransaction.toString());
				transaction.rollback();
				session.close();

			}
		}
		try
		{
			transaction.commit();
			session.close();
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		}
	}



	@Override
	public void save(List<DailyTransaction> dailyTransactions, EAUser loggedInUser) {

		Session session=sessionFactory.openSession();
		Transaction transaction=session.beginTransaction();
		for (DailyTransaction dailyTransaction : dailyTransactions) {
			try
			{
				if (null != dailyTransaction) {
					session.save(dailyTransaction);
				}

			}
			catch(ConstraintViolationException dupExp)
			{
				logger.error(dupExp.getMessage());
				logger.error(dailyTransaction.toString());
				transaction.rollback();
				session.close();
				saveOrUpdateInCaseDuplicateException(dailyTransactions, loggedInUser);
				System.out.println("Came Back from Exception");
				return;
			}
			catch (Exception e) {
				logger.error(e.getMessage());
				logger.error(dailyTransaction.toString());
				transaction.rollback();
				session.close();
				saveOrUpdateInCaseDuplicateException(dailyTransactions, loggedInUser);
				return;
			}
		}
		transaction.commit();
		session.close();
	}

	//Called only in case of exception on duplicate entries for date,location combo. Saves the records only if not found
	private void saveOrUpdateInCaseDuplicateException(List<DailyTransaction> dailyTransactions, EAUser loggedInUser) {
		Session session=sessionFactory.openSession();
		Transaction transaction=session.beginTransaction();
		for (DailyTransaction dailyTransaction : dailyTransactions) {
			try
			{
				Criteria crit = session.createCriteria(DailyTransaction.class);
				if(dailyTransaction.getLocation()!=null)
				{
					crit.add(Restrictions.eq("location.locationId", dailyTransaction.getLocation().getLocationId()));
					crit.add(Restrictions.eq("transactionDate",dailyTransaction.getTransactionDate()));
				}
				DailyTransaction entityByLocDate =(DailyTransaction) crit.uniqueResult();

				//Called only in case of exception on duplicate entries for date,meterSrNo combo. Saves the records only if not found

				crit = session.createCriteria(DailyTransaction.class);
				crit.add(Restrictions.eq("meter.meterSrNo", dailyTransaction.getMeter().getMeterSrNo()));
				crit.add(Restrictions.ne("location.locationId", dailyTransaction.getLocation().getLocationId()));
				crit.add(Restrictions.eq("transactionDate",dailyTransaction.getTransactionDate()));
				DailyTransaction entityByMeterDate =(DailyTransaction) crit.uniqueResult();


				if(entityByLocDate==null&&entityByMeterDate!=null)
				{
					logger.error("SINGLE DUPLICATE OF"+dailyTransaction.toString());
					logger.error("DUPLICATE ENTITY WITH METER and DATE"+entityByMeterDate.toString());

					entityByLocDate=entityByMeterDate;
				}
				else if(entityByLocDate!=null&&entityByMeterDate!=null)
				{
					logger.error("TWO DUPLICATES OF"+dailyTransaction.toString());
					logger.error("DUPLICATE ENTITY WITH METER and DATE"+entityByMeterDate.toString());
					logger.error("DUPLICATE ENTITY WITH LOCATION and DATE"+entityByLocDate.toString());

					session.delete(entityByMeterDate);
				}


				if (null != entityByLocDate) {
					entityByLocDate.updateValues(dailyTransaction);
					session.update(entityByLocDate);
				}
				else
				{
					session.save(dailyTransaction);
				}

			}
			catch(ConstraintViolationException dupExp)
			{
				System.out.println(dupExp.getClass());
				transaction.rollback();
				session.close();
				System.out.println(dailyTransaction);
				return;
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.println(dailyTransaction);
			}

		}
		transaction.commit();
		session.close();
	}



	@Transactional(value="sldcTxnManager")
	@Override
	public List<DailyTransaction> getDailyTransactionsByLocGreaterThanDate(LocationMaster locationMaster,Date startDate) {
		Criteria criteria = createEntityCriteria();
		if(null!=locationMaster)
		{
			criteria.add(Restrictions.eq("location.locationId", locationMaster.getLocationId()));
		}
		else
		{
			return null;
		}
		criteria.add(Restrictions.ge("transactionDate",startDate));
		List<DailyTransaction> results=criteria.list();



		return results;
	}

	@Transactional(value="sldcTxnManager")
	@Override
	public List<DailyTransaction> getDailyTransactionsByMeterGreaterThanDate(MeterMaster meterMaster,Date startDate) {
		Criteria criteria = createEntityCriteria();
		if(null!=meterMaster)
		{
			criteria.add(Restrictions.eq("meter.meterSrNo", meterMaster.getMeterSrNo()));
		}
		else
		{
			return null;
		}

		criteria.add(Restrictions.ge("transactionDate",startDate));
		List<DailyTransaction> results=criteria.list();
		return results;
	}

	@Transactional(value="sldcTxnManager")
	@Override
	public List<DailyTransaction> getDailyTransactionsByMonth(LocationMaster locationMaster,Integer month,Integer year) {


		Criteria criteria = createEntityCriteria();
		if(null!=locationMaster)
		{
			criteria.add(Restrictions.eq("location.locationId", locationMaster.getLocationId()));
		}
		else
		{
			return null;
		}
		Date startDate=DateUtil.startDateTimeForDailySurveyRecs(month, year);
		Date endDate=DateUtil.endDateTimeForDailySurveyRecs(month, year);



		criteria.add(Restrictions.ge("transactionDate",startDate));
		criteria.add(Restrictions.le("transactionDate",endDate));


		List<DailyTransaction> results=criteria.list();


		@SuppressWarnings("unchecked")
		List<DailyTransaction> totalAll = criteria
		.setProjection(Projections.projectionList()
				.add(Projections.sum("exportWHF"), "exportWHF")
				.add(Projections.sum("importWHF"), "importWHF"))
		.setResultTransformer(Transformers.aliasToBean(DailyTransaction.class)).list();
		results.addAll(totalAll);

		return results;
	}


	@Transactional(value="sldcTxnManager")
	@Override
	public List<DailyTransaction> getDailyTransactionsByMonth(MeterMaster meterMaster,Integer month,Integer year) {


		Criteria criteria = createEntityCriteria();
		if(null!=meterMaster)
		{
			criteria.add(Restrictions.eq("meter.meterSrNo", meterMaster.getMeterSrNo()));
		}
		else
		{
			return null;
		}
		Date startDate=DateUtil.startDateTimeForDailySurveyRecs(month, year);
		Date endDate=DateUtil.endDateTimeForDailySurveyRecs(month, year);



		criteria.add(Restrictions.ge("transactionDate",startDate));
		criteria.add(Restrictions.le("transactionDate",endDate));


		List<DailyTransaction> results=criteria.list();


		@SuppressWarnings("unchecked")
		List<DailyTransaction> totalAll = criteria
		.setProjection(Projections.projectionList()
				.add(Projections.sum("exportWHF"), "exportWHF")
				.add(Projections.sum("importWHF"), "importWHF"))
		.setResultTransformer(Transformers.aliasToBean(DailyTransaction.class)).list();
		results.addAll(totalAll);

		return results;
	}



	@Override
	public List<DailyTransaction> findAllAnyLocation(LocationMaster locationMaster) {
		Criteria criteria = createEntityCriteria();
		if(null!=locationMaster)
		{
			criteria.add(Restrictions.eq("location.locationId", locationMaster.getLocationId()));
		}
		else
		{
			return null;
		}
		List<DailyTransaction> results=criteria.list();
		return results;
	}
}
