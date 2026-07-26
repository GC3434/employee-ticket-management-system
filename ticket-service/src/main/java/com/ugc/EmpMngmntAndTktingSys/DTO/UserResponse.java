package com.ugc.EmpMngmntAndTktingSys.DTO;

import com.ugc.EmpMngmntAndTktingSys.model.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long userId;
    private String userName;
    private String email;
    private String department;
    private Set<String> roles;
}