package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.models.Branch;
import id.co.bcaf.adapinjam.services.BranchService;
import id.co.bcaf.adapinjam.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;
    private final JwtUtil jwtUtil;

    private boolean isTokenValid(String authHeader) {
        return authHeader != null && authHeader.startsWith("Bearer ");
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'GET_ALL_BRANCH')")
    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader("Authorization") String authHeader) {
        if (!isTokenValid(authHeader)) return ResponseEntity.status(401).body("Unauthorized");

        String token = authHeader.substring(7);
        jwtUtil.extractEmail(token); // Validates token

        List<Branch> branches = branchService.getAllBranches();
        return ResponseEntity.ok(branches);
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'GET_BRANCH_BY_ID')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@RequestHeader("Authorization") String authHeader,
                                     @PathVariable UUID id) {
        if (!isTokenValid(authHeader)) return ResponseEntity.status(401).body("Unauthorized");

        String token = authHeader.substring(7);
        jwtUtil.extractEmail(token); // Validates token

        return branchService.getBranchById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'CREATE_BRANCH')")
    @PostMapping
    public ResponseEntity<?> create(@RequestHeader("Authorization") String authHeader,
                                    @RequestBody Branch branch) {
        if (!isTokenValid(authHeader)) return ResponseEntity.status(401).body("Unauthorized");

        String token = authHeader.substring(7);
        jwtUtil.extractEmail(token); // Validates token

        return ResponseEntity.ok(branchService.createBranch(branch));
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'UPDATE_BRANCH')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String authHeader,
                                    @PathVariable UUID id,
                                    @RequestBody Branch updatedBranch) {
        if (!isTokenValid(authHeader)) return ResponseEntity.status(401).body("Unauthorized");

        String token = authHeader.substring(7);
        jwtUtil.extractEmail(token); // Validates token

        try {
            Branch updated = branchService.updateBranch(id, updatedBranch);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

//    @PreAuthorize("@accessPermission.hasAccess(authentication, 'DELETE_BRANCH')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String authHeader,
                                    @PathVariable UUID id) {
        if (!isTokenValid(authHeader)) return ResponseEntity.status(401).body("Unauthorized");

        String token = authHeader.substring(7);
        jwtUtil.extractEmail(token); // Validates token

        branchService.deleteBranch(id);
        return ResponseEntity.ok("Branch deleted successfully");
    }
}
