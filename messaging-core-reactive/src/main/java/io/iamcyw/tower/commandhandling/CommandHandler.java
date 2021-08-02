package io.iamcyw.tower.commandhandling;


import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE})
public @interface CommandHandler {

    /**
     * The name of the Command this handler listens to. Defaults to the fully qualified class name of the payload type
     * (i.e. first parameter).
     *
     * @return The command name
     */
    String commandName() default "";

    /**
     * The property of the command to be used as a routing key towards this command handler instance. If multiple
     * handlers instances are available, a sending component is responsible to route commands with the same routing key
     * value to the correct instance.
     *
     * @return The property of the command to use as routing key
     */
    String routingKey() default "";

    /**
     * The type of payload expected by this handler. Defaults to the expected types expresses by (primarily the first)
     * parameters of the annotated Method or Constructor.
     *
     * @return the payload type expected by this handler
     */
    Class<?> payloadType() default Object.class;

}
