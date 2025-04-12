package id.co.bcaf.adapinjam.services;

import id.co.bcaf.adapinjam.models.PasswordResetRequest;
import id.co.bcaf.adapinjam.models.User;
import id.co.bcaf.adapinjam.models.UserEmployee;
import id.co.bcaf.adapinjam.repositories.PasswordResetRequestRepository;
import id.co.bcaf.adapinjam.repositories.UserEmployeeRepository;
import id.co.bcaf.adapinjam.repositories.UserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {

    private final UserEmployeeRepository userEmployeeRepository;
    private final UserRepository userRepository;
    private final PasswordResetRequestRepository passwordResetRequestRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    public String requestResetPassword(String nip) {
        Optional<UserEmployee> userEmployeeOpt = userEmployeeRepository.findByNip(nip);
        if (userEmployeeOpt.isEmpty()) {
            return "Employee not found";
        }

        PasswordResetRequest request = new PasswordResetRequest();
        request.setUserEmployee(userEmployeeOpt.get());
        passwordResetRequestRepository.save(request);

        return "Password reset request submitted to Super Admin";
    }

    public List<PasswordResetRequest> getPendingResetRequests() {
        return passwordResetRequestRepository.findByProcessedFalse();
    }

    public String manualResetPassword(UUID requestId) {
        Optional<PasswordResetRequest> requestOpt = passwordResetRequestRepository.findById(requestId);
        if (requestOpt.isEmpty()) {
            return "Reset request not found";
        }

        PasswordResetRequest request = requestOpt.get();
        UserEmployee employee = request.getUserEmployee();
        User user = employee.getUser();

        // Generate password
        String newPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        try {
            sendEmail(user.getEmail(), newPassword);
            request.setProcessed(true);
            passwordResetRequestRepository.save(request);
            return "Password reset and sent successfully";
        } catch (Exception e) {
            return "Failed to send email: " + e.getMessage();
        }
    }

    private void sendEmail(String toEmail, String password) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("Password Reset by Admin");
        helper.setText("Password baru Anda: " + password, true);

        mailSender.send(message);
    }
}
