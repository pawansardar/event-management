package com.eventmgmt.auth_service.validation.validator;

import com.eventmgmt.auth_service.validation.annotation.StrongPassword;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		if (password == null) {
			return false;
		}
		
		return password.length() >= 8 && password.length() <= 16 &&
				password.matches(".*\\d.*") &&
				password.matches(".*[A-Z].*") &&
				password.matches(".*[a-z].*");
	}

}
