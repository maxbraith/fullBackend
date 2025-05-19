package com.max_griffin_project.service;

import com.max_griffin_project.dto.RoleDto;
import com.max_griffin_project.models.Role;
import com.max_griffin_project.models.UserEntity;
import com.max_griffin_project.repository.RoleRepository;
import com.max_griffin_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    final private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<RoleDto> getUserRoles(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return user.getRoles()
                .stream()
                .map(Role::toRoleDto)
                .toList();
    }
}
