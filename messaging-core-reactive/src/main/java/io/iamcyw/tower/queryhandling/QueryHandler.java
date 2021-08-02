package io.iamcyw.tower.queryhandling;


import io.iamcyw.tower.messaging.MetaData;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface QueryHandler {

    /**
     * The name of the Query this handler listens to. Defaults to the fully qualified class name of the payload type
     * (i.e. first parameter).
     *
     * @return The query name
     */
    String queryName() default "";

}
