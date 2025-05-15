package id.co.bcaf.adapinjam.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import id.co.bcaf.adapinjam.models.FcmToken;
import id.co.bcaf.adapinjam.models.User;
import id.co.bcaf.adapinjam.repositories.FcmTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class NotificationService {

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    public String sendNotification(String token, String title, String body) {
        try {
            // Membuat pesan yang akan dikirim ke perangkat Android
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            // Mengirim pesan menggunakan FCM
            String response = FirebaseMessaging.getInstance().send(message);
            return response; // Response berisi ID pesan yang berhasil dikirim

        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return "Error sending notification";
        }
    }

    public void sendNotificationToUser(User user, String title, String body) {
        Optional<FcmToken> tokenOpt = fcmTokenRepository.findByUser_Email(user.getEmail());
        tokenOpt.ifPresent(token -> {
            try {
                Message message = Message.builder()
                        .setToken(token.getToken())
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .build();
                FirebaseMessaging.getInstance().send(message);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        });
    }

}
