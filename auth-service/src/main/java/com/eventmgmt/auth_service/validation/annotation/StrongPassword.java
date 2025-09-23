package com.eventmgmt.auth_service.validation.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.eventmgmt.auth_service.validation.validator.StrongPasswordValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = StrongPasswordValidator.class)
public @interface StrongPassword {
	String message() default "Password must be between 8 to 16 characters, and include atleast one uppercase, one lowercase and one digit";
	
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
