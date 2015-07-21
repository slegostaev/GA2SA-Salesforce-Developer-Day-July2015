package com.ga2sa.google;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Play;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.GaData.ColumnHeaders;


public class Report {

	private String name;
	private List<String> headers;
	private List<List<String>> data;
	private List<Integer> dateColumns;

	public List<List<String>> getData() {
		return data;
	}

	public void setData(List<List<String>> data) {
		this.data = data;
	}

	public List<String> getHeaders() {
		return headers;
	}

	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Report(String name, GaData data, JsonNode metrics, JsonNode dimensions) {

		this.name = name;
		this.headers = new ArrayList<String>();
		this.dateColumns = new ArrayList<Integer>();
		
		for (ColumnHeaders item : data.getColumnHeaders()) {
			
			
			
			if (item.getColumnType().equals("DIMENSION")) {
				dimensions.forEach(
						(dimension) -> {
							if (dimension.get("id").asText().equals(item.getName())) 
								this.headers.add(dimension.get("uiName").asText());
						}
				);
			} else {
				metrics.forEach(
						(metric) -> {
							if (metric.get("id").asText().equals(item.getName())) 
								this.headers.add(metric.get("uiName").asText());
						}
				);
			}
			
			// Save index date column for formatting in CSV file
			if (item.getName().equals("ga:date")) this.dateColumns.add(data.getColumnHeaders().indexOf(item));
			
		}
		
		this.data = data.getRows();
	}

	public File toCSV() {
		
		String root = Play.isDev() ? "" : "/app/target/universal/stage/";
		File csv = new File(root + this.name + ".csv");
		
		try {
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(csv));

			String header = StringUtils.join(this.headers, ",");

			bw.write(header);
			bw.newLine();
			
			this.data.forEach((row) -> {
				
				// formatting all date columns
				this.dateColumns.forEach((indexColumn) -> {
					row.set(indexColumn, convertToIsoDate(row.get(indexColumn)));
				});
				
				try {
					bw.write(StringUtils.join(row.toArray(), ","));
					bw.newLine();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			});
			
			bw.flush();
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return csv;
	}
	
	public File addToCSV(byte[] prevCSV) {
		
		String root = Play.isDev() ? "" : "/app/target/universal/stage/";
		File csv = new File(root + this.name + ".csv");
		
		try {
			
			FileUtils.writeByteArrayToFile(csv, prevCSV);
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(csv, true));
			
			this.data.forEach((row) -> {
				
				// formatting all date columns
				this.dateColumns.forEach((indexColumn) -> {
					row.set(indexColumn, convertToIsoDate(row.get(indexColumn)));
				});
				
				try {
					bw.write(StringUtils.join(row.toArray(), ","));
					Logger.debug("# " + StringUtils.join(row.toArray(), ","));
					bw.newLine();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			});
			
			bw.flush();
			bw.close();
	
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return csv;
	}
	
	private String convertToIsoDate(String gaDate) {
		String year = gaDate.substring(0, 4);
		String month = gaDate.substring(4, 6);
		String day = gaDate.substring(6, 8);
		return year + "-" + month + "-" + day;
	}
}
