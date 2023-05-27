package com.example.backend.appuser;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String BASIC_ROLE = "STAFF";

    public AppUser findByUsernameWithoutPassword(String username) {
        AppUser appUser = this.findByUsername(username);
        appUser.setPassword("");
        return appUser;
    }

    public AppUser findByUsername(String username) {

        return this.appUserRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public AppUser create(AppUser appUser) {

        if (this.appUserRepository.findByUsername(appUser.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));

        if (
                SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null
                        ||
                !SecurityContextHolder.getContext()
                        .getAuthentication()
                        .isAuthenticated()
                        ||
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getAuthorities()
                        .stream()
                        .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(ADMIN_ROLE))
        ) {
            appUser.setRole(BASIC_ROLE);
        } else {
            appUser.setRole(ADMIN_ROLE);
        }

        this.appUserRepository.save(appUser);

        appUser.setPassword("");

        return appUser;
    }

    public List<AppUser> findBasicRoleUserByInstitutionAndRoleWithoutPassword(String institution) {
        return this.appUserRepository.findAllByInstitutionAndRole(institution, BASIC_ROLE);
    }

    public AppUser createNewStaffMember(AppUser newStaffUser, String institution) {
        newStaffUser.setInstitution(institution);
        newStaffUser.setRole(BASIC_ROLE);
        newStaffUser.setPassword("");
        return this.appUserRepository.save(newStaffUser);
    }

    public void deleteStaffMemberById(AppUser currentManagerUser, String id) {
        AppUser staffMemberToDelete = this.appUserRepository.findById(id).orElseThrow();
        if (currentManagerUser.getRole().equals(BASIC_ROLE) &&
                        staffMemberToDelete.getInstitution().equals(currentManagerUser.getInstitution()))
        {
            this.appUserRepository.deleteById(id);
        }
    }
}
