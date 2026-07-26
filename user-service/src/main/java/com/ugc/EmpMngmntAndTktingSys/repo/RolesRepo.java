package com.ugc.EmpMngmntAndTktingSys.repo;

import com.ugc.EmpMngmntAndTktingSys.model.Role;
import com.ugc.EmpMngmntAndTktingSys.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepo extends JpaRepository<Role,Long> {

    Optional<Role> findByRoleName(RoleType roleType);
}
