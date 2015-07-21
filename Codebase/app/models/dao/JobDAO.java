package models.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.postgresql.util.PSQLException;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import models.GoogleAnalyticsProfile;
import models.Job;

import com.ga2sa.hibernate.AppEntityManager;

public class JobDAO {
	
	public static List<Job> getJobs() {
		
		try {
			return JPA.withTransaction(new play.libs.F.Function0<List<Job>>() {
				public List<Job> apply () {
					return (List<Job>) AppEntityManager.getInstance().createNamedQuery("Job.findAll").getResultList();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public static Job getLastJob() {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<Job>() {
				public Job apply () {
					return (Job) JPA.em().createQuery("select j from Job j ORDER BY j.id DESC", Job.class).setMaxResults(1).getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<Job> getJobsForScheduler() {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<List<Job>>() {
				public List<Job> apply () {
					return (List<Job>) AppEntityManager.getInstance().createQuery("select j from Job j where j.status = 'PENDING' or j.repeatPeriod <> null").getResultList();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Transactional
	public static void save(Job job) throws Exception {
		
		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().persist(job);
	            }
	        });
		} catch (Throwable e) {
			e.printStackTrace();
			
			Throwable t = e.getCause();
			
			while ((t != null) && !(t instanceof PSQLException)) t = t.getCause();
				    
			if (t instanceof PSQLException) throw new Exception((PSQLException) t);
		}
		
	}
	
	@Transactional
	public static void delete(Job job) {
		
		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().remove(JPA.em().merge(job));
	            }
	        });
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	@Transactional
	public static void update(Job job) throws Exception {
		
		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().merge(job);
	            }
	        });
		} catch (Throwable e) {
			e.printStackTrace();
			
			Throwable t = e.getCause();
			
			while ((t != null) && !(t instanceof PSQLException)) t = t.getCause();
				    
			if (t instanceof PSQLException) throw new Exception((PSQLException) t);
		}
		
	}
	
	public static HashMap<String, HashMap<String, String>> validate(Job job) {
		
		HashMap<String, HashMap<String, String>> result = new HashMap<String, HashMap<String, String>>();
		
		result.put("errors", new HashMap<String, String>());
		
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		
		Set<ConstraintViolation<Job>> errors = factory.getValidator().validate( job );
		
		for (ConstraintViolation<Job> error : errors) result.get("errors").put(error.getPropertyPath().toString(), error.getMessage());
		
		return result;
	}
}
