package id.co.bcaf.adapinjam.services;

import id.co.bcaf.adapinjam.models.User;
import id.co.bcaf.adapinjam.models.UserEmployee;
import id.co.bcaf.adapinjam.repositories.UserEmployeeRepository;
import id.co.bcaf.adapinjam.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<UserEmployee> getAll() {
        return userEmployeeRepository.findAll();
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
