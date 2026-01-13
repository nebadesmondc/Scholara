/**
 * Progress Module - Learning Analytics and Performance Tracking.
 *
 * <p>This module is responsible for:
 * <ul>
 *   <li>Learning progress tracking</li>
 *   <li>Performance analytics and reporting</li>
 *   <li>Achievement and milestone tracking</li>
 *   <li>Aggregated statistics computation</li>
 * </ul>
 *
 * <p>Module Boundaries:
 * <ul>
 *   <li>Depends on identity for user information</li>
 *   <li>Depends on shared for common types and events</li>
 *   <li>Consumes events from assessment and content modules</li>
 *   <li>No direct dependencies on assessment or content</li>
 * </ul>
 *
 * <p>Event-Driven Design:
 * This module primarily operates through event consumption,
 * maintaining eventual consistency with source modules.
 *
 * @see com.scholara.shared
 * @see com.scholara.identity
 */
@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {"shared", "identity"}
)
package com.scholara.progress;
