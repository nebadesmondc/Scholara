/**
 * Shared Module - Common Types and Infrastructure.
 *
 * <p>This module provides:
 * <ul>
 *   <li>Value objects used across modules</li>
 *   <li>Domain event definitions</li>
 *   <li>Common error models and exceptions</li>
 *   <li>Utility types and interfaces</li>
 * </ul>
 *
 * <p>Module Boundaries:
 * <ul>
 *   <li>No dependencies on any other module</li>
 *   <li>Contains no business logic</li>
 *   <li>All types are designed for immutability</li>
 * </ul>
 *
 * <p>Design Principles:
 * <ul>
 *   <li>All value objects are implemented as records</li>
 *   <li>Domain events use sealed interfaces where appropriate</li>
 *   <li>No service or repository classes</li>
 * </ul>
 */
@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {}
)
package com.scholara.shared;
