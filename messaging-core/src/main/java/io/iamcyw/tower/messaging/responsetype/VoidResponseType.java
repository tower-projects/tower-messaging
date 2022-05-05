package io.iamcyw.tower.messaging.responsetype;

import java.lang.reflect.Type;

public class VoidResponseType extends AbstractResponseType<Void> {
    protected VoidResponseType() {
        super(Void.class);
    }

    @Override
    public boolean matches(Type responseType) {
        return responseType.getTypeName().equals("void");
    }

    @Override
    public Class<Void> responseMessagePayloadType() {
        return Void.class;
    }

    @Override
    public String name() {
        return "void";
    }

}
