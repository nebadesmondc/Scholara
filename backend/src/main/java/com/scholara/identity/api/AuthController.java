package com.scholara.identity.api;

import com.scholara.identity.api.dto.ChangePasswordRequest;
import com.scholara.identity.api.dto.ForgotPasswordRequest;
import com.scholara.identity.api.dto.LoginRequest;
import com.scholara.identity.api.dto.RegisterRequest;
import com.scholara.identity.api.dto.ResendVerificationRequest;
import com.scholara.identity.api.dto.ResetPasswordRequest;
import com.scholara.identity.api.dto.UserResponse;
import com.scholara.identity.api.dto.VerifyEmailRequest;
import com.scholara.identity.domain.model.EmailVerificationToken;
import com.scholara.identity.domain.model.User;
import com.scholara.identity.domain.service.AuthenticationService;
import com.scholara.identity.domain.service.EmailVerificationService;
import com.scholara.identity.domain.service.PasswordResetService;
import com.scholara.identity.domain.service.UserService;
import com.scholara.identity.infrastructure.security.CookieService;
import com.scholara.identity.infrastructure.security.ScholaraPrincipal;
import com.scholara.shared.domain.Email;
import com.scholara.shared.event.SendVerificationEmailEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication endpoints.
 */
@RestController
@RequestMapping("/v1/auth")
@Validated
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;
    private final CookieService cookieService;
    private final ApplicationEventPublisher eventPublisher;


    @Operation(summary = "Register a new user")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        Email email = Email.of(request.email());
        User user = userService.registerLocalUser(email, request.password(), request.role());

        // Create verification token and publish event to send email
        EmailVerificationToken token = emailVerificationService.createVerificationToken(user);
        eventPublisher.publishEvent(new SendVerificationEmailEvent(
                user.userId(),
                email,
                token.getOtpCode()
        ));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponse.from(user));
    }

    @Operation(summary = "Authenticate a user and set cookies")
    @ApiResponse(responseCode = "200", description = "User authenticated successfully")
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletResponse response) {
        Email email = Email.of(request.email());
        AuthenticationService.AuthenticationResult result =
                authenticationService.authenticate(email, request.password());

        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieService.createAccessTokenCookie(result.accessToken()).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieService.createRefreshTokenCookie(result.refreshToken()).toString());

        return ResponseEntity.ok(result.user());
    }

    @Operation(summary = "Logs out the user")
    @ApiResponse(responseCode = "204", description = "User logged out successfully")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                        HttpServletResponse response) {
        cookieService.extractRefreshToken(request)
                .ifPresent(authenticationService::logout);

        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieService.createLogoutAccessCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieService.createLogoutRefreshCookie().toString());

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Refreshes the access token")
    @ApiResponse(responseCode = "204", description = "Access token refreshed successfully")
    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest request,
                                         HttpServletResponse response) {
        String refreshToken = cookieService.extractRefreshToken(request)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        AuthenticationService.AuthenticationResult result =
                authenticationService.refreshAccessToken(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieService.createAccessTokenCookie(result.accessToken()).toString());

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "User details retrieved successfully")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal ScholaraPrincipal principal) {
        return ResponseEntity.ok(UserResponse.from(principal.user()));
    }

    @Operation(summary = "Verify user's email")
    @ApiResponse(responseCode = "204", description = "Email verified successfully")
    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        emailVerificationService.verifyEmail(request.userId(), request.otp());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change the authenticated user's password")
    @ApiResponse(responseCode = "204", description = "Password changed successfully")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal ScholaraPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {

        // Verify current password by attempting authentication
        Email email = Email.of(principal.getUsername());
        authenticationService.authenticate(email, request.currentPassword());

        // Change password
        userService.changePassword(principal.user(), request.newPassword());

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Initiate password reset")
    @ApiResponse(responseCode = "202", description = "Password reset initiated")
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.initiatePasswordReset(Email.of(request.email()));
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Reset the password")
    @ApiResponse(responseCode = "204", description = "Password reset successfully")
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Resend email verification OTP")
    @ApiResponse(responseCode = "202", description = "Verification email resent")
    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(
            @Valid @RequestBody ResendVerificationRequest request) {

        Email email = Email.of(request.email());
        userService.findByEmail(email).ifPresent(user -> {
            if (!user.isEmailVerified()) {
                EmailVerificationToken token = emailVerificationService.resendVerification(user);
                eventPublisher.publishEvent(new SendVerificationEmailEvent(
                        user.userId(),
                        email,
                        token.getOtpCode()
                ));
            }
        });

        return ResponseEntity.accepted().build();
    }
}
