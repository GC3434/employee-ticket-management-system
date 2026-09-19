package com.ugc.EmpMngmntAndTktingSys.controller;

import com.ugc.EmpMngmntAndTktingSys.DTO.RegisterRequest;
import com.ugc.EmpMngmntAndTktingSys.DTO.UpdateUserRequest;
import com.ugc.EmpMngmntAndTktingSys.DTO.UserResponse;
import com.ugc.EmpMngmntAndTktingSys.mapper.UserMapper;
import com.ugc.EmpMngmntAndTktingSys.model.User;
import com.ugc.EmpMngmntAndTktingSys.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/empNtkt")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request){
        UserResponse userResponse = userMapper.mapToUserResponse(userService.createUser(request));
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/employee")
    public String Hello(){
        return "Hello";
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/employees")
    public ResponseEntity<List<UserResponse>> getEmployees(){
        return ResponseEntity.ok(userService.getEmployees());
    }

    @GetMapping("/by-username/{userName}")
    public ResponseEntity<UserResponse> getUserByUserName(
            @PathVariable String userName) {

        return ResponseEntity.ok(userService.getUserByUserName(userName));
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<UserResponse> updateUserById(@PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest){
        return ResponseEntity.ok(userService.updateUserById(id,updateUserRequest));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.deleteUserById(id));
    }
}