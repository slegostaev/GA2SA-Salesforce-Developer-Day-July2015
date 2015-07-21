package controllers;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import akka.actor.ActorRef;

import com.google.common.io.Files;
import com.fasterxml.jackson.databind.JsonNode;
import com.ga2sa.salesforce.SalesforceDataManager;
import com.ga2sa.scheduler.Scheduler;
import com.ga2sa.security.Access;
import com.ga2sa.security.ApplicationSecurity;

import models.GoogleAnalyticsProfile;
import models.GoogleAnalyticsReport;
import models.Job;
import models.SalesforceAnalyticsProfile;
import models.dao.GoogleAnalyticsProfileDAO;
import models.dao.GoogleAnalyticsReportDAO;
import models.dao.JobDAO;
import models.dao.SalesforceAnalyticsProfileDAO;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Controller;

@Access
public class JobsManager extends Controller {
	
	public static Result create () {
		
		JsonNode requestData = request().body().asJson();
		
		GoogleAnalyticsProfile gaProfile = GoogleAnalyticsProfileDAO.getProfileById(requestData.get("googleAnalyticsProfile").intValue());
		SalesforceAnalyticsProfile saProfile = SalesforceAnalyticsProfileDAO.getProfileById(requestData.get("salesforceAnalyticsProfile").intValue());
		
		Job job = new Job();
		job.setName(requestData.get("name").textValue());
		job.setGoogleAnalyticsProfile(gaProfile);
		job.setSalesforceAnalyticsProfile(saProfile);
		job.setGoogleAnalyticsProperties(requestData.get("googleAnalyticsProperties").toString());
		job.setUser(ApplicationSecurity.getCurrentUser());
		job.setStatus("PENDING");
		
		if (!requestData.get("repeatPeriod").isNull()) job.setRepeatPeriod(requestData.get("repeatPeriod").asText());
		
		
		if (!requestData.get("startTime").isNull()) job.setStartTime(new Timestamp(requestData.get("startTime").asLong()));
		else job.setStartTime(new Timestamp(new Date().getTime()));
		
		if (!requestData.get("includePreviousData").isNull()) job.setIncludePreviousData(requestData.get("includePreviousData").asBoolean());
		
		HashMap<String, HashMap<String, String>> validateResult = JobDAO.validate(job);
		
		JsonNode response = Json.toJson(validateResult);
		
		if (!validateResult.get("errors").isEmpty()) return badRequest(response).as("application/json");
		
		try {
			JobDAO.save(job);
		} catch (Exception e) {
			Logger.debug(e.getMessage());
			validateResult.get("errors").put("name", "Job already exists");
			response = Json.toJson(validateResult);
			return badRequest(response).as("application/json");
		}
		
		Scheduler.getInstance().tell("update", ActorRef.noSender());
		
		return ok(Json.toJson(job)).as("application/json");
	}
}
