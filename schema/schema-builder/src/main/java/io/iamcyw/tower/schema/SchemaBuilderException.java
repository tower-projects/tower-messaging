package io.iamcyw.tower.schema;

/**
 * Runtime exception when we could not create a type (input or output) while building the schema
 * or the schema itself
 */
public class SchemaBuilderException extends RuntimeException {

    public SchemaBuilderException() {
    }

    public SchemaBuilderException(String string) {
        super(string);
    }

    public SchemaBuilderException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public SchemaBuilderException(Throwable thrwbl) {
        super(thrwbl);
    }

    public SchemaBuilderException(String string, Throwable thrwbl, boolean bln, boolean bln1) {
        super(string, thrwbl, bln, bln1);
    }

}
