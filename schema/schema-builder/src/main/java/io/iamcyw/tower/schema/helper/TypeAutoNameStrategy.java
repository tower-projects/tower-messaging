package io.iamcyw.tower.schema.helper;

/**
 * Naming strategy for type
 */
public enum TypeAutoNameStrategy {
    Default, // Spec compliant
    MergeInnerClass, // Inner class prefix parent name
    Full // Use fully qualified name
}
