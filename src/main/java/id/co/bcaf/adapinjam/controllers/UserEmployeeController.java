package id.co.bcaf.adapinjam.controllers;

import id.co.bcaf.adapinjam.models.UserEmployee;
import id.co.bcaf.adapinjam.services.UserEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user-employees")
public class UserEmployeeController {

    @Autowired
    private UserEmployeeService userEmployeeService;

    @GetMapping
    public List<UserEmployee> getAll() {
        return userEmployeeService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEmployee> getById(@PathVariable UUID id) {
        Optional<UserEmployee> data = userEmployeeService.getById(id);
        return data.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserEmployee> create(@RequestBody UserEmployee userEmployee) {
        return ResponseEntity.ok(userEmployeeService.create(userEmployee));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserEmployee> update(@PathVariable UUID id, @RequestBody UserEmployee updatedData) {
        return userEmployeeService.update(id, updatedData)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return userEmployeeService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
