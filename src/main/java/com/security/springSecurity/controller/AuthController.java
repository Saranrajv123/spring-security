package com.security.springSecurity.controller;

import com.security.springSecurity.exceptions.ApiException;
import com.security.springSecurity.models.ERole;
import com.security.springSecurity.models.Role;
import com.security.springSecurity.models.User;
import com.security.springSecurity.payload.SignUpRequest;
import com.security.springSecurity.payload.responses.MessageResponse;
import com.security.springSecurity.repository.RoleRepository;
import com.security.springSecurity.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth/")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {

        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            throw new ApiException("User name not found", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new ApiException("Email not found", HttpStatus.BAD_REQUEST);
        }

        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword())
        );

        Set<String> roles = signUpRequest.getRole();

        Set<Role> newRole = new HashSet<>();

        if (roles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            newRole.add(userRole);
        } else {
            roles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Admin Role is not found."));
                        newRole.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Mod Role is not found."));
                        newRole.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Default Role is not found."));
                        newRole.add(userRole);
                }
            });
        }


//        if (userRepository.count() == 0) {
//            newRole.add(roleRepository.findByName(ERole.ROLE_USER)
//                    .orElseThrow(() -> new RuntimeException("USER_ROLE_NOT_SET")));
//            newRole.add(roleRepository.findByName(ERole.ROLE_ADMIN)
//                    .orElseThrow(() -> new RuntimeException("ADMIN_ROLE_NOT_SET")));
//        } else {
//            newRole.add(roleRepository.findByName(ERole.ROLE_USER)
//                    .orElseThrow(() -> new RuntimeException("USER_ADMIN_ROLE_NOT_SET")));
//        }

        user.setRoles(newRole);

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Register successfully"));

    }
}
