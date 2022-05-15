package io.iamcyw.tower.messaging.handle;

import io.iamcyw.tower.StringPool;
import io.iamcyw.tower.schema.model.Operation;
import io.iamcyw.tower.utils.CommonKit;

import java.util.Objects;

public class Identifier {
    private final String command;

    private final String field;

    public Identifier(String command, String field) {
        this.command = CommonKit.withoutPackagePrefix(command);
        this.field = CommonKit.withoutPackagePrefix(field);
    }

    public Identifier(Operation operation) {
        this(operation.getArguments().get(0).getName(), operation.getReference().getName());
    }

    public String getCommand() {
        return command;
    }

    public String getField() {
        return field;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.command);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Identifier that = (Identifier) o;

        if (!command.equals(that.command) && !command.endsWith(that.command)) {
            return false;
        }
        if (StringPool.ASTERISK.equals(field) || StringPool.ASTERISK.equals(that.field)) {
            return true;
        }

        return field.equals(that.field);
    }

    @Override
    public String toString() {
        return "Identifier{" + "command='" + command + '\'' + ", field='" + field + '\'' + '}';
    }

}
