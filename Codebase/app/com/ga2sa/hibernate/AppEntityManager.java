package com.ga2sa.hibernate;

import javax.persistence.EntityManager;
import play.db.jpa.JPA;

public class AppEntityManager {
	
	private static EntityManager em = JPA.em("default");
	
	public static EntityManager getInstance() {		
		if (!em.isOpen()) em = JPA.em("default");
		return em;
	}
	
}
