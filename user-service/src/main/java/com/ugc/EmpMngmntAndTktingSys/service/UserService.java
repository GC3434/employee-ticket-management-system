package com.ugc.EmpMngmntAndTktingSys.service;

import com.ugc.EmpMngmntAndTktingSys.DTO.CreateManagerRequest;
import com.ugc.EmpMngmntAndTktingSys.DTO.RegisterRequest;
import com.ugc.EmpMngmntAndTktingSys.DTO.UpdateUserRequest;
import com.ugc.EmpMngmntAndTktingSys.DTO.UserResponse;
import com.ugc.EmpMngmntAndTktingSys.exception.UsernameAlreadyExistsException;
import com.ugc.EmpMngmntAndTktingSys.mapper.UserMapper;
import com.ugc.EmpMngmntAndTktingSys.model.Role;
import com.ugc.EmpMngmntAndTktingSys.model.RoleType;
import com.ugc.EmpMngmntAndTktingSys.model.User;
import com.ugc.EmpMngmntAndTktingSys.repo.RolesRepo;
import com.ugc.EmpMngmntAndTktingSys.repo.UserRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepo userRepo;
    private final RolesRepo rolesRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private User createUserWithRole(User user, RoleType roleType){
        if (userRepo.findByUserName(user.getUserName()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists!");        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = rolesRepo.findByRoleName(roleType).orElseThrow();
        user.setRoles(Set.of(role));
        return userRepo.save(user);
    }

    public User createUser(RegisterRequest request) {
        User user = new User();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setDepartment(request.getDepartment());

        return createUserWithRole(user,RoleType.ROLE_EMP);
    }

    public UserResponse createManager(CreateManagerRequest managerRequest) {
        User user = new User();
        user.setUserName(managerRequest.getUsername());
        user.setPassword(managerRequest.getPassword());
        user.setEmail(managerRequest.getEmail());
        user.setDepartment(managerRequest.getDepartment());
        return userMapper.mapToUserResponse(createUserWithRole(user,RoleType.ROLE_MANAGER));

    }

    @Cacheable(value = "users", key = "#id", unless = "#result == null")
    public UserResponse getUserById(Long id) {

        System.out.println("Fetching from MySQL...");

        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.mapToUserResponse(user);
    }

    public UserResponse getUserByUserName(String userName) {

        User user = userRepo.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.mapToUserResponse(user);
    }

    public List<UserResponse> getEmployees() {
        return userRepo.findAll()
                .stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getRoleName() == RoleType.ROLE_EMP))
                .map(userMapper :: mapToUserResponse)
                .toList();
    }

    @CachePut(value = "users", key = "#id")
    public UserResponse updateUserById(Long id, UpdateUserRequest updateUserRequest) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setDepartment(updateUserRequest.getDepartment());
        user.setEmail(updateUserRequest.getEmail());
        System.out.println("Updating MySQL & Redis...");
        return userMapper.mapToUserResponse(userRepo.save(user));
    }

    @CacheEvict(value = "users", key = "#id")
    public String deleteUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("deleting from MySQL & Redis...");
        userRepo.deleteById(id);
        return "deleted Successfully...";
    }

}
