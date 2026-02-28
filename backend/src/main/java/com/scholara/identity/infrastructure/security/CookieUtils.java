package com.scholara.identity.infrastructure.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Optional;

/**
 * Utility class for HTTP cookie operations.
 *
 * <p>Provides methods for creating, reading, deleting, and serializing cookies.
 * Used primarily for storing OAuth2 authorization requests in stateless authentication flows.
 */
public final class CookieUtils {

    private CookieUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Retrieves a cookie by name from the request.
     *
     * @param request the HTTP request
     * @param name    the cookie name
     * @return an Optional containing the cookie if found
     */
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Adds a cookie to the response using ResponseCookie for proper SameSite support.
     *
     * @param response the HTTP response
     * @param name     the cookie name
     * @param value    the cookie value
     * @param maxAge   the maximum age in seconds
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/api")
                .httpOnly(true)
                .maxAge(maxAge)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * Deletes a cookie by setting its max age to 0.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @param name     the cookie name to delete
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    ResponseCookie deleteCookie = ResponseCookie.from(name, "")
                            .path("/api")
                            .httpOnly(true)
                            .maxAge(0)
                            .sameSite("Lax")
                            .build();
                    response.addHeader("Set-Cookie", deleteCookie.toString());
                }
            }
        }
    }

    /**
     * Serializes an object to a Base64-encoded string.
     *
     * @param object the object to serialize
     * @return the Base64-encoded string representation
     */
    public static String serialize(Object object) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            return Base64.getUrlEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to serialize object", e);
        }
    }

    /**
     * Deserializes a Base64-encoded cookie value back to an object.
     *
     * @param cookie the cookie containing the serialized data
     * @param cls    the target class type
     * @param <T>    the type parameter
     * @return the deserialized object
     */
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        byte[] bytes = Base64.getUrlDecoder().decode(cookie.getValue());
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return cls.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Failed to deserialize cookie", e);
        }
    }
}
