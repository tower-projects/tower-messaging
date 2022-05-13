package io.iamcyw.tower.messaging.responsetype;

import io.iamcyw.tower.schema.model.WrapperType;

public class VoidResponseType extends AbstractResponseType<Void> {
    protected VoidResponseType() {
        super(Void.class);
    }

    @Override
    public WrapperType responseMessagePayloadWrapperType() {
        return WrapperType.EMPTY;
    }

    @Override
    public String name() {
        return "void";
    }

}
