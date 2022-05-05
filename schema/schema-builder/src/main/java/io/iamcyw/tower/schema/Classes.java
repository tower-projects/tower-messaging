package io.iamcyw.tower.schema;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class helper
 */
public class Classes {

    private static final DotName BYTE = DotName.createSimple(Byte.class.getName());

    private static final DotName BYTE_PRIMATIVE = DotName.createSimple(byte.class.getName());

    private static final DotName SHORT = DotName.createSimple(Short.class.getName());

    private static final DotName SHORT_PRIMATIVE = DotName.createSimple(short.class.getName());

    private static final DotName INTEGER = DotName.createSimple(Integer.class.getName());

    private static final DotName INTEGER_PRIMATIVE = DotName.createSimple(int.class.getName());

    private static final DotName INTEGER_OPTIONAL = DotName.createSimple(OptionalInt.class.getName());

    private static final DotName INTEGER_ATOMIC = DotName.createSimple(AtomicInteger.class.getName());

    private static final DotName BIG_INTEGER = DotName.createSimple(BigInteger.class.getName());

    private static final DotName DOUBLE = DotName.createSimple(Double.class.getName());

    private static final DotName DOUBLE_PRIMATIVE = DotName.createSimple(double.class.getName());

    private static final DotName DOUBLE_OPTIONAL = DotName.createSimple(OptionalDouble.class.getName());

    private static final DotName BIG_DECIMAL = DotName.createSimple(BigDecimal.class.getName());

    private static final DotName LONG = DotName.createSimple(Long.class.getName());

    private static final DotName LONG_PRIMATIVE = DotName.createSimple(long.class.getName());

    private static final DotName LONG_OPTIONAL = DotName.createSimple(OptionalLong.class.getName());

    private static final DotName LONG_ATOMIC = DotName.createSimple(AtomicLong.class.getName());

    private static final DotName FLOAT = DotName.createSimple(Float.class.getName());

    private static final DotName FLOAT_PRIMATIVE = DotName.createSimple(float.class.getName());

    private static final List<DotName> KNOWN_COLLECTIONS = new ArrayList<>();

    private static final List<DotName> KNOWN_MAPS = new ArrayList<>();

    public static final DotName SERIALIZABLE = DotName.createSimple(Serializable.class.getName());

    public static final DotName OBJECT = DotName.createSimple(Object.class.getName());

    public static final DotName COLLECTION = DotName.createSimple(Collection.class.getName());

    public static final DotName LIST = DotName.createSimple(List.class.getName());

    public static final DotName LINKED_LIST = DotName.createSimple(LinkedList.class.getName());

    public static final DotName VECTOR = DotName.createSimple(Vector.class.getName());

    public static final DotName ARRAY_LIST = DotName.createSimple(ArrayList.class.getName());

    public static final DotName STACK = DotName.createSimple(Stack.class.getName());

    public static final DotName SET = DotName.createSimple(Set.class.getName());

    public static final DotName HASH_SET = DotName.createSimple(HashSet.class.getName());

    public static final DotName SORTED_SET = DotName.createSimple(SortedSet.class.getName());

    public static final DotName TREE_SET = DotName.createSimple(TreeSet.class.getName());

    public static final DotName QUEUE = DotName.createSimple(Queue.class.getName());

    public static final DotName DEQUE = DotName.createSimple(Deque.class.getName());

    public static final DotName MAP = DotName.createSimple(Map.class.getName());

    public static final DotName HASH_MAP = DotName.createSimple(HashMap.class.getName());

    public static final DotName TREE_MAP = DotName.createSimple(TreeMap.class.getName());

    public static final DotName HASHTABLE = DotName.createSimple(Hashtable.class.getName());

    public static final DotName SORTED_MAP = DotName.createSimple(SortedMap.class.getName());

    public static final DotName ENTRY = DotName.createSimple("io.smallrye.graphql.api.Entry");

    public static final DotName OPTIONAL = DotName.createSimple(Optional.class.getName());

    public static final DotName ENUM = DotName.createSimple(Enum.class.getName());

    public static final DotName RECORD = DotName.createSimple("java.lang.Record");

    public static final DotName LOCALDATE = DotName.createSimple(LocalDate.class.getName());

    public static final DotName LOCALDATETIME = DotName.createSimple(LocalDateTime.class.getName());

    public static final DotName LOCALTIME = DotName.createSimple(LocalTime.class.getName());

    public static final DotName ZONEDDATETIME = DotName.createSimple(ZonedDateTime.class.getName());

    public static final DotName OFFSETDATETIME = DotName.createSimple(OffsetDateTime.class.getName());

    public static final DotName OFFSETTIME = DotName.createSimple(OffsetTime.class.getName());

    public static final DotName INSTANT = DotName.createSimple(Instant.class.getName());

    public static final DotName PERIOD = DotName.createSimple(Period.class.getName());

    public static final DotName DURATION = DotName.createSimple(Duration.class.getName());

    public static final DotName UTIL_DATE = DotName.createSimple(Date.class.getName());

    public static final DotName SQL_DATE = DotName.createSimple(java.sql.Date.class.getName());

    public static final DotName SQL_TIMESTAMP = DotName.createSimple(java.sql.Timestamp.class.getName());

    public static final DotName SQL_TIME = DotName.createSimple(java.sql.Time.class.getName());

    // Adapters
    public static final DotName JSONB_ADAPTER = DotName.createSimple("javax.json.bind.adapter.JsonbAdapter");

    public static final DotName ADAPTER = DotName.createSimple("io.smallrye.graphql.api.Adapter");

    static {
        KNOWN_COLLECTIONS.add(COLLECTION);
        KNOWN_COLLECTIONS.add(LIST);
        KNOWN_COLLECTIONS.add(LINKED_LIST);
        KNOWN_COLLECTIONS.add(VECTOR);
        KNOWN_COLLECTIONS.add(ARRAY_LIST);
        KNOWN_COLLECTIONS.add(STACK);
        KNOWN_COLLECTIONS.add(SET);
        KNOWN_COLLECTIONS.add(HASH_SET);
        KNOWN_COLLECTIONS.add(SORTED_SET);
        KNOWN_COLLECTIONS.add(TREE_SET);
        KNOWN_COLLECTIONS.add(QUEUE);
        KNOWN_COLLECTIONS.add(DEQUE);

        KNOWN_MAPS.add(MAP);
        KNOWN_MAPS.add(HASH_MAP);
        KNOWN_MAPS.add(TREE_MAP);
        KNOWN_MAPS.add(HASHTABLE);
        KNOWN_MAPS.add(SORTED_MAP);
    }

    private Classes() {
    }

    public static boolean isWrapper(Type type) {
        return isParameterized(type) || isArray(type);
    }

    /**
     * Check if this is a Parameterized type
     *
     * @param type
     * @return
     */
    public static boolean isParameterized(Type type) {
        return type.kind().equals(Type.Kind.PARAMETERIZED_TYPE);
    }

    /**
     * Check if a certain type is Optional
     *
     * @param type the type
     * @return true if it is
     */
    public static boolean isOptional(Type type) {
        return isParameterized(type) && type.name().equals(OPTIONAL) // Normal Optional
                || type.name().equals(LONG_OPTIONAL) || type.name().equals(DOUBLE_OPTIONAL) ||
                type.name().equals(INTEGER_OPTIONAL);
    }

    /**
     * Check if a certain class is an interface
     *
     * @param classInfo the class to check
     * @return true if it is
     */
    public static boolean isInterface(ClassInfo classInfo) {
        if (classInfo == null)
            return false;
        return Modifier.isInterface(classInfo.flags());
    }

    /**
     * Check if a certain class is an enum
     *
     * @param classInfo the class to check
     * @return true if it is
     */
    public static boolean isEnum(ClassInfo classInfo) {
        if (classInfo == null)
            return false;
        return ENUM.equals(classInfo.superName());
    }

    /**
     * Check if this type is a Number (or collection of numbers)
     *
     * @param type the type to check
     * @return true if it is
     */
    public static boolean isNumberLikeTypeOrContainedIn(Type type) {
        return isTypeOrContainedIn(type, BYTE, BYTE_PRIMATIVE, SHORT, SHORT_PRIMATIVE, INTEGER, INTEGER_PRIMATIVE,
                                   BIG_INTEGER, DOUBLE, DOUBLE_PRIMATIVE, BIG_DECIMAL, LONG, LONG_PRIMATIVE, FLOAT,
                                   FLOAT_PRIMATIVE, INTEGER_OPTIONAL, DOUBLE_OPTIONAL, LONG_OPTIONAL, INTEGER_ATOMIC,
                                   LONG_ATOMIC);
    }

    /**
     * Check if this type is a Date (or collection of numbers)
     *
     * @param type the type to check
     * @return true if it is
     */
    public static boolean isDateLikeTypeOrContainedIn(Type type) {
        return isTypeOrContainedIn(type, LOCALDATE, LOCALTIME, LOCALDATETIME, ZONEDDATETIME, OFFSETDATETIME, OFFSETTIME,
                                   UTIL_DATE, SQL_DATE, SQL_TIMESTAMP, SQL_TIME, INSTANT);
    }

    private static boolean isTypeOrContainedIn(Type type, DotName... valid) {
        switch (type.kind()) {
            case PARAMETERIZED_TYPE:
                // Container
                Type typeInCollection = type.asParameterizedType().arguments().get(0);
                return isTypeOrContainedIn(typeInCollection, valid);
            case ARRAY:
                // Array
                Type typeInArray = type.asArrayType().component();
                return isTypeOrContainedIn(typeInArray, valid);
            default:
                for (DotName dotName : valid) {
                    if (type.name().toString().equals(dotName.toString())) {
                        return true;
                    }
                }
                return false;
        }
    }

    /**
     * Return true if this is an array
     *
     * @param type
     * @return
     */
    public static boolean isArray(Type type) {
        return type.kind().equals(Type.Kind.ARRAY);
    }

    /**
     * Return true if type is java Collection type which is handled as GraphQL array
     *
     * @param type to check
     * @return if this is a collection
     */
    public static boolean isCollection(Type type) {
        if (isParameterized(type)) {

            ClassInfo clazz = ScanningContext.getIndex().getClassByName(type.name());
            if (clazz == null) {
                // use classloader instead of jandex to handle basic java classes/interfaces
                try {
                    Class<?> clazzLoaded = Classes.class.getClassLoader().loadClass(type.name().toString());
                    return Collection.class.isAssignableFrom(clazzLoaded);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(
                            "Info not found in Jandex index nor classpath for class name:" + type.name());
                }
            }
            if (KNOWN_COLLECTIONS.contains(clazz.name())) {
                return true;
            }

            // we have to go recursively over all super-interfaces as Jandex provides only direct interfaces
            // implemented in the class itself
            for (Type intf : clazz.interfaceTypes()) {
                if (isCollection(intf)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isMap(Type type) {
        if (isParameterized(type)) {

            ClassInfo clazz = ScanningContext.getIndex().getClassByName(type.name());
            if (clazz == null) {
                // use classloader instead of jandex to handle basic java classes/interfaces
                try {
                    Class<?> clazzLoaded = Classes.class.getClassLoader().loadClass(type.name().toString());
                    return Map.class.isAssignableFrom(clazzLoaded);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(
                            "Info not found in Jandex index nor classpath for class name:" + type.name());
                }
            }
            if (KNOWN_MAPS.contains(clazz.name())) {
                return true;
            }

            // we have to go recursively over all super-interfaces as Jandex provides only direct interfaces
            // implemented in the class itself
            for (Type intf : clazz.interfaceTypes()) {
                if (isMap(intf)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return true if given type is parametrized type unwrapped/handled by the runtime before the serialization
     * (Optional&lt;&gt;, CompletableFutur&lt;&gt;, CompletionStage&lt;&gt; etc)
     *
     * @param type to be checked
     * @return true if type is unwrapped by the runtime
     */
    @Deprecated
    public static boolean isUnwrappedType(Type type) {
        return isParameterized(type) && (isOptional(type) // Optional<>
        );
    }

}
