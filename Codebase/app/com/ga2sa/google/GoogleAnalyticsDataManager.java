package com.ga2sa.google;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.Analytics.Data.Ga.Get;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.Column;
import com.google.api.services.analytics.model.Columns;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.Webproperties;

public class GoogleAnalyticsDataManager {
	
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	
	private static Analytics analytics;
	
	private static Analytics getAnalytics(GoogleCredential credential) {
		if (analytics == null) analytics = new Analytics.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build();
		return analytics;
	}
	
	public static Accounts getAccounts(GoogleCredential credential) {
		
		Accounts accounts = null;
		
		try {
			accounts = getAnalytics(credential).management().accounts().list().execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return accounts;
	}
	
	public static Webproperties getProperties(GoogleCredential credential, String accountId) {
		
		Webproperties properties = null;
		
		try {
			properties = getAnalytics(credential).management().webproperties().list(accountId).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return properties;
	}

	public static Profiles getProfiles(GoogleCredential credential, String accountId, String propertyId) {
		
		Profiles profiles = null;
		
		try {
			profiles = getAnalytics(credential).management().profiles().list(accountId, propertyId).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return profiles;
	}

	public static List<Map<String, String>> getDimensions(GoogleCredential credential) {
		
		List<Map<String, String>> filteredColumns = new ArrayList<Map<String, String>>();
		
		try {
			Columns columns = getAnalytics(credential).metadata().columns().list("ga").execute();
			for (Column c : columns.getItems()) {
				if (c.getAttributes().get("type").equals("DIMENSION") && c.getAttributes().get("status").equals("PUBLIC")) {
					c.getAttributes().put("id", c.getId());
					filteredColumns.add(c.getAttributes());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return filteredColumns;
	}

	public static List<Map<String, String>> getMetrics(GoogleCredential credential) {
		
		List<Map<String, String>> filteredColumns = new ArrayList<Map<String, String>>();
		
		try {
			Columns columns = getAnalytics(credential).metadata().columns().list("ga").execute();
			for (Column c : columns.getItems()) {
				if (c.getAttributes().get("type").equals("METRIC") && c.getAttributes().get("status").equals("PUBLIC")) {
					c.getAttributes().put("id", c.getId());
					filteredColumns.add(c.getAttributes());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return filteredColumns;
	}

	public static GaData getReport(GoogleCredential credential, String googleAnalyticsProperties) throws Exception {
		GaData result = null;
		Get query = null;
		JsonNode params = Json.parse(googleAnalyticsProperties);
		String metrics = params.get("metrics").isArray() ? StringUtils.join(params.get("metrics").elements(), ",").replace("\"", "") : params.get("metrics").asText().replace("\"", "");
		String dimensions = params.get("dimensions").isArray() ? StringUtils.join(params.get("dimensions").elements(), ",").replace("\"", "") : params.get("dimensions").asText().replace("\"", "");
		
		Logger.debug("QUERY    " + googleAnalyticsProperties);
		
		query = getAnalytics(credential).data().ga().get(
			"ga:" + params.get("analyticsProfile").textValue(),
			params.get("startDate").textValue(),
			params.get("endDate").textValue(),
			metrics
		).setDimensions(dimensions);
		
		if (!params.get("sort").asText().isEmpty()) {
			String sort = params.get("sort").isArray() ? StringUtils.join(params.get("sort").elements(), ",").replace("\"", "") : params.get("sort").asText().replace("\"", "");
			query.setSort(sort);
		}
		
		try	{
			result = query.execute();
		} catch (Exception exception) {
			throw exception;
		}
		
		return result;
	}
	
	/*public GaData getAnalyticData(Query query) throws IOException {
		GaData result = null;
		try {
			result = this.analytics.data().ga().get("ga:" + query.profile, query.startDate, query.endDate, StringUtils.join(query.metrics, ","))
					.setDimensions(StringUtils.join(query.dimensions, ",")).setMaxResults(25).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}*/

}
