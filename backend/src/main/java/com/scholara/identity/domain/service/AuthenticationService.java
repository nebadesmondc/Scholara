package com.scholara.identity.domain.service;

import com.scholara.identity.api.dto.UserResponse;
import com.scholara.identity.domain.exception.AccountDisabledException;
import com.scholara.identity.domain.exception.InvalidCredentialsException;
import com.scholara.identity.domain.exception.InvalidTokenException;
import com.scholara.identity.domain.model.RefreshToken;
import com.scholara.identity.domain.model.User;
import com.scholara.identity.domain.repository.UserRepository;
import com.scholara.shared.domain.Email;
import com.scholara.shared.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for authentication operations.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    /**
     * Authenticates a user with email and password.
     *
     * @param email the user's email
     * @param rawPassword the raw password
     * @return the authentication result with tokens
     * @throws InvalidCredentialsException if credentials are invalid
     * @throws AccountDisabledException if the account is disabled
     */
    public AuthenticationResult authenticate(Email email, String rawPassword) {
        User user = userRepository.findByEmail(email.value())
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isEnabled()) {
            throw new AccountDisabledException("Account is not verified");
        }

        if (user.getPassword() == null) {
            // OAuth user trying to use password login
            throw new InvalidCredentialsException("Please use your OAuth provider to login");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        userService.recordLogin(user);

        String accessToken = jwtTokenService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);
        UserResponse userResponse = UserResponse.from(user);


        return new AuthenticationResult(accessToken, refreshToken, userResponse);
    }

    /**
     * Authenticates a user for OAuth flow (no password required).
     *
     * @param user the OAuth user
     * @return the authentication result with tokens
     */
    public AuthenticationResult authenticateOAuth(User user) {
        userService.recordLogin(user);

        String accessToken = jwtTokenService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);
        UserResponse userResponse = UserResponse.from(user);

        return new AuthenticationResult(accessToken, refreshToken, userResponse);
    }

    /**
     * Refreshes an access token using a refresh token.
     *
     * @param rawRefreshToken the raw refresh token
     * @return the new authentication result
     * @throws InvalidTokenException if the refresh token is invalid
     */
    public AuthenticationResult refreshAccessToken(String rawRefreshToken) {
        RefreshToken token = refreshTokenService.validateAndGet(rawRefreshToken);

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new InvalidTokenException("User not found"));

        if (!user.isEnabled()) {
            throw new AccountDisabledException();
        }

        String newAccessToken = jwtTokenService.generateAccessToken(user);
        UserResponse userResponse = UserResponse.from(user);

        return new AuthenticationResult(newAccessToken, rawRefreshToken, userResponse);
    }

    /**
     * Logs out a user by revoking their refresh token.
     *
     * @param rawRefreshToken the refresh token to revoke
     */
    public void logout(String rawRefreshToken) {
        refreshTokenService.revoke(rawRefreshToken);
    }

    /**
     * Logs out a user from all sessions by revoking all refresh tokens.
     *
     * @param userId the user's ID
     */
    public void logoutAll(UserId userId) {
        refreshTokenService.revokeAllForUser(userId);
    }

    /**
     * Result of a successful authentication.
     */
    public record AuthenticationResult(
            String accessToken,
            String refreshToken,
            UserResponse user
    ) {
    }
}
