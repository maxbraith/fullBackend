package com.max_griffin_project.controllers;

import com.max_griffin_project.AppConstants;
import com.max_griffin_project.auth.CustomUserDetailService;
import com.max_griffin_project.auth.JWTGenerator;
import com.max_griffin_project.dto.AuthResponseDto;
import com.max_griffin_project.dto.LoginDto;
import com.max_griffin_project.dto.RegisterDto;
import com.max_griffin_project.dto.RefreshTokenRequestDto;
import com.max_griffin_project.models.Role;
import com.max_griffin_project.models.UserEntity;
import com.max_griffin_project.repository.RoleRepository;
import com.max_griffin_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTGenerator jwtGenerator;
    private final CustomUserDetailService customUserDetailService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          JWTGenerator jwtGenerator,
                          CustomUserDetailService customUserDetailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
        this.customUserDetailService = customUserDetailService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtGenerator.generateToken(authentication);
        String refreshToken = jwtGenerator.generateRefreshToken(authentication);
        String expiresIn = String.valueOf(AppConstants.JWT_EXPIRES_IN / 1000); // in seconds

        return new ResponseEntity<>(new AuthResponseDto(accessToken, refreshToken, expiresIn), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.username())) {
            return new ResponseEntity<>("Username is taken.", HttpStatus.BAD_REQUEST);
        }

        UserEntity user = new UserEntity();
        user.setUsername(registerDto.username());
        user.setPassword(passwordEncoder.encode(registerDto.password()));

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("User Role not set."));
        user.setRoles(Collections.singletonList(role));

        userRepository.save(user);

        return new ResponseEntity<>("User register success", HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto refreshRequest) {
        String refreshToken = refreshRequest.refreshToken();
        try {
            // Validate the provided refresh token
            if (!jwtGenerator.validateToken(refreshToken)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Extract the username from the refresh token
        String username = jwtGenerator.getUsernameFromJWT(refreshToken);

        // Load the user details (this ensures that the token is issued with the proper roles)
        UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

        // Create an Authentication object based on the user details.
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Generate new tokens
        String newAccessToken = jwtGenerator.generateToken(authentication);
        String newRefreshToken = jwtGenerator.generateRefreshToken(authentication);
        String expiresIn = String.valueOf(AppConstants.JWT_EXPIRES_IN / 1000); // in seconds

        return ResponseEntity.ok(new AuthResponseDto(newAccessToken, newRefreshToken, expiresIn));
    }

}
