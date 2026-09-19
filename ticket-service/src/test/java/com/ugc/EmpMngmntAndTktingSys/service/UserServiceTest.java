//package com.ugc.EmpMngmntAndTktingSys.service;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Optional;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class UserServiceTest {
//
//    @Mock
//    private UserRepo userRepo;
//
//    @Mock
//    private RolesRepo rolesRepo;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private UserService userService;
//
//    @Test
//    void shouldCreateEmployeeSuccessfully(){
//        //Arrange
//        RegisterRequest request = new RegisterRequest(
//                "abhisk",
//                "sk@gmail.com",
//                "12345",
//                "IT"
//        );
//
//        Role role = new Role(1L, RoleType.ROLE_EMP);
//
//        when(userRepo.findByUserName(request.getUserName()))
//                .thenReturn(Optional.empty());
//
//        when(passwordEncoder.encode(request.getPassword()))
//                .thenReturn("encodedPassword");
//
//        when(rolesRepo.findByRoleName(RoleType.ROLE_EMP))
//                .thenReturn(Optional.of(role));
//
//        when(userRepo.save(any(User.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        //Act
//        User savedUser = userService.createUser(request);
//
//        //Assert
//        assertEquals(request.getUserName(),savedUser.getUserName());
//        assertEquals(request.getEmail(),savedUser.getEmail());
//        assertEquals("encodedPassword",savedUser.getPassword());
//        assertTrue(savedUser.getRoles().contains(role));
//
//        //Verify
//        verify(userRepo).save(any(User.class));
//
//        verify(passwordEncoder).encode(request.getPassword());
//
//        verify(rolesRepo).findByRoleName(RoleType.ROLE_EMP);
//
//    }
//
//    @Test
//    void shouldCreateManagerSuccessfully(){
//        // Arrange
//        CreateManagerRequest request = new CreateManagerRequest(
//                "manager",
//                "12345",
//                "manager@gmail.com",
//                "IT"
//        );
//
//        Role role = new Role(1L, RoleType.ROLE_MANAGER);
//
//        when(userRepo.findByUserName(request.getUsername()))
//                .thenReturn(Optional.empty());
//        when(passwordEncoder.encode(request.getPassword()))
//                .thenReturn("encodedPassword");
//        when((rolesRepo.findByRoleName(RoleType.ROLE_MANAGER)))
//                .thenReturn(Optional.of(role));
//        when(userRepo.save(any(User.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        //Act
//        User saved = userService.createManager(request);
//
//        //Assert
//        assertEquals(request.getUsername(),saved.getUserName());
//        assertEquals("encodedPassword",saved.getPassword());
//        assertEquals(request.getEmail(),saved.getEmail());
//        assertTrue(saved.getRoles().contains(role));
//
//        //Verify
//        verify(userRepo).findByUserName(request.getUsername());
//        verify(passwordEncoder).encode(request.getPassword());
//        verify(rolesRepo).findByRoleName(RoleType.ROLE_MANAGER);
//        verify(userRepo).save(any(User.class));
//    }
//
//    @Test
//    void shouldThrowUsernameAlreadyExistExeption(){
//        //Arrange
//        RegisterRequest request = new RegisterRequest(
//                "abhisk",
//                "sk@gmail.com",
//                "12345",
//                "IT"
//        );
//        User existingUser = new User(1L,"abhisk","abhi@gmail.com","3434","IT", Set.of(new Role(1L,RoleType.ROLE_EMP)));
//
//        when(userRepo.findByUserName(request.getUserName()))
//                .thenReturn(Optional.of(existingUser));
//
//        //Act+Assert
//        UsernameAlreadyExistsException ex = assertThrows(UsernameAlreadyExistsException.class,
//                                                            ()-> userService.createUser(request));
//        assertEquals("Username already exists!",ex.getMessage());
//
//        //Verify
//        verify(userRepo).findByUserName(request.getUserName());
//        verify(userRepo,never()).save(any(User.class));
//        verify(passwordEncoder,never()).encode(request.getPassword());
//        verify(rolesRepo,never()).findByRoleName(RoleType.ROLE_EMP);
//    }
//
//}