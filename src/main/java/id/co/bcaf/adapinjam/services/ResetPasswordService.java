package id.co.bcaf.adapinjam.services;

import id.co.bcaf.adapinjam.models.Notification;
import id.co.bcaf.adapinjam.models.PasswordResetRequest;
import id.co.bcaf.adapinjam.models.User;
import id.co.bcaf.adapinjam.models.UserEmployee;
import id.co.bcaf.adapinjam.repositories.NotificationRepository;
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
    private final NotificationRepository notificationRepository;

    // Employee mengajukan permintaan reset password
    public String requestResetPassword(String nip) {
        Optional<UserEmployee> userEmployeeOpt = userEmployeeRepository.findByNip(nip);
        if (userEmployeeOpt.isEmpty()) {
            return "Employee not found";
        }

        // Create password reset token
        String token = UUID.randomUUID().toString();
        PasswordResetRequest resetToken = new PasswordResetRequest(token, userEmployeeOpt.get());
        passwordResetRequestRepository.save(resetToken);

        notificationRepository.save(Notification.builder()
                .title("Reset Password Request")
                .message("Karyawan dengan NIP " + nip + " mengajukan reset password.")
                .build());

        // Send email to employee with reset link containing the token
        sendResetEmail(userEmployeeOpt.get().getUser().getEmail(), token);

        return "Password reset request submitted to Super Admin";
    }

    // Admin melihat request reset yang belum diproses
    public List<PasswordResetRequest> getPendingResetRequests() {
        return passwordResetRequestRepository.findByProcessedFalse();
    }

    // Admin memproses dan mengirimkan token reset password ke email employee
    public String processPasswordReset(UUID requestId) {
        Optional<PasswordResetRequest> requestOpt = passwordResetRequestRepository.findById(requestId);
        if (requestOpt.isEmpty()) {
            return "Reset request not found";
        }

        PasswordResetRequest request = requestOpt.get();
        UserEmployee employee = request.getUserEmployee();
        // Token sudah di-generate pada saat pengajuan reset password

        request.setProcessed(true);
        passwordResetRequestRepository.save(request);
        return "Password reset token sent to employee email";
    }

    // Mengirim email dengan link reset password
    private void sendResetEmail(String toEmail, String token) {
        String resetUrl = "https://adapinjam.vercel.app/reset-password/" + token;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Reset Password Request");
            helper.setText("Klik link ini untuk mereset password Anda: " + resetUrl, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    // Reset password dengan token
    public String resetPasswordWithToken(String token, String newPassword) {
        Optional<PasswordResetRequest> tokenOpt = passwordResetRequestRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return "Invalid or expired token";
        }

        PasswordResetRequest resetToken = tokenOpt.get();
        UserEmployee employee = resetToken.getUserEmployee();
        User user = employee.getUser();

        // Set new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Token dianggap sudah dipakai
        passwordResetRequestRepository.delete(resetToken);
        return "Password has been successfully reset";
    }
}
