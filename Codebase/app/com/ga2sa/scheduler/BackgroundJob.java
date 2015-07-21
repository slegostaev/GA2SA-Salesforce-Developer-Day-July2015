package com.ga2sa.scheduler;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.ga2sa.salesforce.SalesforceDataManager;
import com.google.common.io.Files;

import controllers.GoogleAnalyticsAPI;
import play.Logger;
import play.libs.Json;
import models.GoogleAnalyticsReport;
import models.Job;
import models.dao.GoogleAnalyticsReportDAO;
import models.dao.JobDAO;
import akka.actor.UntypedActor;

public class BackgroundJob extends UntypedActor{

	@Override
	public void onReceive(Object obj) throws Exception {
		if (obj instanceof Job) {
			
			Job job = (Job) obj;
			
			File csvReport = null;
			
			Logger.debug("Job started: " + job.getName());
			
			GoogleAnalyticsReport previousReport = GoogleAnalyticsReportDAO.getReportByJobId(job.getId());
			
			if (job.isRepeated()) {
				
				Integer duration = (job.getRepeatPeriod().equals("week")) ? 7 : 1;
				Integer timeUnit = (job.getRepeatPeriod().equals("week") || job.getRepeatPeriod().equals("day") ) ? Calendar.DATE : Calendar.MONTH;
					
				if (job.needIncludePreviousData() && previousReport != null) {
					
					JsonNode changedProperties = Json.parse(job.getGoogleAnalyticsProperties());
					
					Calendar startDateForReport = Calendar.getInstance();
					Calendar endDateForReport = Calendar.getInstance();
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
					
					try {
						endDateForReport.setTime(sdf.parse(changedProperties.get("endDate").asText()));
						startDateForReport.setTime(endDateForReport.getTime());
						startDateForReport.add(Calendar.DATE, 1);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					endDateForReport.add(timeUnit, duration);
					
					ObjectNode node = (ObjectNode) changedProperties;
					node.set("startDate", new TextNode(sdf.format(startDateForReport.getTime())));
					node.set("endDate", new TextNode(sdf.format(endDateForReport.getTime())));
					
					job.setGoogleAnalyticsProperties(node.toString());
					
					csvReport = GoogleAnalyticsAPI.getReport(job).addToCSV(previousReport.getData());
				
				} else {
					csvReport = GoogleAnalyticsAPI.getReport(job).toCSV();
				}
			} else {
				csvReport = GoogleAnalyticsAPI.getReport(job).toCSV();
			}
			
			Logger.debug("QUERY   " + job.getGoogleAnalyticsProperties());
			
			try {
				if (job.isRepeated() && job.needIncludePreviousData() && previousReport != null) {
					previousReport.setData(IOUtils.toByteArray(Files.asByteSource(csvReport).openStream()));
					GoogleAnalyticsReportDAO.update(previousReport);
				} else {
					GoogleAnalyticsReportDAO.save(new GoogleAnalyticsReport(job.getId(), IOUtils.toByteArray(Files.asByteSource(csvReport).openStream())));
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				job.setStatus("FAIL");
				job.setErrors(e1.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				SalesforceDataManager.uploadData(job.getSalesforceAnalyticsProfile(), csvReport);
				job.setStatus("OK");			
			} catch (Exception e) {
				e.printStackTrace();
				job.setStatus("FAIL");
				job.setErrors(e.getMessage());
			}
			
			job.setEndTime(new Timestamp(new Date().getTime()));
			
			try {
				JobDAO.update(job);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			csvReport.delete();
		}
	}
	
}
