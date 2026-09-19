package com.ugc.EmpMngmntAndTktingSys.controller;

import com.ugc.EmpMngmntAndTktingSys.DTO.CreateManagerRequest;
import com.ugc.EmpMngmntAndTktingSys.DTO.UserResponse;
import com.ugc.EmpMngmntAndTktingSys.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @PostMapping("/create-manager")
    public ResponseEntity<UserResponse> createManager(
            @Valid @RequestBody CreateManagerRequest managerRequest) {

        return ResponseEntity.ok(
                userService.createManager(managerRequest)
        );
    }
}