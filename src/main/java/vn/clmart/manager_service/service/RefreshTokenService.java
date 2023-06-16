package vn.clmart.manager_service.service;

import vn.clmart.manager_service.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);
    RefreshToken createRefreshToken(String userId);
    RefreshToken verifyExpiration(RefreshToken token);
    int deleteByUserId(String userId);
}
