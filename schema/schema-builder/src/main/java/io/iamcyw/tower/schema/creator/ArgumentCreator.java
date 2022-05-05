package io.iamcyw.tower.schema.creator;

import io.iamcyw.tower.schema.Annotations;
import io.iamcyw.tower.schema.SchemaBuilderException;
import io.iamcyw.tower.schema.helper.Direction;
import io.iamcyw.tower.schema.helper.MethodHelper;
import io.iamcyw.tower.schema.model.Argument;
import io.iamcyw.tower.schema.model.Operation;
import io.iamcyw.tower.schema.model.Reference;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;
import org.jboss.logging.Logger;

import java.util.Optional;

/**
 * Creates a Argument object
 *
 * @author Phillip Kruger (phillip.kruger@redhat.com)
 */
public class ArgumentCreator extends ModelCreator {

    private Logger logger = Logger.getLogger(ArgumentCreator.class.getName());

    public ArgumentCreator(ReferenceCreator referenceCreator) {

        super(referenceCreator);
    }

    /**
     * Create an argument model. Arguments exist on Operations as input parameters
     *
     * @param operation  The operation
     * @param methodInfo the operation method
     * @param position   the argument position
     * @return an Argument
     */
    public Optional<Argument> createArgument(Operation operation, MethodInfo methodInfo, short position) {
        if (position >= methodInfo.parameters().size()) {
            throw new SchemaBuilderException(
                    "Can not create argument for parameter [" + position + "] " + "on method [" +
                            methodInfo.declaringClass().name() + "#" + methodInfo.name() + "]: " + "method has only " +
                            methodInfo.parameters().size() + " parameters");
        }

        Annotations annotationsForThisArgument = Annotations.getAnnotationsForArgument(methodInfo, position);

        // Argument Type
        Type argumentType = methodInfo.parameters().get(position);

        // Name
        String defaultName = methodInfo.parameterName(position);

        Reference reference = referenceCreator.createReferenceForSourceArgument(argumentType,
                                                                                annotationsForThisArgument);


        Argument argument = new Argument(defaultName, methodInfo.name(),
                                         MethodHelper.getPropertyName(Direction.IN, methodInfo.name()), defaultName,
                                         reference);
        // set domain flag
        if (position == 0) {
            argument.setDomainArgument(true);
        }

        // parameter
        annotationsForThisArgument.getOneOfTheseAnnotations(Annotations.PARAMETER).ifPresent(annotationInstance -> {
            argument.setParameterArgument(true);
            argument.setName(annotationInstance.value().asString());
        });

        populateField(Direction.IN, argument, argumentType, annotationsForThisArgument);

        return Optional.of(argument);
    }

}
