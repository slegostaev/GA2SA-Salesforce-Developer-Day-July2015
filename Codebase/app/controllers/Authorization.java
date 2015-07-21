package controllers;

import models.GoogleAnalyticsProfile;
import models.dao.GoogleAnalyticsProfileDAO;

import com.ga2sa.google.GoogleConnector;
import com.ga2sa.helpers.forms.LoginForm;
import com.ga2sa.security.Access;
import com.ga2sa.security.ApplicationSecurity;

import play.Logger;
import play.cache.Cache;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class Authorization extends Controller {
	
	private static Form<LoginForm> loginForm = Form.form(LoginForm.class);
	
	public static Result ga2saSignIn() {
		if(request().method().equals("POST")) {
			Form<LoginForm> bindedForm = loginForm.bindFromRequest();
			if (ApplicationSecurity.authenticate(bindedForm.get())) return redirect(routes.Application.index());
			else return redirect(routes.Authorization.ga2saSignIn());
		}
		return ok(views.html.pages.auth.signin.render());
	}
	
	@Access
	public static Result ga2saSignOut() {
		ApplicationSecurity.logout();
		return redirect(routes.Authorization.ga2saSignIn());
	}
	
	@Access
	public static Result googleSignIn() {
		
		String code = request().getQueryString("code");
		String profileId = flash(GoogleAnalyticsProfileDAO.FLASH_PROFILE_ID);

		GoogleAnalyticsProfile profile = (GoogleAnalyticsProfile) Cache.get(GoogleAnalyticsProfileDAO.CACHE_PROFILE_PREFIX + profileId);

		GoogleConnector.exchangeCode(profile, code);
		
		return ok(views.html.pages.auth.googleAuthComplete.render());
		
	}

}
