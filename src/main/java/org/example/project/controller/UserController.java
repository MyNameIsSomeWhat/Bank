package org.example.project.controller;

import org.example.project.dto.UserResponseDto;
import org.example.project.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    // FR-10: Đổi PIN / Quên mật khẩu (cơ bản)
    @PostMapping("/{id}/change-pin")
    public ResponseEntity<String> changePin(@PathVariable Long id, @RequestBody String newPin) {
        userService.changePin(id, newPin);
        return ResponseEntity.ok("PIN changed successfully");
    }
}