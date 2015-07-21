package com.ga2sa.salesforce;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;

import org.apache.commons.io.FilenameUtils;

import models.SalesforceAnalyticsProfile;

import com.sforce.dataset.loader.DatasetLoader;
import com.sforce.dataset.loader.DatasetLoaderException;
import com.sforce.dataset.util.DatasetUtils;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;

import controllers.SessionManager;

public class SalesforceDataManager {
	
	public static void uploadData(SalesforceAnalyticsProfile profile, File report) throws Exception {
				
		String dataset = FilenameUtils.removeExtension(report.getName());
	    String datasetLabel = null;
	    String app = profile.getApplicationName();
	    String username = profile.getUsername();
	    String password = profile.getPassword();
	    String token = null;
	    String sessionId = null;
	    String endpoint = null;
	    String action = null;
	    String inputFile = report.getAbsolutePath();
	    String uploadFormat = "csv";
	    CodingErrorAction codingErrorAction = CodingErrorAction.REPORT;   
	    Charset fileCharset = Charset.forName("UTF-8");
	    String Operation = "Overwrite";
	    boolean useBulkAPI = false;
	    
	    PartnerConnection partnerConnection = null;
	    
	    try {
	    	partnerConnection = DatasetUtils.login(0, username, password, token, endpoint, sessionId, true);
        } catch (ConnectionException | MalformedURLException e) {
        	throw new ConnectionException();
        }
	    
	    try {
	    	DatasetLoader.uploadDataset(inputFile, uploadFormat, codingErrorAction, fileCharset, dataset, app, datasetLabel, Operation, useBulkAPI, partnerConnection, System.out);
	    } catch (DatasetLoaderException e) {
	    	throw new DatasetLoaderException(e.getMessage());
	    }
		
	}
}
