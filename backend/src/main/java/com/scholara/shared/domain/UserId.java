package com.scholara.shared.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a unique user identifier.
 *
 * <p>Other modules should use UserId rather than raw UUIDs for type safety.
 */
public record UserId(UUID value) {

    public UserId {
        Objects.requireNonNull(value, "UserId value cannot be null");
    }

    /**
     * Generates a new random UserId.
     *
     * @return a new UserId with a random UUID
     */
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    /**
     * Creates a UserId from a string representation.
     *
     * @param value the UUID string
     * @return a UserId parsed from the string
     * @throws IllegalArgumentException if the string is not a valid UUID
     */
    public static UserId of(String value) {
        Objects.requireNonNull(value, "UserId string cannot be null");
        return new UserId(UUID.fromString(value));
    }

    /**
     * Creates a UserId from an existing UUID.
     *
     * @param value the UUID
     * @return a UserId wrapping the UUID
     */
    public static UserId of(UUID value) {
        return new UserId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
