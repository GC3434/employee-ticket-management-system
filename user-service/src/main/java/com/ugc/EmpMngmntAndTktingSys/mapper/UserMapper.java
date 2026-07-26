package com.ugc.EmpMngmntAndTktingSys.mapper;

import com.ugc.EmpMngmntAndTktingSys.DTO.UserResponse;
import com.ugc.EmpMngmntAndTktingSys.model.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse mapToUserResponse(User user){
        Set<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toSet());
        return new UserResponse(user.getUserId(),
                                user.getUserName(),
                                user.getEmail(),
                                user.getDepartment(),
                                roles
                                );
    }

}
