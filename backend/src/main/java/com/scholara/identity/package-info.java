/**
 * Identity Module - Authentication, Authorization, and User Management.
 *
 * <p>This module is responsible for:
 * <ul>
 *   <li>User authentication and session management</li>
 *   <li>Authorization and access control</li>
 *   <li>User lifecycle management (registration, profile updates)</li>
 *   <li>Role and permission management</li>
 * </ul>
 *
 * <p>Module Boundaries:
 * <ul>
 *   <li>No dependencies on other business modules</li>
 *   <li>May only depend on the shared module</li>
 *   <li>Exposes user identity through well-defined APIs</li>
 * </ul>
 *
 * @see com.scholara.shared
 */
@org.springframework.modulith.ApplicationModule(
    allowedDependencies = "shared"
)
package com.scholara.identity;
