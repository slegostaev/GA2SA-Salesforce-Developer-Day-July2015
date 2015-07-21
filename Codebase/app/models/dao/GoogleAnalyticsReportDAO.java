package models.dao;

import org.postgresql.util.PSQLException;

import com.ga2sa.hibernate.AppEntityManager;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import models.GoogleAnalyticsReport;

public class GoogleAnalyticsReportDAO {
	
	public static GoogleAnalyticsReport getReportByJobId(Long id) {
		try {
			return JPA.withTransaction(new play.libs.F.Function0<GoogleAnalyticsReport>() {
				public GoogleAnalyticsReport apply () {
					return (GoogleAnalyticsReport) JPA.em().createQuery("select gar from GoogleAnalyticsReport gar where gar.jobId = :id", GoogleAnalyticsReport.class).setParameter("id", id).getSingleResult();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void save(GoogleAnalyticsReport report) throws Exception {	
		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().persist(report);
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
	public static void update(GoogleAnalyticsReport googleAnalyticsReport) throws Exception {
		try {
			JPA.withTransaction(new play.libs.F.Callback0() {
	            @Override
	            public void invoke() throws Throwable {
	                JPA.em().merge(googleAnalyticsReport);
	            }
	        });
		} catch (Throwable e) {
			e.printStackTrace();
			
			Throwable t = e.getCause();
			
			while ((t != null) && !(t instanceof PSQLException)) t = t.getCause();
				    
			if (t instanceof PSQLException) throw new Exception((PSQLException) t);
		}
		
	}
}
