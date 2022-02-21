package io.iamcyw.tower.quarkus.deployment;

import io.quarkus.builder.item.SimpleBuildItem;
import org.jboss.jandex.ClassInfo;

import java.util.List;

public final class MessageClassBuildItem extends SimpleBuildItem {

    final List<ClassInfo> messageClasses;

    public MessageClassBuildItem(List<ClassInfo> messageClasses) {
        this.messageClasses = messageClasses;
    }

}
