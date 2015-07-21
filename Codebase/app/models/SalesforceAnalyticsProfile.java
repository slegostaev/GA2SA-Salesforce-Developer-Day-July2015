package models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.Job;


/**
 * The persistent class for the salesforce_analytics_profiles database table.
 * 
 */
@Entity
@Table(name="salesforce_analytics_profiles")
@NamedQuery(name="SalesforceAnalyticsProfile.findAll", query="SELECT s FROM SalesforceAnalyticsProfile s ORDER BY s.id ASC")
public class SalesforceAnalyticsProfile implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="SALESFORCE_ANALYTICS_PROFILES_ID_GENERATOR", sequenceName="SALESFORCE_ANALYTICS_PROFILES_ID_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SALESFORCE_ANALYTICS_PROFILES_ID_GENERATOR")
	private Integer id;
	
	@NotNull
	@NotEmpty
	@Column(name="application_name")
	private String applicationName;
	
	@NotNull
	@NotEmpty
	private String password;
	
	@NotNull
	@NotEmpty
	private String username;
	
	@NotNull
	@NotEmpty
	private String name;
	
	//bi-directional many-to-one association to Job
	@JsonIgnore
	@OneToMany(mappedBy="salesforceAnalyticsProfile")
	private List<Job> jobs;

	public SalesforceAnalyticsProfile() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getApplicationName() {
		return this.applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<Job> getJobs() {
		return this.jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

	public Job addJob(Job job) {
		getJobs().add(job);
		job.setSalesforceAnalyticsProfile(this);

		return job;
	}

	public Job removeJob(Job job) {
		getJobs().remove(job);
		job.setSalesforceAnalyticsProfile(null);

		return job;
	}

}