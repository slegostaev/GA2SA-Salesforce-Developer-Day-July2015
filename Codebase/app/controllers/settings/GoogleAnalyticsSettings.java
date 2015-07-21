package controllers.settings;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ga2sa.google.GoogleConnector;
import com.ga2sa.security.Access;
import com.ga2sa.security.UserGroup;

import models.GoogleAnalyticsProfile;
import models.dao.GoogleAnalyticsProfileDAO;
import models.dao.UserDAO;
import play.Logger;
import play.cache.Cache;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

@Access(allowFor = UserGroup.ADMIN)
public class GoogleAnalyticsSettings extends Controller {
	
	public static Result get(String profileId) {
		
		Integer id = Integer.parseInt(profileId);
		
		GoogleAnalyticsProfile profile = GoogleAnalyticsProfileDAO.getProfileById(id);
		
		return ok(Json.toJson(profile));
	}
	
	public static Result add() {
		
		GoogleAnalyticsProfile profile = (GoogleAnalyticsProfile)Json.fromJson(request().body().asJson(), GoogleAnalyticsProfile.class);
		
		HashMap<String, HashMap<String, String>> validateResult = GoogleAnalyticsProfileDAO.validate(profile);
		
		JsonNode response = Json.toJson(validateResult);
		
		if (!validateResult.get("errors").isEmpty()) return badRequest(response).as("application/json");
		
		try {
			GoogleAnalyticsProfileDAO.save(profile);
		} catch (Exception e) {
			Logger.debug(e.getMessage());
			validateResult.get("errors").put("name", "Profile already exists");
			response = Json.toJson(validateResult);
			return badRequest(response).as("application/json");
		}
		
		return ok(Json.toJson(profile)).as("application/json");	
	}
	
	public static Result delete(String profileId) {
		
		Integer id = Integer.parseInt(profileId);
		
		GoogleAnalyticsProfile profile = GoogleAnalyticsProfileDAO.getProfileById(id);
		
		GoogleAnalyticsProfileDAO.delete(profile);
		
		return ok();
	}
	
	public static Result update(String profileId) {
		
		GoogleAnalyticsProfile profile = (GoogleAnalyticsProfile)Json.fromJson(request().body().asJson(), GoogleAnalyticsProfile.class);
		
		HashMap<String, HashMap<String, String>> validateResult = GoogleAnalyticsProfileDAO.validate(profile);
		
		JsonNode response = Json.toJson(validateResult);
		
		if (!validateResult.get("errors").isEmpty()) return badRequest(response).as("application/json");
		
		try {
			GoogleAnalyticsProfileDAO.update(profile);
		} catch (Exception e) {
			Logger.debug(e.getMessage());
			validateResult.get("errors").put("name", "Profile already exists");
			response = Json.toJson(validateResult);
			return badRequest(response).as("application/json");
		}
		
		return ok(Json.toJson(profile)).as("application/json");	
	}
	
	public static Result connect(String profileId) {
		
		ObjectNode result = Json.newObject();
		
		GoogleAnalyticsProfile profile = (GoogleAnalyticsProfile) GoogleAnalyticsProfileDAO.getProfileById(Integer.parseInt(profileId));
		
		Cache.set(GoogleAnalyticsProfileDAO.CACHE_PROFILE_PREFIX + profileId, profile);
		
		result.put("authUrl", GoogleConnector.getAuthURL(profile));
		
		flash(GoogleAnalyticsProfileDAO.FLASH_PROFILE_ID, profileId);
		
		return ok(result);
	}
	
	public static Result disconnect(String profileId) {	
		
		GoogleAnalyticsProfile profile = (GoogleAnalyticsProfile) GoogleAnalyticsProfileDAO.getProfileById(Integer.parseInt(profileId));
		
		String cacheId = GoogleConnector.CACHE_CREDENTIAL_PREFIX + profileId;
		
		profile.setAccessToken(null);
		profile.setRefreshToken(null);
		profile.setConnected(false);
		
		Cache.remove(cacheId);
		
		try {
			GoogleAnalyticsProfileDAO.update(profile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ok(Json.toJson(profile));
	}

}
