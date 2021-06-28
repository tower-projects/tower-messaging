package io.iamcyw.tower.config;

import io.iamcyw.tower.utils.Assert;
import io.iamcyw.tower.utils.i18n.I18ns;

import java.util.function.Function;
import java.util.function.Supplier;

public class Component<B> {

    private final String name;

    private final Supplier<Configuration> configuration;

    private Function<Configuration, ? extends B> builderFunction;

    private B instance;

    /**
     * Creates a component for the given {@code config} with given {@code name} created by the given
     * {@code builderFunction}. Then the Configuration is not initialized yet, consider using
     * {@link #Component(Supplier, String, Function)} instead.
     *
     * @param config          The Configuration the component is part of
     * @param name            The name of the component
     * @param builderFunction The builder function of the component
     */
    public Component(Configuration config, String name, Function<Configuration, ? extends B> builderFunction) {
        this(() -> config, name, builderFunction);
    }

    /**
     * Creates a component for the given {@code config} with given {@code name} created by the given
     * {@code builderFunction}.
     *
     * @param config          The supplier function of the configuration
     * @param name            The name of the component
     * @param builderFunction The builder function of the component
     */
    public Component(Supplier<Configuration> config, String name,
                     Function<Configuration, ? extends B> builderFunction) {
        this.configuration = config;
        this.name = name;
        this.builderFunction = builderFunction;
    }

    /**
     * Retrieves the object contained in this component, triggering the builder function if the component hasn't been
     * built yet.
     *
     * @return the initialized component contained in this instance
     */
    public B get() {
        if (instance == null) {
            instance = builderFunction.apply(configuration.get());
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
        Assert.state(instance == null,
                     I18ns.create().content("Cannot change {} : it is already in use").args(name).apply());
        this.builderFunction = builderFunction;
    }

}
