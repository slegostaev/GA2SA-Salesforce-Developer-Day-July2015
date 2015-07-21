package controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import models.dao.GoogleAnalyticsProfileDAO;
import models.dao.JobDAO;
import models.dao.SalesforceAnalyticsProfileDAO;
import akka.actor.ActorRef;
import akka.actor.Props;

import com.fasterxml.jackson.databind.JsonNode;
import com.ga2sa.scheduler.Scheduler;
import com.ga2sa.scheduler.SchedulerManager;
import com.ga2sa.security.Access;
import com.ga2sa.utils.JsonUtil;

import play.Logger;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

@Access
public class Dashboard extends Controller {
	
	private static Map<String, JsonNode> params = new HashMap<String, JsonNode>();
	
	public static Result index() {
		params.clear();
		
		
		JsonNode googleProfiles = Json.toJson(GoogleAnalyticsProfileDAO.getConnectedProfiles());
		JsonNode salesforceProfiles = Json.toJson(SalesforceAnalyticsProfileDAO.getProfiles());
		JsonNode jobs = Json.toJson(JobDAO.getJobs());

		params.put("googleProfiles", JsonUtil.excludeFields(googleProfiles, GoogleAnalyticsProfileDAO.privateFields));
		params.put("salesforceProfiles", JsonUtil.excludeFields(salesforceProfiles, SalesforceAnalyticsProfileDAO.privateFields));
		params.put("jobs", jobs);

		return ok(views.html.pages.dashboard.index.render(params));
	}

}
