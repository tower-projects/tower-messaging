package io.iamcyw.tower.config;

import io.iamcyw.tower.utils.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.function.Function;
import java.util.function.Supplier;

public class Component<B> {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup()
                                                                         .lookupClass());

    private final String name;

    private final Supplier<Configuration> configSupplier;

    private Function<Configuration, ? extends B> builderFunction;

    private B instance;

    public Component(Configuration config, String name, Function<Configuration, ? extends B> builderFunction) {
        this(() -> config, name, builderFunction);
    }

    public Component(Supplier<Configuration> config, String name, Function<Configuration, ? extends B> builderFunction) {
        this.configSupplier = config;
        this.name = name;
        this.builderFunction = builderFunction;
    }


    /**
     * Retrieves the object contained in this component, triggering the builder function if the component hasn't been
     * built yet. Upon initiation of the instance the {@link LifecycleHandlerInspector#registerLifecycleHandlers(Configuration,
     * Object)} methods will be called to resolve and register lifecycle methods.
     *
     * @return the initialized component contained in this instance
     */
    public B get() {
        if (instance == null) {
            Configuration configuration = configSupplier.get();
            instance = builderFunction.apply(configuration);
            logger.debug("Instantiated component [{}]: {}", name, instance);
            LifecycleHandlerInspector.registerLifecycleHandlers(configuration, instance);
        }
        return instance;
    }

    /**
     * Updates the builder function for this component.
     *
     * @param builderFunction The new builder function for the component
     * @throws IllegalStateException when the component has already been retrieved using {@link #get()}.
     */
    public void update(Function<Configuration, ? extends B> builderFunction) {
        Assert.state(instance == null, () -> "Cannot change " + name + ": it is already in use");
        this.builderFunction = builderFunction;
    }

    /**
     * Checks if the component is already initialized.
     *
     * @return true if component is initialized
     */
    public boolean isInitialized() {
        return instance != null;
    }

}
