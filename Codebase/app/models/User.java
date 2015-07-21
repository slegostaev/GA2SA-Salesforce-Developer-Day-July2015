package models;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.ga2sa.security.PasswordManager;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;

import models.Job;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import play.Logger;


/**
 * The persistent class for the users database table.
 * 
 */
@Entity
@Table(name="users", uniqueConstraints=@UniqueConstraint(columnNames = { "username" }))
@NamedQuery(name="User.findAll", query="SELECT u FROM User u ORDER BY u.id ASC")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="USERS_ID_GENERATOR", sequenceName="USERS_ID_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="USERS_ID_GENERATOR")
	private Long id;

	@NotEmpty
	@NotNull
	@Email
	@Column(name="email_address")
	private String emailAddress;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="is_active")
	private Boolean isActive;
	
	@Column(name="last_login_date_time")
	private Timestamp lastLoginDateTime;

	@Column(name="last_name")
	private String lastName;
	
	@NotEmpty
	@NotNull
	private String password;
	
	@NotNull
	@Column(name="record_created_by")
	private Long recordCreatedBy;
	
	@NotNull
	@Column(name="record_created_date_time")
	private Timestamp recordCreatedDateTime;
	
	@Column(name="record_modified_by")
	private Long recordModifiedBy;
	
	@Column(name="record_modified_date_time")
	private Timestamp recordModifiedDateTime;

	@NotEmpty
	@NotNull
	private String role;
	
	@NotEmpty
	@NotNull
	private String username;
	
	//bi-directional many-to-one association to Job
	@JsonIgnore
	@OneToMany(mappedBy="user")
	private List<Job> jobs;

	public User() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmailAddress() {
		return this.emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Boolean getIsActive() {
		return this.isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Timestamp getLastLoginDateTime() {
		return this.lastLoginDateTime;
	}

	public void setLastLoginDateTime(Timestamp lastLoginDateTime) {
		this.lastLoginDateTime = lastLoginDateTime;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getRecordCreatedBy() {
		return this.recordCreatedBy;
	}

	public void setRecordCreatedBy(Long recordCreatedBy) {
		this.recordCreatedBy = recordCreatedBy;
	}

	public Timestamp getRecordCreatedDateTime() {
		return this.recordCreatedDateTime;
	}

	public void setRecordCreatedDateTime(Timestamp recordCreatedDateTime) {
		this.recordCreatedDateTime = recordCreatedDateTime;
	}

	public Long getRecordModifiedBy() {
		return this.recordModifiedBy;
	}

	public void setRecordModifiedBy(Long recordModifiedBy) {
		this.recordModifiedBy = recordModifiedBy;
	}

	public Timestamp getRecordModifiedDateTime() {
		return this.recordModifiedDateTime;
	}

	public void setRecordModifiedDateTime(Timestamp recordModifiedDateTime) {
		this.recordModifiedDateTime = recordModifiedDateTime;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	@JsonProperty("password")
	private String defaultPassword () {
		return PasswordManager.PASSWORD_TMP;
	}
	
	public List<Job> getJobs() {
		return this.jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

	public Job addJob(Job job) {
		getJobs().add(job);
		job.setUser(this);

		return job;
	}

	public Job removeJob(Job job) {
		getJobs().remove(job);
		job.setUser(null);

		return job;
	}

}