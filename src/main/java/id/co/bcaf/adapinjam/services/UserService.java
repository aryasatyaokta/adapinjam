package id.co.bcaf.adapinjam.services;

import id.co.bcaf.adapinjam.models.Role;
import id.co.bcaf.adapinjam.models.User;
import id.co.bcaf.adapinjam.repositories.RoleRepository;
import id.co.bcaf.adapinjam.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        Integer roleId = user.getRole().getId();
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);
        return userRepository.save(user);
    }

    public Optional<User> updateUser(UUID id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            Integer roleId = userDetails.getRole().getId();
            Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole(role);
            user.setEmail(userDetails.getEmail());
            user.setName(userDetails.getName());
            user.setPassword(userDetails.getPassword());
            return userRepository.save(user);
        });
    }

    public boolean deleteUser(UUID id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return true;
        }).orElse(false);
    }
}