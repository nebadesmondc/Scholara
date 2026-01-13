/**
 * Notification Module - Communication and Alerts.
 *
 * <p>This module is responsible for:
 * <ul>
 *   <li>Email notifications</li>
 *   <li>In-app notifications</li>
 *   <li>Notification preferences management</li>
 *   <li>Notification delivery tracking</li>
 * </ul>
 *
 * <p>Module Boundaries:
 * <ul>
 *   <li>Depends on identity for user contact information</li>
 *   <li>Depends on shared for common types and events</li>
 *   <li>Operates purely through event consumption</li>
 *   <li>No direct module calls from other modules</li>
 * </ul>
 *
 * <p>Event-Driven Design:
 * This module is entirely event-driven and does not expose
 * any APIs for other modules to call directly.
 *
 * @see com.scholara.shared
 * @see com.scholara.identity
 */
@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {"shared", "identity"}
)
package com.scholara.notification;
