package com.example.oauth2.token;

import com.example.oauth2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    public void saveUserToken(User user, String jwtToken) {
        Token token = new Token();
        token.setUser(user);
        token.setToken(jwtToken);
        token.setTokenType(TokenType.BEARER);
        token.setExpired(false);
        token.setRevoked(false);
        token.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(token);
    }

    public boolean isTokenValid(String jwtToken) {
        Optional<Token> optionalToken = tokenRepository.findByToken(jwtToken);

        if (!optionalToken.isPresent()) {
            return false;
        }

        Token token = optionalToken.get();

        if (token.isExpired() || token.isRevoked()) {
            return false;
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        return true;
    }

    public boolean validateToken(String token) {

        return true;
    }
}
