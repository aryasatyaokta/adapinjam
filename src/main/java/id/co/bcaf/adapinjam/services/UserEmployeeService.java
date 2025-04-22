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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;

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

    @Autowired
    private JavaMailSender mailSender;

    public List<UserEmployee> getAll() {
        return userEmployeeRepository.findAll();
    }

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Optional<UserEmployee> getByUserEmail(String email) {
        return userEmployeeRepository.findByUserEmail(email);
    }

    public UserEmployee addUserEmployee(UserEmployee userEmployee) {
        Integer roleId = userEmployee.getUser().getRole().getId();
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        UUID branchId = userEmployee.getBranch().getId();
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        // Set role dan branch
        userEmployee.getUser().setRole(role);
        userEmployee.setBranch(branch);

        // 1. Generate random password
        String randomPassword = RandomStringUtils.randomAlphanumeric(10);

        // 2. Encode password
        String hashedPassword = passwordEncoder.encode(randomPassword);
        userEmployee.getUser().setPassword(hashedPassword);

        // 3. Save user
        User savedUser = userRepository.save(userEmployee.getUser());
        userEmployee.setUser(savedUser);

        // 4. Save userEmployee
        UserEmployee savedUserEmployee = userEmployeeRepository.save(userEmployee);

        // 5. Kirim email ke user
        sendNewUserEmail(savedUser.getEmail(), savedUserEmployee.getNip(), randomPassword);

        return savedUserEmployee;
    }

    private void sendNewUserEmail(String toEmail, String nip, String rawPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Informasi Akun Anda");
        message.setText(
                "Halo,\n\nAkun Anda telah berhasil dibuat.\n\n" +
                        "Login NIP: " + nip +
                        "\nPassword: " + rawPassword +
                        "\n\nSilakan login dan segera ubah password Anda untuk keamanan akun.\n\n" +
                        "Terima kasih."
        );
        mailSender.send(message);
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

    public UserEmployee update(UUID id, UserEmployee updatedData) {
        // Cari UserEmployee berdasarkan ID
        UserEmployee existingUserEmployee = userEmployeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserEmployee not found"));

        // Perbarui data userEmployee
        existingUserEmployee.setNip(updatedData.getNip());
        existingUserEmployee.setStatusEmployee(updatedData.getStatusEmployee());

        // Update Role jika ada perubahan
        Integer roleId = updatedData.getUser().getRole().getId();
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        existingUserEmployee.getUser().setRole(role);

        // Update Branch jika ada perubahan
        UUID branchId = updatedData.getBranch().getId();
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        existingUserEmployee.setBranch(branch);

        // Update password hanya jika ada perubahan password
        if (updatedData.getUser().getPassword() != null && !updatedData.getUser().getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(updatedData.getUser().getPassword());
            existingUserEmployee.getUser().setPassword(hashedPassword);
        }

        // Simpan perubahan ke database
        userEmployeeRepository.save(existingUserEmployee);
        return existingUserEmployee;
    }

    public boolean delete(UUID id) {
        return userEmployeeRepository.findById(id).map(data -> {
            userEmployeeRepository.delete(data);
            return true;
        }).orElse(false);
    }
}
