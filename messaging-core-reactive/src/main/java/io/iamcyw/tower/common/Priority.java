package io.iamcyw.tower.common;

import java.lang.annotation.*;

/**
 * Indicates the relative priority of the annotated component. Components with a higher priority are considered before
 * those with lower priority.
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Priority {

    /**
     * Value indicating the annotated member should come last.
     */
    int LAST = Integer.MIN_VALUE;
    /**
     * Value indicating the annotated member should be placed at the "lower half".
     */
    int LOW = Integer.MIN_VALUE / 2;
    /**
     * Value indicating the annotated member should have medium priority, effectively placing it "in the middle".
     */
    int NEUTRAL = 0;
    /**
     * Value indicating the annotated member should have high priority, effectively placing it "in the first half".
     */
    int HIGH = Integer.MAX_VALUE / 2;

    /**
     * Value indicating the annotated member should be the very first
     */
    int FIRST = Integer.MAX_VALUE;

    /**
     * A value indicating the priority. Members with higher values must come before members with a lower value
     */
    int value() default NEUTRAL;

}
