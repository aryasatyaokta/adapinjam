package id.co.bcaf.adapinjam.services;

import id.co.bcaf.adapinjam.models.User;
import id.co.bcaf.adapinjam.repositories.UserRepository;
import id.co.bcaf.adapinjam.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


    @Autowired
    private UserRepository usersRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public String authenticateUser(String email, String password) {
        logger.info("Mencoba login dengan email: {}", email);  // Cek apakah email diterima

        Optional<User> userOptional = usersRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            logger.info("User ditemukan: {}", user.getEmail()); // Konfirmasi user ditemukan di database
            // Langsung bandingkan dengan password di database (karena belum di-hash)
            if (user.getPassword().equals(password)) {
                logger.info("Password cocok, menghasilkan token...");
                return jwtUtil.generateToken(email); // Kembalikan JWT Token
            }
            else {
                logger.warn("Password tidak cocok untuk email: {}", email);
            }
        }
        else {
            logger.warn("User dengan email {} tidak ditemukan di database", email);
        }
        return null; // Jika gagal login
    }
}
