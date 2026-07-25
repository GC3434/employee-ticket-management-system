package com.ugc.EmpMngmntAndTktingSys.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ugc.EmpMngmntAndTktingSys.DTO.LoginRequest;
import com.ugc.EmpMngmntAndTktingSys.Security.CustomerUserDetailService;
import com.ugc.EmpMngmntAndTktingSys.Security.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @MockitoBean
    private CustomerUserDetailService customerUserDetailService;
    @MockitoBean
    AuthenticationManager authenticationManager;
    @MockitoBean
    JwtService jwtService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldLoginSuccessfully() throws Exception {
        //Arrange
        LoginRequest loginRequest = new LoginRequest("abhi", "12345");
        String token = "123";
        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.getUserName(),loginRequest.getPassword());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtService.generateToken(loginRequest.getUserName())).thenReturn(token);

        //Act + Assert
        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));

        //Verify
        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(jwtService).generateToken(loginRequest.getUserName());

    }


}
