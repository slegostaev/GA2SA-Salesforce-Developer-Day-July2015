package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import com.ga2sa.security.Access;

@Access
public class Application extends Controller {
	
	public static Result index() {
		return redirect(routes.Dashboard.index());
	}
}
