package com.ugc.EmpMngmntAndTktingSys.feign;

import com.ugc.EmpMngmntAndTktingSys.DTO.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {
    @GetMapping("/internal/users/{username}")
    UserResponse getUserByUsername(@PathVariable String username);

    @GetMapping("/internal/users/id/{id}")
    UserResponse getUserById(@PathVariable Long id);

}