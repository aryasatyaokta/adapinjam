package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.models.UserEmployee;
import id.co.bcaf.adapinjam.services.UserEmployeeService;
import id.co.bcaf.adapinjam.models.User;
import id.co.bcaf.adapinjam.models.Role;
import id.co.bcaf.adapinjam.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user-employee")
public class UserEmployeeController {

    @Autowired
    private UserEmployeeService userEmployeeService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Endpoint untuk menambahkan UserEmployee
    @PostMapping("/add")
    public ResponseEntity<?> addUserEmployee(@RequestBody UserEmployee userEmployee) {
        try {
            // Panggil service untuk menambahkan UserEmployee
            UserEmployee createdUserEmployee = userEmployeeService.addUserEmployee(userEmployee);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUserEmployee);
        } catch (Exception e) {
            // Tangani error jika ada
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating UserEmployee: " + e.getMessage());
        }
    }

    @PreAuthorize("@accessPermission.hasAccess(authentication, 'GET_ALLEMPLOYEE')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllUserEmployees() {
        List<UserEmployee> employees = userEmployeeService.getAll();
        if (employees.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No UserEmployee found");
        }
        return ResponseEntity.ok(employees);
    }

    // Endpoint untuk mendapatkan UserEmployee berdasarkan ID (UUID)
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUserEmployeeById(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);  // Coba konversi string ke UUID
            UserEmployee userEmployee = userEmployeeService.getById(uuid)
                    .orElseThrow(() -> new RuntimeException("UserEmployee not found"));
            return ResponseEntity.ok(userEmployee);
        } catch (IllegalArgumentException e) {
            // Handle jika ID tidak valid (bukan UUID)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid UUID format");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UserEmployee not found");
        }
    }

    // Endpoint untuk memperbarui UserEmployee
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUserEmployee(@PathVariable String id, @RequestBody UserEmployee updatedData) {
        try {
            UUID uuid = UUID.fromString(id);  // Coba konversi string ke UUID
            updatedData.setId(uuid);
            UserEmployee updatedUserEmployee = userEmployeeService.update(uuid, updatedData)
                    .orElseThrow(() -> new RuntimeException("UserEmployee not found"));
            return ResponseEntity.ok(updatedUserEmployee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid UUID format");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error updating UserEmployee: " + e.getMessage());
        }
    }

    // Endpoint untuk menghapus UserEmployee
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUserEmployee(@PathVariable String id) {
        try {
            UUID uuid = UUID.fromString(id);  // Coba konversi string ke UUID
            boolean deleted = userEmployeeService.delete(uuid);
            if (deleted) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("UserEmployee deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UserEmployee not found");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid UUID format");
        }
    }
}
