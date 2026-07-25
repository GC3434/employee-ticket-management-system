package com.ugc.EmpMngmntAndTktingSys.Security;

import com.ugc.EmpMngmntAndTktingSys.model.User;
import com.ugc.EmpMngmntAndTktingSys.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByUserName(username).orElseThrow(()->
                        new UsernameNotFoundException("User Not Found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                user.getRoles()
                                .stream()
                                .map(role->
                                        new SimpleGrantedAuthority(
                                                role.getRoleName().name()))
                                .toList()
        );
    }
}
