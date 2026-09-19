package com.ugc.EmpMngmntAndTktingSys.service;

import com.ugc.EmpMngmntAndTktingSys.DTO.UserResponse;
import com.ugc.EmpMngmntAndTktingSys.exception.ServiceUnavailableException;
import com.ugc.EmpMngmntAndTktingSys.feign.UserClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserValidationService {

    private final UserClient userClient;

    public UserValidationService(UserClient userClient) {
        this.userClient = userClient;
    }

    @Retry(
            name = "userService",
            fallbackMethod = "getUserFallback"
    )
    @CircuitBreaker(
            name = "userService",
            fallbackMethod = "getUserFallback"
    )
    public UserResponse getUser(String username) {
        return userClient.getUserByUsername(username);
    }

    private UserResponse getUserFallback(
            String username,
            Exception ex) {

        log.error(
                "User Service unavailable while validating user {}",
                username,
                ex
        );

        throw new ServiceUnavailableException(
                "User Service is temporarily unavailable. Please try again later."
        );
    }

    @Retry(
            name = "userService",
            fallbackMethod = "getUserFallback"
    )
    @CircuitBreaker(
            name = "userService",
            fallbackMethod = "getUserByIdFallback"
    )
    public UserResponse getUserById(Long userId) {
        return userClient.getUserById(userId);
    }

    private UserResponse getUserByIdFallback(
            Long userId,
            Exception ex) {

        log.error(
                "User Service unavailable while validating user {}",
                userId,
                ex
        );

        throw new ServiceUnavailableException(
                "User Service is temporarily unavailable. Please try again later."
        );
    }
}