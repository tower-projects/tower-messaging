package io.iamcyw.tower.messaging.handle;

import io.iamcyw.tower.schema.model.Operation;

import java.util.Objects;

public class Identifier {
    private final String command;

    private final String field;

    public Identifier(String command, String field) {
        this.command = command;
        this.field = field;
    }

    public Identifier(Operation operation) {
        this(operation.getArguments().get(0).getReference().getClassName(), operation.getReference().getClassName());
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
        hash = 73 * hash + Objects.hashCode(this.field);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Identifier that = (Identifier) o;

        if (!command.equals(that.command))
            return false;
        return field.equals(that.field);
    }

    @Override
    public String toString() {
        return "Identifier{" + "command='" + command + '\'' + ", field='" + field + '\'' + '}';
    }

}
