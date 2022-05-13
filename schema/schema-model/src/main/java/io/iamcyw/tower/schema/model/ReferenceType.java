package io.iamcyw.tower.schema.model;

/**
 * Type of reference
 * <p>
 * Because we refer to types before they might exist, we need an indication of the type
 */
public enum ReferenceType {
    INPUT,
    TYPE,
}