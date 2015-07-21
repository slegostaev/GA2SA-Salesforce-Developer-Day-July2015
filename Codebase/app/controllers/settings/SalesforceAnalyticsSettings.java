package controllers.settings;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.ga2sa.security.Access;
import com.ga2sa.security.UserGroup;

import models.SalesforceAnalyticsProfile;
import models.dao.GoogleAnalyticsProfileDAO;
import models.dao.SalesforceAnalyticsProfileDAO;
import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

@Access(allowFor = UserGroup.ADMIN)
public class SalesforceAnalyticsSettings extends Controller {
	@Transactional
	public static Result add() {
		
		String profileName 		= request().body().asJson().get("name").textValue();
		String username 		= request().body().asJson().get("username").textValue();
		String password 		= request().body().asJson().get("password").textValue();
		String applicationName 	= request().body().asJson().get("applicationName").textValue();
		
		SalesforceAnalyticsProfile profile = new SalesforceAnalyticsProfile();
		
		profile.setName(profileName);
		profile.setUsername(username);
		profile.setPassword(password);
		profile.setApplicationName(applicationName);
		
		HashMap<String, HashMap<String, String>> validateResult = SalesforceAnalyticsProfileDAO.validate(profile);
		
		JsonNode response = Json.toJson(validateResult);
		
		if (!validateResult.get("errors").isEmpty()) return badRequest(response).as("application/json");
		
		try {
			SalesforceAnalyticsProfileDAO.save(profile);
		} catch (Exception e) {
			Logger.debug(e.getMessage());
			validateResult.get("errors").put("name", "Profile already exists");
			response = Json.toJson(validateResult);
			return badRequest(response).as("application/json");
		}
		
		return ok(Json.toJson(profile)).as("application/json");	
	}
	
	@Transactional
	public static Result delete(String profileId) {
		
		Integer id = Integer.parseInt(profileId);
		
		SalesforceAnalyticsProfile profile = SalesforceAnalyticsProfileDAO.getProfileById(id);
		SalesforceAnalyticsProfileDAO.delete(profile);
		
		return ok();
	}
	
	@Transactional
	public static Result update(String profileId) {
		
		Integer id 				= Integer.parseInt(profileId);
		String profileName 		= request().body().asJson().get("name").textValue();
		String username 		= request().body().asJson().get("username").textValue();
		String password 		= request().body().asJson().get("password").textValue();
		String applicationName 	= request().body().asJson().get("applicationName").textValue();
		
		SalesforceAnalyticsProfile profile = SalesforceAnalyticsProfileDAO.getProfileById(id);
		
		profile.setName(profileName);
		profile.setUsername(username);
		profile.setPassword(password);
		profile.setApplicationName(applicationName);
		
		HashMap<String, HashMap<String, String>> validateResult = SalesforceAnalyticsProfileDAO.validate(profile);
		
		JsonNode response = Json.toJson(validateResult);
		
		if (!validateResult.get("errors").isEmpty()) return badRequest(response).as("application/json");
		
		try {
			SalesforceAnalyticsProfileDAO.update(profile);
		} catch (Exception e) {
			Logger.debug(e.getMessage());
			validateResult.get("errors").put("name", "Profile already exists");
			response = Json.toJson(validateResult);
			return badRequest(response).as("application/json");
		}
		
		return ok(Json.toJson(profile)).as("application/json");	
	}
}
