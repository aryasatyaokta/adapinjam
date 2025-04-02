package id.co.bcaf.adapinjam.services;

import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {

    // In-memory storage for blacklisted tokens
    private final Set<String> blacklistedTokens = new HashSet<>();

    // Add token to the blacklist
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    // Check if token is blacklisted
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    // Optionally, clear the blacklist (if needed)
    public void clearBlacklist() {
        blacklistedTokens.clear();
    }
}
