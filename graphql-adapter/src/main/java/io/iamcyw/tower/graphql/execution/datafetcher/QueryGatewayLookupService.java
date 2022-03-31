package io.iamcyw.tower.graphql.execution.datafetcher;

import io.iamcyw.tower.queryhandling.gateway.QueryGateway;

import java.util.ServiceLoader;


/**
 * Lookup service that allows multiple DI frameworks to use this.
 * By default, plain old reflection will be used.
 */
public interface QueryGatewayLookupService {

    ServiceLoader<QueryGatewayLookupService> lookupServices = ServiceLoader.load(QueryGatewayLookupService.class);

    QueryGatewayLookupService lookupService = load();

    static QueryGatewayLookupService get() {
        return lookupService;
    }

    static QueryGatewayLookupService load() {
        QueryGatewayLookupService lookupService;
        try {
            lookupService = lookupServices.iterator().next();
        } catch (Exception ex) {
            lookupService = null;
        }
        return lookupService;
    }

    QueryGateway getQueryGateway();


    /**
     * Default Lookup service that gets used when none is provided with SPI.
     * This use reflection
     */
    // class DefaultLookupService implements QueryGatewayLookupService {
    //
    //     @Override
    //     public String getName() {
    //         return "Reflection (default)";
    //     }
    //
    //     @Override
    //     public Class<?> getClass(Class<?> declaringClass) {
    //         return declaringClass;
    //     }
    //
    //     @Override
    //     public <T> ManagedInstance<T> getInstance(Class<T> declaringClass) {
    //         try {
    //             return new DefaultManagedInstance<T>(declaringClass.getConstructor().newInstance());
    //         } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException |
    //         IllegalArgumentException | InvocationTargetException ex) {
    //             throw msg.countNotGetInstance(ex);
    //         }
    //     }
    //
    //     @Override
    //     public boolean isResolvable(Class<?> declaringClass) {
    //         return true;
    //     }
    //
    // }

}
