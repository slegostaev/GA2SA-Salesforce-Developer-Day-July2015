package controllers.settings;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ga2sa.security.Access;
import com.ga2sa.security.ApplicationSecurity;
import com.ga2sa.security.PasswordManager;
import com.ga2sa.security.UserGroup;

import models.User;
import models.dao.UserDAO;
import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

@Access(allowFor = UserGroup.ADMIN)
public class UsersSettings extends Controller {
	
	@Transactional
	public static Result add() {
		
		User user = (User) Json.fromJson(request().body().asJson(), User.class);

		user.setPassword(PasswordManager.encryptPassword(user.getPassword()));
		
		user.setRecordCreatedBy(ApplicationSecurity.getCurrentUser().getId());
		user.setRecordCreatedDateTime(new Timestamp(new Date().getTime()));
		
		HashMap<String, HashMap<String, String>> validateResult = UserDAO.validate(user);
		
		JsonNode response = Json.toJson(validateResult);
		
		if (!validateResult.get("errors").isEmpty()) return badRequest(response).as("application/json");
		
		try {
			UserDAO.save(user);
		} catch (Exception e) {
			Logger.debug(e.getMessage());
			validateResult.get("errors").put("username", "User already exists");
			response = Json.toJson(validateResult);
			return badRequest(response).as("application/json");
		}
		
		return ok(Json.toJson(user)).as("application/json");	
	}
	
	@Transactional
	public static Result delete(String profileId) {
		
		Long id = Long.parseLong(profileId);
		
		User user = (User) UserDAO.getUserById(id);
		
		UserDAO.delete(user);
		
		return ok();
	}
	
	@Transactional
	public static Result update(String profileId) {
		
		JsonNode jsonUser = request().body().asJson();
		
		User user = (User) Json.fromJson(jsonUser, User.class);
		
		HashMap<String, HashMap<String, String>> validateResult = UserDAO.validate(user);
		
		JsonNode response = Json.toJson(validateResult);
		
		if (!validateResult.get("errors").isEmpty()) return badRequest(response).as("application/json");
		
		try {
			UserDAO.update(user);
		} catch (Exception e) {
			validateResult.get("errors").put("username", "User already exists");
			response = Json.toJson(validateResult);
			return badRequest(response).as("application/json");
		}
		
		return ok(Json.toJson(user)).as("application/json");	
	}

}
