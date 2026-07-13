package com.tplite.core_banking.module.auth.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tplite.core_banking.module.auth.entity.RolePermission;
import com.tplite.core_banking.module.auth.entity.UserRole;
import com.tplite.core_banking.module.auth.repository.RolePermissionRepository;
import com.tplite.core_banking.module.auth.repository.UserRoleRepository;
import com.tplite.core_banking.module.user.entity.User;
import com.tplite.core_banking.module.user.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepo;
    private final UserRoleRepository userRoleRepo;
    private final RolePermissionRepository rolePermissionRepo;

    public CustomUserDetailsService(
        UserRepository userRepo,
        UserRoleRepository userRoleRepo,
        RolePermissionRepository rolePermissionRepo
    ) {
        this.userRepo = userRepo;
        this.userRoleRepo = userRoleRepo;
        this.rolePermissionRepo = rolePermissionRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<SimpleGrantedAuthority> authorities = loadAuthorities(user);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .accountLocked(user.getStatus().name().equals("LOCKED") || user.getStatus().name().equals("BLOCKED"))
                .disabled(user.getStatus().name().equals("DISABLED") || user.getStatus().name().equals("INACTIVE"))
                .build();
    }

    private Set<SimpleGrantedAuthority> loadAuthorities(User user) {
        List<UserRole> userRoles = userRoleRepo.findByUserId(user.getId());
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        List<UUID> roleIds = userRoles.stream()
                .map(userRole -> {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getName()));
                    return userRole.getRole().getId();
                })
                .toList();

        if (!roleIds.isEmpty()) {
            List<RolePermission> rolePermissions = rolePermissionRepo.findByRoleIdIn(roleIds);
            rolePermissions.forEach(rolePermission ->
                    authorities.add(new SimpleGrantedAuthority(rolePermission.getPermission().getName()))
            );
        }

        return authorities;
    }
}
