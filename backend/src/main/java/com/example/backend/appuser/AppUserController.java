package com.example.backend.appuser;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/app-users")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @PostMapping("/login")
    public AppUser login() {
       return this.me();
    }

    @GetMapping("/me")
    public AppUser me() {
        return this.appUserService.findByUsernameWithoutPassword(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
    }

    @PostMapping
    public AppUser create(@RequestBody AppUser appUser) {
        return this.appUserService.create(appUser);
    }

    @GetMapping("/staff")
    public List<AppUser> getAllStaffMembersWithoutPassword() {
        AppUser currentUser = this.me();
        return this.appUserService.findBasicRoleUserByInstitutionAndRoleWithoutPassword(currentUser.getInstitution());
    }

    @PostMapping("/staff")
    public AppUser createStaffMember(@RequestBody AppUser newStaffUser) {
        AppUser currentManagerUser = this.me();
        return this.appUserService.createNewStaffMember(newStaffUser, currentManagerUser.getInstitution());
    }

    @DeleteMapping("/{id}")
    public void deleteStaffMemberById(@PathVariable String id) {
        this.appUserService.deleteStaffMemberById(id);
    }

    @GetMapping("/logout")
    public void logout (HttpSession httpSession) {
        httpSession.invalidate();
    }
}
