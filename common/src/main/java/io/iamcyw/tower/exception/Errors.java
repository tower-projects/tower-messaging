package io.iamcyw.tower.exception;


import io.iamcyw.tower.utils.lang.NonNull;
import io.iamcyw.tower.utils.lang.Nullable;

public class Errors {

    public static Builder create(String key) {
        return new Builder(key);
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        @Nullable
        private String key;

        @NonNull
        private String msg;

        @Nullable
        private Object[] args;

        public Builder(String key) {
            this.key = key;
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
            return new ErrorMessage(key, msg, args);
        }

    }

}
