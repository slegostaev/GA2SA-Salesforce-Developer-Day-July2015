package models;

import java.io.Serializable;
import java.sql.Array;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import models.Job;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;


/**
 * The persistent class for the google_analytics_profiles database table.
 * 
 */
//@JsonFilter("myFilter")
@Entity
@Table(name="google_analytics_profiles")
@NamedQuery(name="GoogleAnalyticsProfile.findAll", query="SELECT g FROM GoogleAnalyticsProfile g WHERE g.name <> 'default' ORDER BY g.id ASC")
public class GoogleAnalyticsProfile implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="GOOGLE_ANALYTICS_PROFILES_ID_GENERATOR", sequenceName="GOOGLE_ANALYTICS_PROFILES_ID_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="GOOGLE_ANALYTICS_PROFILES_ID_GENERATOR")
	private Integer id;
	
	@NotNull
	@NotEmpty
	@URL
	@Column(name="auth_provider_x509_cert_url")
	private String authProviderX509CertUrl;
	
	@NotNull
	@NotEmpty
	@URL
	@Column(name="auth_uri")
	private String authUri;
	
	@NotNull
	@NotEmpty
	@Email
	@Column(name="client_email")
	private String clientEmail;
	
	@NotNull
	@NotEmpty
	@Column(name="client_id")
	private String clientId;
	
	@NotNull
	@NotEmpty
	@Column(name="client_secret")
	private String clientSecret;
	
	@NotNull
	@NotEmpty
	@URL
	@Column(name="client_x509_cert_url")
	private String clientX509CertUrl;
	
	@NotNull
	@NotEmpty
	private String name;
	
	@NotNull
	@Column(name="redirect_uris")
	@Type(type="com.ga2sa.hibernate.types.ArrayUserType")
	private String [] redirectUris;
	
	@NotNull
	@NotEmpty
	@URL
	@Column(name="token_uri")
	private String tokenUri;
	
	private Boolean connected;

	@JsonIgnore
	@Column(name="google_user_id")
	private String googleUserId;
	
	@JsonIgnore
	@Column(name="access_token")
	private String accessToken;
	
	@JsonIgnore
	@Column(name="refresh_token")
	private String refreshToken;
	
	//bi-directional many-to-one association to Job
	@JsonIgnore
	@OneToMany(mappedBy="googleAnalyticsProfile")
	private List<Job> jobs;

	public GoogleAnalyticsProfile() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAuthProviderX509CertUrl() {
		return this.authProviderX509CertUrl;
	}

	public void setAuthProviderX509CertUrl(String authProviderX509CertUrl) {
		this.authProviderX509CertUrl = authProviderX509CertUrl;
	}

	public String getAuthUri() {
		return this.authUri;
	}

	public void setAuthUri(String authUri) {
		this.authUri = authUri;
	}

	public String getClientEmail() {
		return this.clientEmail;
	}

	public void setClientEmail(String clientEmail) {
		this.clientEmail = clientEmail;
	}

	public String getClientId() {
		return this.clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return this.clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getClientX509CertUrl() {
		return this.clientX509CertUrl;
	}

	public void setClientX509CertUrl(String clientX509CertUrl) {
		this.clientX509CertUrl = clientX509CertUrl;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String [] getRedirectUris() {
		return this.redirectUris;
	}

	public void setRedirectUris(String [] redirectUris) {
		this.redirectUris = redirectUris;
	}

	public String getTokenUri() {
		return this.tokenUri;
	}

	public void setTokenUri(String tokenUri) {
		this.tokenUri = tokenUri;
	}

	public Boolean getConnected() {
		return connected;
	}

	public void setConnected(Boolean connected) {
		this.connected = connected;
	}

	public String getGoogleUserId() {
		return googleUserId;
	}

	public void setGoogleUserId(String googleUserId) {
		this.googleUserId = googleUserId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	public List<Job> getJobs() {
		return this.jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

	public Job addJob(Job job) {
		getJobs().add(job);
		job.setGoogleAnalyticsProfile(this);

		return job;
	}

	public Job removeJob(Job job) {
		getJobs().remove(job);
		job.setGoogleAnalyticsProfile(null);

		return job;
	}

}