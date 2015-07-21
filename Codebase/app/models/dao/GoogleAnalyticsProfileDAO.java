package models.dao;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.postgresql.util.PSQLException;

import com.ga2sa.hibernate.AppEntityManager;

import models.GoogleAnalyticsProfile;
import models.User;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

public class GoogleAnalyticsProfileDAO {
	
	public static List<String> privateFields = Arrays.asList(
		"authProviderX509CertUrl", 
		"authUri", 
		"clientEmail", 
		"clientId",
		"clientSecret",
		"clientX509CertUrl",
		"redirectUris",
		"tokenUri",
		"connected",
		"googleUserId",
		"accessToken",
		"refreshToken"
	);
	
	public static final String CACHE_PROFILE_PREFIX = "google_profile_";
	public static final String FLASH_PROFILE_ID 	= "profile_id";
	
	public static List<GoogleAnalyticsProfile> getProfiles() {
		
		try {
			return JPA.withTransaction(new play.libs.F.Function0<List<GoogleAnalyticsProfile>>() {
				public List<GoogleAnalyticsProfile> apply () {
					return (List<GoogleAnalyticsProfile>) JPA.em().createNamedQuery("GoogleAnalyticsProfile.findAll").getResultList();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static List<GoogleAnalyticsProfile> getConnectedProfiles() {
		
		try {
			return JPA.withTransaction(new play.libs.F.Function0<List<GoogleAnalyticsProfile>>() {
				public List<GoogleAnalyticsProfile> apply () {
					return (List<GoogleAnalyticsProfile>) JPA.em().createQuery("select gap from GoogleAnalyticsProfile gap where gap.name <> 'default' and gap.connected = true").getResultList();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static GoogleAnalyticsProfile getDefaultProfile() {
		
		try {
			return JPA.withTransaction(new play.libs.F.Function0<GoogleAnalyticsProfile>() {
				public GoogleAnalyticsProfile apply () {
					return (GoogleAnalyticsProfile) JPA.em().createQuery("select gap from GoogleAnalyticsProfile gap where gap.name = 'default'", GoogleAnalyticsProfile.class).getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static GoogleAnalyticsProfile getProfileByName(String name) {
		
		try {
			return JPA.withTransaction(new play.libs.F.Function0<GoogleAnalyticsProfile>() {
				public GoogleAnalyticsProfile apply () {
					return (GoogleAnalyticsProfile) JPA.em().createQuery("select gap from GoogleAnalyticsProfile gap where gap.name = :name", GoogleAnalyticsProfile.class).setParameter("name", name).getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public static GoogleAnalyticsProfile getProfileById(Integer profileId) {
		
		try {
			return JPA.withTransaction(new play.libs.F.Function0<GoogleAnalyticsProfile>() {
				public GoogleAnalyticsProfile apply () {
					return (GoogleAnalyticsProfile) JPA.em().createQuery("select gap from GoogleAnalyticsProfile gap where gap.id = :id", GoogleAnalyticsProfile.class).setParameter("id", profileId).getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}

	}
	
	@Transactional
	public static void save(GoogleAnalyticsProfile profile) throws Exception {
		
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
	public static void delete(GoogleAnalyticsProfile profile) {
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
	public static void update(GoogleAnalyticsProfile profile) throws Exception {
		
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
	
	public static HashMap<String, HashMap<String, String>> validate(GoogleAnalyticsProfile profile) {
		
		HashMap<String, HashMap<String, String>> result = new HashMap<String, HashMap<String, String>>();
		
		result.put("errors", new HashMap<String, String>());
		
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		
		Set<ConstraintViolation<GoogleAnalyticsProfile>> errors = factory.getValidator().validate( profile );
		
		for (ConstraintViolation<GoogleAnalyticsProfile> error : errors) result.get("errors").put(error.getPropertyPath().toString(), error.getMessage());
		
		return result;
	}
	
}
