package models.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.ga2sa.security.ApplicationSecurity;
import com.ga2sa.security.PasswordManager;

import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import org.postgresql.util.PSQLException;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import models.User;

public class UserDAO {
	
	public static List<User> getUsers() {
		
		try {
			return JPA.withTransaction(new play.libs.F.Function0<List<User>>() {
				public List<User> apply () {
					return (List<User>) JPA.em().createNamedQuery("User.findAll").getResultList();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	
	}
	
	public static List<User> getUserWithoutCurrent() {
		
		User currentUser = ApplicationSecurity.getCurrentUser();
		
		try {
			return JPA.withTransaction(new play.libs.F.Function0<List<User>>() {
				public List<User> apply () {
					Query query = JPA.em().createQuery("select u from User u where u.id <> :id ORDER BY u.id ASC", User.class);
					query.setParameter("id", currentUser.getId());
					return (List<User>) query.getResultList();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static User getUserByUsername(String username) {

		try {
			return JPA.withTransaction(new play.libs.F.Function0<User>() {
				public User apply () {
					Query query = JPA.em().createQuery("select u from User u where u.username = :username", User.class);
					query.setParameter("username", username);
					return (User) query.getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static User getUserById(Long userId) {
				
		try {
			return JPA.withTransaction(new play.libs.F.Function0<User>() {
				public User apply () {
					Query query = JPA.em().createQuery("select u from User u where u.id = :user_id", User.class);
					query.setParameter("user_id", userId);
					return (User) query.getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	@Transactional
	public static void save(User user) throws Exception{

		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().persist(user);
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
	public static void delete(User user) {
		
		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().remove(JPA.em().merge(user));
	            }
	        });
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Transactional
	public static void update(User changedUser) throws Exception {
		
		User user = getUserById(changedUser.getId());
		
		user.setUsername(changedUser.getUsername());
		user.setEmailAddress(changedUser.getEmailAddress());
		user.setFirstName(changedUser.getFirstName());
		user.setLastName(changedUser.getLastName());
		user.setRole(changedUser.getRole());
		user.setRecordModifiedBy(ApplicationSecurity.getCurrentUser().getId());
		user.setRecordModifiedDateTime(new Timestamp(new Date().getTime()));
		
		if (changedUser.getIsActive() != null) user.setIsActive(changedUser.getIsActive());
		if (!changedUser.getPassword().equals(PasswordManager.PASSWORD_TMP)) user.setPassword(PasswordManager.encryptPassword(changedUser.getPassword()));
		
		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().merge(user);
	            }
	        });
		} catch (Throwable e) {
			e.printStackTrace();
			
			Throwable t = e.getCause();
	
		    while ((t != null) && !(t instanceof PSQLException)) t = t.getCause();
		    
		    if (t instanceof PSQLException) throw new Exception((PSQLException) t);
		}
		
	}
	
	public static HashMap<String, HashMap<String, String>> validate(User user) {
		
		HashMap<String, HashMap<String, String>> result = new HashMap<String, HashMap<String, String>>();
		
		result.put("errors", new HashMap<String, String>());
		
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		
		Set<ConstraintViolation<User>> errors = factory.getValidator().validate( user );
		
		for (ConstraintViolation<User> error : errors) result.get("errors").put(error.getPropertyPath().toString(), error.getMessage());
		
		return result;
	}

}
