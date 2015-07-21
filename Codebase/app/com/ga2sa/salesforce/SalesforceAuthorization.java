package com.ga2sa.salesforce;

import controllers.SessionManager;

public class SalesforceAuthorization {
	
	public static final String USERNAME_SESSION_ID = "salesforce_username";
	public static final String PASSWORD_SESSION_ID = "salesforce_password";
	
	public SalesforceAuthorization() {
		
	}
	
	public static void signIn(String username, String password) {
		SessionManager.set(USERNAME_SESSION_ID, username);
		SessionManager.set(PASSWORD_SESSION_ID, password);
	}
	
	public static void signOut() {
		SessionManager.remove(USERNAME_SESSION_ID);
		SessionManager.remove(PASSWORD_SESSION_ID);
	}

	public static Boolean isAlreadyAuth() {
		return SessionManager.get(USERNAME_SESSION_ID) != null && SessionManager.get(PASSWORD_SESSION_ID) != null;
	}

}
