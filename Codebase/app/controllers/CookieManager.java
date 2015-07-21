package controllers;

import play.Play;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Cookie;

public class CookieManager extends Controller {
	
	public static Cookie get(String id) {
		return Http.Context.current().request().cookie(id);
	}
	
	public static void set(String id, String value, boolean isSecure) {
		Http.Context.current().response().setCookie(id, value, null, null, null, Play.isProd(), isSecure);
	}
	
	public static void remove(String id, boolean isSecure) {
		Http.Context.current().response().discardCookie(id, null, null, isSecure);
	}
}
