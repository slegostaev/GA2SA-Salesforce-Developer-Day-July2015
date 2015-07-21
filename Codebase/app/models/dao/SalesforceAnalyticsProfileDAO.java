package models.dao;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.postgresql.util.PSQLException;

import com.ga2sa.hibernate.AppEntityManager;

import models.GoogleAnalyticsProfile;
import models.SalesforceAnalyticsProfile;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

public class SalesforceAnalyticsProfileDAO {
	
	public static List<String> privateFields = Arrays.asList(
			"password", 
			"username", 
			"applicationName"
		);
	
	public static List<SalesforceAnalyticsProfile> getProfiles() {
		
		try {
			return JPA.withTransaction(new play.libs.F.Function0<List<SalesforceAnalyticsProfile>>() {
				public List<SalesforceAnalyticsProfile> apply () {
					return (List<SalesforceAnalyticsProfile>) JPA.em().createNamedQuery("SalesforceAnalyticsProfile.findAll").getResultList();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static SalesforceAnalyticsProfile getProfileByName(String name) {
		
		try {
			return JPA.withTransaction(new play.libs.F.Function0<SalesforceAnalyticsProfile>() {
				public SalesforceAnalyticsProfile apply () {
					return (SalesforceAnalyticsProfile) JPA.em().createQuery("select gap from SalesforceAnalyticsProfile gap where gap.name = :name", SalesforceAnalyticsProfile.class).setParameter("name", name).getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static SalesforceAnalyticsProfile getProfileById(Integer profileId) {
		
		try {
			return JPA.withTransaction(new play.libs.F.Function0<SalesforceAnalyticsProfile>() {
				public SalesforceAnalyticsProfile apply () {
					return (SalesforceAnalyticsProfile) JPA.em().createQuery("select gap from SalesforceAnalyticsProfile gap where gap.id = :id", SalesforceAnalyticsProfile.class).setParameter("id", profileId).getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	
	}
	
	@Transactional
	public static void save(SalesforceAnalyticsProfile profile) throws Exception {
		
		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().persist(profile);
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
	public static void delete(SalesforceAnalyticsProfile profile) {
		
		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().remove(JPA.em().merge(profile));
	            }
	        });
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	@Transactional
	public static void update(SalesforceAnalyticsProfile profile) throws Exception {
		
		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().merge(profile);
	            }
	        });
		} catch (Throwable e) {
			e.printStackTrace();
			
			Throwable t = e.getCause();
			
			while ((t != null) && !(t instanceof PSQLException)) t = t.getCause();
				    
			if (t instanceof PSQLException) throw new Exception((PSQLException) t);
		}
		
	}
	
	public static HashMap<String, HashMap<String, String>> validate(SalesforceAnalyticsProfile profile) {
		
		HashMap<String, HashMap<String, String>> result = new HashMap<String, HashMap<String, String>>();
		
		result.put("errors", new HashMap<String, String>());
		
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		
		Set<ConstraintViolation<SalesforceAnalyticsProfile>> errors = factory.getValidator().validate( profile );
		
		for (ConstraintViolation<SalesforceAnalyticsProfile> error : errors) result.get("errors").put(error.getPropertyPath().toString(), error.getMessage());
		
		return result;
	}
}
