package io.iamcyw.tower.exception;


import io.iamcyw.tower.utils.lang.NonNull;
import io.iamcyw.tower.utils.lang.Nullable;

public class Errors {

    private Errors() {
        throw new IllegalStateException("Utility class");
    }

    public static Builder create(String key) {
        return new Builder(key);
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        @Nullable
        private String error;

        @NonNull
        private String msg = "";

        @Nullable
        private Object[] args;

        public Builder(@Nullable String error) {
            this.error = error;
        }

        public Builder() {
        }

        public Builder content(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder args(Object... args) {
            this.args = args;
            return this;
        }

        public ErrorMessage apply() {
            return new ErrorMessage(error, msg, args);
        }

    }

}
