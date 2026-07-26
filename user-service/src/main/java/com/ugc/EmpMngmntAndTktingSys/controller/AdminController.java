package com.ugc.EmpMngmntAndTktingSys.controller;

import com.ugc.EmpMngmntAndTktingSys.DTO.CreateManagerRequest;
import com.ugc.EmpMngmntAndTktingSys.DTO.LoginRequest;
import com.ugc.EmpMngmntAndTktingSys.DTO.UserResponse;
import com.ugc.EmpMngmntAndTktingSys.mapper.UserMapper;
import com.ugc.EmpMngmntAndTktingSys.model.User;
import com.ugc.EmpMngmntAndTktingSys.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private static UserService userService;
    private static UserMapper userMapper;

    @PostMapping("/create-manager")
    public ResponseEntity<UserResponse> createManager(@Valid @RequestBody CreateManagerRequest managerRequest){
        return ResponseEntity.ok(userService.createManager(managerRequest));
    }

}
