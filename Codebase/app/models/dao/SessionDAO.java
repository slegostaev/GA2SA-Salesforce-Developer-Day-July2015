package models.dao;

import javax.persistence.Query;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import models.Session;

public class SessionDAO {
	
	public static Session getSession(String sessionId) {
		
		try {
			return JPA.withTransaction(new play.libs.F.Function0<Session>() {
				public Session apply () {
					Query query = JPA.em().createQuery("select s from Session s where s.id = :session_id", Session.class);
					query.setParameter("session_id", sessionId);
					return (Session) query.getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	
	}
	
	@Transactional
	public static void save(Session session) {
		
		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().persist(session);
	            }
	        });
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	@Transactional
	public static void deleteById(String sessionId) {
		
		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().remove(JPA.em().merge(getSession(sessionId)));
	            }
	        });
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

}
