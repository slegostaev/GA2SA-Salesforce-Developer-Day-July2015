package com.ga2sa.security;

import com.fasterxml.jackson.databind.node.ObjectNode;

import models.User;
import play.libs.F;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

public class AccessAction extends Action<Access> {

	@Override
	public Promise<Result> call(Context ctx) throws Throwable {
		
		User user = ApplicationSecurity.getCurrentUser();
		
		UserGroup userGroup = configuration.allowFor();
		
		ObjectNode result = Json.newObject();
		result.put("error", 401);
		result.put("message", "You don't have permissions");
		
		if (user == null) return F.Promise.pure((Result) redirect(controllers.routes.Authorization.ga2saSignIn()));
		
		if (!user.getRole().equals(UserGroup.ADMIN.name()) && userGroup.equals((UserGroup.ADMIN))) return F.Promise.pure((Result) unauthorized(result));
		
		return delegate.call(ctx);
		
	}

}
