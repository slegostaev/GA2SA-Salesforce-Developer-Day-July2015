package controllers.settings;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.ga2sa.security.Access;
import com.ga2sa.security.PasswordManager;
import com.ga2sa.security.UserGroup;

import models.User;
import models.dao.UserDAO;
import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

@Access
public class ProfileSettings extends Controller {
		
	@Transactional
	public static Result update(String userId) {
		
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
