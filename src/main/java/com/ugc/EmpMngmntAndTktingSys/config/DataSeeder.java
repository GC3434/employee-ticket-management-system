package com.ugc.EmpMngmntAndTktingSys.config;

import com.ugc.EmpMngmntAndTktingSys.model.Role;
import com.ugc.EmpMngmntAndTktingSys.model.RoleType;
import com.ugc.EmpMngmntAndTktingSys.model.User;
import com.ugc.EmpMngmntAndTktingSys.repo.RolesRepo;
import com.ugc.EmpMngmntAndTktingSys.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RolesRepo rolesRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // Seed Roles
        seedRole(RoleType.ROLE_ADMIN);
        seedRole(RoleType.ROLE_MANAGER);
        seedRole(RoleType.ROLE_EMP);

        // Seed Default Admin
        if (userRepo.findByUserName("admin").isEmpty()) {

            User admin = new User();
            admin.setUserName("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@company.com");
            admin.setDepartment("ADMIN");

            Role adminRole = rolesRepo.findByRoleName(RoleType.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            admin.setRoles(Set.of(adminRole));

            userRepo.save(admin);

            System.out.println("Default admin created.");
        } else {
            System.out.println("Admin already exists.");
        }
    }

    private void seedRole(RoleType roleType) {

        if (rolesRepo.findByRoleName(roleType).isEmpty()) {

            Role role = new Role();
            role.setRoleName(roleType);

            rolesRepo.save(role);

            System.out.println(roleType + " created.");
        }
    }
}