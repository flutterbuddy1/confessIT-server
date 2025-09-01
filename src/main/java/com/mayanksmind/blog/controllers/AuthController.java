package com.mayanksmind.blog.controllers;

import com.mayanksmind.blog.dto.UserDTO;
import com.mayanksmind.blog.enums.Role;
import com.mayanksmind.blog.models.*;
import com.mayanksmind.blog.repo.UserRepo;
import com.mayanksmind.blog.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try
        {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Role.USER);
            userRepository.save(user);
            return ResponseEntity.ok(new ApiResponse(true,"User registered successfully"));
        }catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(false,"Email Already Exist / Invalid Email address"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
        String token = jwtUtil.generateToken(authRequest.getEmail());
        UserDTO user = new UserDTO(userRepository.findByEmail(authRequest.getEmail()).orElseThrow());
        return ResponseEntity.ok(new AuthResponse(token,user));
    }

    @GetMapping("/user")
    public UserDTO currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> user = userRepository.findByEmail(email);
        return new UserDTO(user.orElseThrow());
    }
}
