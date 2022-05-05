package io.iamcyw.tower.schema.model;

/**
 * Represent an wrapper type in the Schema.
 */
public enum WrapperType {
    OPTIONAL,
    COLLECTION,
    MAP,
    ARRAY,
    UNKNOWN // Could be a plugged in type, or normal generics
}