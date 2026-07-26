package com.ugc.EmpMngmntAndTktingSys.client;

import com.ugc.EmpMngmntAndTktingSys.DTO.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserClient {

    @Autowired
    private RestClient restClient;

    @Autowired
    private HttpServletRequest request;

    private static final String USER_SERVICE_URL = "http://localhost:8081";

    public UserResponse getUserByUserName(String userName) {

        String authHeader = request.getHeader("Authorization");

        return restClient.get()
                .uri(USER_SERVICE_URL + "/empNtkt/by-username/{userName}", userName)                .header("Authorization", authHeader)
                .retrieve()
                .body(UserResponse.class);
    }

    public UserResponse getUserById(Long userId) {

        String authHeader = request.getHeader("Authorization");

        return restClient.get()
                .uri(USER_SERVICE_URL + "/empNtkt/user/{userId}", userId)
                .header("Authorization", authHeader)
                .retrieve()
                .body(UserResponse.class);
    }
}