package io.iamcyw.tower.messaging.spi;

import io.iamcyw.tower.utils.Classes;

import java.util.ServiceLoader;

import static io.iamcyw.tower.messaging.TowerMessageServerLogging.log;
import static io.iamcyw.tower.messaging.TowerMessageServerMessages.msg;

/**
 * Classloading service that will load classes
 * By default, TCCL will be use.
 */
public interface ClassloadingService {

    ServiceLoader<ClassloadingService> classloadingServices = ServiceLoader.load(ClassloadingService.class);

    ClassloadingService classloadingService = load();

    static ClassloadingService get() {
        return classloadingService;
    }

    static ClassloadingService load() {
        ClassloadingService cls;
        try {
            cls = classloadingServices.iterator().next();
        } catch (Exception ex) {
            cls = new DefaultClassloadingService();
        }
        log.usingClassLoadingService(cls.getName());
        return cls;
    }

    String getName();

    default Class<?> loadClass(String className) {
        try {
            if (Classes.isPrimitive(className)) {
                return Classes.getPrimativeClassType(className);
            } else {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                if (loader != null) {
                    try {
                        return loadClass(className, loader);
                    } catch (ClassNotFoundException cnfe) {
                        // Let's try this class classloader.
                    }
                }
                return loadClass(className, ClassloadingService.class.getClassLoader());
            }
        } catch (ClassNotFoundException pae) {
            throw msg.canNotLoadClass(className, pae);
        }
    }

    default Class<?> loadClass(String className, ClassLoader loader) throws ClassNotFoundException {
        Class<?> c = Class.forName(className, false, loader);
        return c;
    }

    /**
     * Default Lookup service that gets used when none is provided with SPI.
     * This use reflection
     */
    class DefaultClassloadingService implements ClassloadingService {

        @Override
        public String getName() {
            return "TCCL (default)";
        }

    }

}
