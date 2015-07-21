package com.ga2sa.security;

import models.User;

public class CheckAccesTemplates {
	
	public static boolean hasAccess() {
		
		User user = ApplicationSecurity.getCurrentUser();
		
		if (user == null) return false;
		if (!user.getRole().equals(UserGroup.ADMIN.name())) return false;
		
		return true;
	}
	
}
