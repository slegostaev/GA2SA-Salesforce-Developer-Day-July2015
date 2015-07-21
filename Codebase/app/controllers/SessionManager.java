package controllers;

import play.mvc.Controller;

public class SessionManager extends Controller {
	
	public static String get(String id) {
		return session(id);
	}
	
	public static void set(String id, String value) {
		session(id, value);
	}
	
	public static void remove(String id) {
		session().remove(id);
	}

}
