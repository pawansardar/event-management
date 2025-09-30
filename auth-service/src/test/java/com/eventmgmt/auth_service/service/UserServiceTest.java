package com.eventmgmt.auth_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.eventmgmt.auth_service.dto.request.UpdateUserRequest;
import com.eventmgmt.auth_service.exception.custom.ResourceConflictException;
import com.eventmgmt.auth_service.exception.custom.ResourceNotFoundException;
import com.eventmgmt.auth_service.exception.custom.RoleOperationNotAllowedException;
import com.eventmgmt.auth_service.model.Role;
import com.eventmgmt.auth_service.model.User;
import com.eventmgmt.auth_service.repository.RoleRepository;
import com.eventmgmt.auth_service.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@Mock
	private UserRepository userRepo;
	
	@Mock
	private RoleRepository roleRepo;
	
	@InjectMocks
	private UserService userService;
	
	@Test
	void getUser_shouldReturnUser_whenExists() {
		User user = new User();
		user.setId(1L);
		when(userRepo.findById(1L)).thenReturn(Optional.of(user));
		
		User result = userService.getUser(1L);
		
		assertNotNull(result);
		assertThat(result.getId()).isEqualTo(1L);
		
		verify(userRepo).findById(1L);
	}
	
	@Test
	void getUser_shouldThrow_whenNotFound() {
		when(userRepo.findById(2L)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> userService.getUser(2L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("User not found");
		
		verify(userRepo).findById(2L);
	}
	
	@Test
	void updateUser_shouldUpdateAndReturn_whenEmailChanged() {
		User user = new User();
		user.setId(1L);
		user.setName("Pawan");
		user.setEmail("example@email.com");
		when(userRepo.findById(1L)).thenReturn(Optional.of(user));
		
		UpdateUserRequest request = new UpdateUserRequest("New Name", "new@email.com");
		when(userRepo.findByEmail("new@email.com")).thenReturn(Optional.empty());
		
		User updatedUser = userService.updateUser(1L, request);
		
		assertThat(updatedUser.getName()).isEqualTo("New Name");
		assertThat(updatedUser.getEmail()).isEqualTo("new@email.com");
		
		verify(userRepo).findById(1L);
		verify(userRepo).findByEmail("new@email.com");
		verify(userRepo).save(user);
	}
	
	@Test
	void updateUser_shouldUpdateAndReturn_whenEmailNotChanged() {
		User user = new User();
		user.setId(1L);
		user.setName("Pawan");
		user.setEmail("example@email.com");
		when(userRepo.findById(1L)).thenReturn(Optional.of(user));
		
		UpdateUserRequest request = new UpdateUserRequest("New Name", "example@email.com");
		
		User updatedUser = userService.updateUser(1L, request);
		
		assertThat(updatedUser.getName()).isEqualTo("New Name");
		assertThat(updatedUser.getEmail()).isEqualTo("example@email.com");
		
		verify(userRepo).findById(1L);
		verify(userRepo).save(user);
	}
	
	@Test
	void updateUser_shouldThrow_whenEmailAlreadyExists() {
		User user = new User();
		user.setId(1L);
		user.setName("Pawan");
		user.setEmail("example@email.com");
		when(userRepo.findById(1L)).thenReturn(Optional.of(user));
		
		UpdateUserRequest request = new UpdateUserRequest("New Name", "another_user@email.com");
		when(userRepo.findByEmail("another_user@email.com")).thenReturn(Optional.of(new User()));
		
		assertThrows(ResourceConflictException.class,
				() -> userService.updateUser(1L, request),
				"Expected updateUser() to throw, but it didn't");
		
		verify(userRepo).findById(1L);
		verify(userRepo).findByEmail("another_user@email.com");
		verify(userRepo, never()).save(user);
	}
	
	@Test
	void updateUser_shouldThrow_whenSaveViolatesConstraint() {
		User user = new User();
		user.setId(1L);
		user.setEmail("example@email.com");
		when(userRepo.findById(1L)).thenReturn(Optional.of(user));
		
		UpdateUserRequest request = new UpdateUserRequest("Name", "example@email.com");
		
		when(userRepo.save(user)).thenThrow(new DataIntegrityViolationException("Duplicate"));
		
		assertThrows(ResourceConflictException.class,
				() -> userService.updateUser(1L, request),
				"Expected updateUser() to throw, but it didn't");
	}
	
	@Test
	void updateUser_shouldThrow_whenNotFound() {
		when(userRepo.findById(2L)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> userService.getUser(2L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("User not found");
		
		verify(userRepo).findById(2L);
	}
	
	@Test
	void deleteUser_shouldDelete_whenExists() {
		User user = new User();
		user.setId(1L);
		when(userRepo.findById(1L)).thenReturn(Optional.of(user));
		
		userService.deleteUser(1L);
		
		verify(userRepo).findById(1L);
		verify(userRepo).deleteById(1L);
	}
	
	@Test
	void deleteUser_shouldThrow_whenNotFound() {
		when(userRepo.findById(1L)).thenReturn(Optional.empty());
		
		assertThrows(ResourceNotFoundException.class,
				() -> userService.deleteUser(1L));
		
		verify(userRepo).findById(1L);
		verify(userRepo, never()).deleteById(anyLong());
	}
	
	@Test
	void assignRole_shouldSaveRole() {
		User user = new User();
		user.setId(1L);
		Role role = new Role();
		role.setId(3);
		user.setRoles(new HashSet<>(Set.of(role)));
		
		when(userRepo.findById(1L)).thenReturn(Optional.of(user));
		Role newRole = new Role();
		newRole.setId(2);
		when(roleRepo.findById(2)).thenReturn(Optional.of(newRole));
		when(userRepo.save(any(User.class))).thenReturn(user);
		
		User savedUser = userService.assignRole(1L, 2);
		
		assertThat(savedUser.getRoles()).contains(role);
		assertThat(savedUser.getRoles()).contains(newRole);
		
		verify(userRepo).save(user);
	}
	
	@Test
	void assignRole_shouldThrow_whenRoleAlreadyExists() {
		User user = new User();
		user.setId(1L);
		Role role = new Role();
		role.setId(3);
		user.setRoles(new HashSet<>(Set.of(role)));
		
		when(userRepo.findById(1L)).thenReturn(Optional.of(user));
		when(roleRepo.findById(3)).thenReturn(Optional.of(role));
		
		assertThrows(ResourceConflictException.class,
				() -> userService.assignRole(1L, 3),
				"Expected assignRole() to throw, but it didn't");
		
		verify(userRepo, never()).save(user);
	}
	
	@Test
	void removeRole_shouldRemove() {
		User user = new User();
		user.setId(1L);
		
		Role role = new Role();
		role.setId(3);
		role.setName("ROLE_ATTENDEE");
		
		Role anotherRole = new Role();
		anotherRole.setId(2);
		anotherRole.setName("ROLE_ANOTHER");
		
		user.setRoles(new HashSet<>(Set.of(role, anotherRole)));
		
		when(userRepo.findById(1L)).thenReturn(Optional.of(user));
		when(roleRepo.findById(2)).thenReturn(Optional.of(anotherRole));
		when(userRepo.save(any(User.class))).thenReturn(user);
		
		User savedUser = userService.removeRole(1L, 2);
		
		assertThat(savedUser.getRoles()).contains(role);
		assertThat(savedUser.getRoles()).doesNotContain(anotherRole);
		
		verify(userRepo).save(user);
	}
	
	@ParameterizedTest
	@CsvSource({
	    "ROLE_ATTENDEE", 
	    "ROLE_EMPTY"
	})
	void removeRole_shouldThrowException_whenRoleCannotBeRemoved(String roleName) {
	    User user = new User();
	    user.setId(1L);

	    Role role = new Role();
	    role.setId(3);
	    role.setName(roleName);

	    // For "ROLE_EMPTY", we only add 1 role i.e. size < 2
	    if (roleName.equals("ROLE_EMPTY")) {
	        user.setRoles(new HashSet<>(Set.of(role)));
	    } else {
	        Role anotherRole = new Role();
	        anotherRole.setId(2);
	        anotherRole.setName("ROLE_OTHER");
	        user.setRoles(new HashSet<>(Set.of(role, anotherRole)));
	    }

	    when(userRepo.findById(1L)).thenReturn(Optional.of(user));
	    when(roleRepo.findById(3)).thenReturn(Optional.of(role));
	    
	    assertThatThrownBy(() -> userService.removeRole(1L, 3))
	        .isInstanceOf(RoleOperationNotAllowedException.class)
	        .hasMessage("This role cannot be removed!");
	}

}
