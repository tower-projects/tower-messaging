package io.iamcyw.tower.config;

public interface ConfigureModule {

    void configureModule(Configure configurer);

    /**
     * Returns the relative order this configurer should be invoked, compared to other intstances.
     * <p>
     * Use lower (negative) values for modules providing sensible defaults, and higher values for modules overriding
     * values potentially previously set.
     *
     * @return the order in which this configurer should be invoked
     */
    default int order() {
        return 0;
    }

}
