package id.co.bcaf.adapinjam.services;

import id.co.bcaf.adapinjam.models.Branch;
import id.co.bcaf.adapinjam.models.Role;
import id.co.bcaf.adapinjam.models.User;
import id.co.bcaf.adapinjam.models.UserEmployee;
import id.co.bcaf.adapinjam.repositories.BranchRepository;
import id.co.bcaf.adapinjam.repositories.RoleRepository;
import id.co.bcaf.adapinjam.repositories.UserEmployeeRepository;
import id.co.bcaf.adapinjam.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserEmployeeService {

    @Autowired
    private UserEmployeeRepository userEmployeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BranchRepository branchRepository;

    public List<UserEmployee> getAll() {
        return userEmployeeRepository.findAll();
    }

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserEmployee addUserEmployee(UserEmployee userEmployee) {
        // Get Role by ID (assuming role is passed in the request body as roleId)
        Integer roleId = userEmployee.getUser().getRole().getId();
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Ambil Branch berdasarkan ID
        UUID branchId = userEmployee.getBranch().getId();
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        // Set the Role to the User
        userEmployee.getUser().setRole(role);

        userEmployee.setBranch(branch);

        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(userEmployee.getUser().getPassword());
        userEmployee.getUser().setPassword(hashedPassword);

        // Save the User entity first
        User savedUser = userRepository.save(userEmployee.getUser());

        // Set the saved user in UserEmployee
        userEmployee.setUser(savedUser);

        // Save the UserEmployee entity
        return userEmployeeRepository.save(userEmployee);
    }

    public Optional<UserEmployee> getById(UUID id) {
        return userEmployeeRepository.findById(id);
    }

    public UserEmployee create(UserEmployee userEmployee) {
        User user = userRepository.findById(userEmployee.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        userEmployee.setUser(user);
        return userEmployeeRepository.save(userEmployee);
    }

    public Optional<UserEmployee> update(UUID id, UserEmployee updatedData) {
        return userEmployeeRepository.findById(id).map(data -> {
            data.setUser(updatedData.getUser());
            data.setNip(updatedData.getNip());
            data.setBranch(updatedData.getBranch());
            data.setStatusEmployee(updatedData.getStatusEmployee());
            return userEmployeeRepository.save(data);
        });
    }

    public boolean delete(UUID id) {
        return userEmployeeRepository.findById(id).map(data -> {
            userEmployeeRepository.delete(data);
            return true;
        }).orElse(false);
    }
}
