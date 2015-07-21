/**
 * 
 */
package com.ga2sa.security;

import java.util.UUID;

import play.Logger;
import play.db.jpa.Transactional;
import play.mvc.Http.Cookie;
import models.Session;
import models.User;
import models.dao.SessionDAO;
import models.dao.UserDAO;

import com.ga2sa.helpers.forms.LoginForm;

import controllers.CookieManager;

public class ApplicationSecurity {
	
	public static final String SESSION_ID_KEY = "session_id";
	
	public static Boolean authenticate(LoginForm loginForm) {
		User user = UserDAO.getUserByUsername(loginForm.getUsername());
		if (user == null) {
			Logger.error("Not found User");
		} else {
			if (user.getIsActive()) {
				if (PasswordManager.checkPassword(loginForm.getPassword(), user.getPassword())) {				
					final String sessionId = UUID.randomUUID().toString();
					CookieManager.set(SESSION_ID_KEY, sessionId, true);
					SessionDAO.save(new Session(sessionId, user.getId()));
					return true;
				}
			}
		}
		return false;
	}
	
	public static String getSessionId() {
		Cookie cookie = CookieManager.get(SESSION_ID_KEY);
		if (cookie != null) {
			return cookie.value();
		}
		return null;
	}
	
	public static Session getCurrentSession() {
		final String sessionId = getSessionId();
		Session session = SessionDAO.getSession(sessionId);
		return session;
	}
	
	public static User getCurrentUser() {
	
		final String sessionId = getSessionId();
		Session session = getCurrentSession();
		
		if (session == null && sessionId == null) {
			Logger.debug("User is not loggined.");
			return null;
		}
		
		if (session != null)  {
			return UserDAO.getUserById(session.getUserId());
		} else {
			return null;
		}
		
	}
	
	public static boolean isAdmin() {
		User user = getCurrentUser();
		return user == null ? false : user.getRole().equals("ADMIN");
	}
	
	public static void logout() {
		Cookie cookie = CookieManager.get(SESSION_ID_KEY);
		if (cookie != null && cookie.value() != null) {
			SessionDAO.deleteById(cookie.value());
			CookieManager.remove(SESSION_ID_KEY, true);
		}
	}
}
