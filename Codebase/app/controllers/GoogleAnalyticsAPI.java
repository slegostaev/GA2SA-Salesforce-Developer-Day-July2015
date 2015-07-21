package controllers;

import models.GoogleAnalyticsProfile;
import models.GoogleAnalyticsReport;
import models.Job;
import models.dao.GoogleAnalyticsProfileDAO;
import models.dao.GoogleAnalyticsReportDAO;

import com.fasterxml.jackson.databind.JsonNode;
import com.ga2sa.google.GoogleAnalyticsDataManager;
import com.ga2sa.google.GoogleConnector;
import com.ga2sa.google.Report;
import com.ga2sa.security.Access;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Controller;
import play.cache.Cache;

@Access
public class GoogleAnalyticsAPI extends Controller {
		
	private static GoogleCredential getCredetial(String profileId) {
		String cacheId = GoogleConnector.CACHE_CREDENTIAL_PREFIX + profileId;
		GoogleCredential credential = (GoogleCredential)Cache.get(cacheId);
		if (credential == null) {
			GoogleAnalyticsProfile profile = GoogleAnalyticsProfileDAO.getProfileById(Integer.parseInt(profileId));
			credential = GoogleConnector.getCredentials(profile);
			Cache.set(cacheId, credential);
		}
		return credential;
	}
	
	public static Result getAccounts(String profileId) {
		JsonNode result = Json.toJson(GoogleAnalyticsDataManager.getAccounts(getCredetial(profileId))).get("items");
		if (result == null) result = Json.newObject();
		return ok(result).as("application/json");
	}
	
	public static Result getProperties(String profileId, String accountId) {
		JsonNode result = Json.toJson(GoogleAnalyticsDataManager.getProperties(getCredetial(profileId), accountId)).get("items");
		if (result == null) result = Json.newObject();
		return ok(result).as("application/json");
	}
	
	public static Result getProfiles(String profileId, String accountId, String propertyId) {
		JsonNode result = Json.toJson(GoogleAnalyticsDataManager.getProfiles(getCredetial(profileId), accountId, propertyId)).get("items");
		if (result == null) result = Json.newObject();
		return ok(result).as("application/json");
	}
	
	public static Result getDimensions(String profileId) {
		JsonNode result = Json.toJson(GoogleAnalyticsDataManager.getDimensions(getCredetial(profileId)));
		if (result == null) result = Json.newObject();
		return ok(result).as("application/json");
	}
	
	public static Result getMetrics(String profileId) {
		JsonNode result = Json.toJson(GoogleAnalyticsDataManager.getMetrics(getCredetial(profileId)));
		if (result == null) result = Json.newObject();
		return ok(result).as("application/json");
	}
	
	public static Report getReport(Job job) throws Exception {
		
		String nameReport = job.getName();
		String profileId = job.getGoogleAnalyticsProfile().getId().toString();
		String properties = job.getGoogleAnalyticsProperties();
		
		Report report = null;
		
		GoogleCredential credential = getCredetial(profileId);
		
		JsonNode metrics = Json.toJson(GoogleAnalyticsDataManager.getMetrics(credential));
		JsonNode dimensions = Json.toJson(GoogleAnalyticsDataManager.getDimensions(credential));
		
		try {
			report = new Report( nameReport, GoogleAnalyticsDataManager.getReport(credential, properties), metrics, dimensions);
		} catch (Exception exception) {
			throw exception;
		}
		
		return report;
	}
}
