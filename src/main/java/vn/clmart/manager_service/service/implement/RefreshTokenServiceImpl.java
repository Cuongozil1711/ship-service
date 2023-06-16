package vn.clmart.manager_service.service.implement;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.config.exceptions.TokenRefreshException;
import vn.clmart.manager_service.model.RefreshToken;
import vn.clmart.manager_service.repository.RefreshTokenRepo;
import vn.clmart.manager_service.repository.UserRepo;
import vn.clmart.manager_service.service.RefreshTokenService;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private static final Logger logger = Logger.getLogger(RefreshTokenServiceImpl.class);

    private final UserRepo userRepo;
    private final RefreshTokenRepo refreshTokenRepo;
    @Value("${app.security.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    public RefreshTokenServiceImpl(UserRepo userRepo, RefreshTokenRepo refreshTokenRepo) {
        this.userRepo = userRepo;
        this.refreshTokenRepo = refreshTokenRepo;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepo.findByToken(token);
    }

    @Override
    public RefreshToken createRefreshToken(String userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUserId(userId);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepo.save(refreshToken);
        return refreshToken;
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepo.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Override
    public int deleteByUserId(String userId) {
        return 0;
    }
}
