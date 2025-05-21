package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.dtos.CreateRoleRequest;
import id.co.bcaf.adapinjam.models.Feature;
import id.co.bcaf.adapinjam.models.Role;
import id.co.bcaf.adapinjam.models.RoleToFeature;
import id.co.bcaf.adapinjam.services.RoleService;
import id.co.bcaf.adapinjam.services.RoleToFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleToFeatureService roleToFeatureService;

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'GET_ALL_ROLES')")
    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Integer id) {
        Optional<Role> role = roleService.getRoleById(id);
        return role.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'GET_ROLES_FEATURES')")
    @GetMapping("/{roleId}/features")
    public ResponseEntity<List<Feature>> getFeaturesByRole(@PathVariable Integer roleId) {
        List<Feature> features = roleToFeatureService.getFeaturesByRole(roleId);
        return ResponseEntity.ok(features); // Kembalikan meskipun kosong
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'CREATE_ROLES_FEATURES')")
    @PostMapping("/add")
    public ResponseEntity<String> addRole(@RequestBody CreateRoleRequest createRoleRequest) {
        roleToFeatureService.addRoleWithFeatures(createRoleRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Role and features added successfully.");
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'UPDATE_ROLES_FEATURES')")
    @PutMapping("/edit/{roleId}")
    public ResponseEntity<String> updateRole(@PathVariable Long roleId, @RequestBody CreateRoleRequest createRoleRequest) {
        roleToFeatureService.updateRoleWithFeatures(Math.toIntExact(roleId), createRoleRequest);
        return ResponseEntity.ok("Role and features updated successfully.");
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'DELETE_ROLES_FEATURES')")
    @DeleteMapping("/delete/{roleId}")
    public ResponseEntity<String> deleteRole(@PathVariable Integer roleId) {
        roleToFeatureService.deleteRoleWithFeatures(roleId);
        return ResponseEntity.ok("Role and associated features deleted successfully.");
    }


}
