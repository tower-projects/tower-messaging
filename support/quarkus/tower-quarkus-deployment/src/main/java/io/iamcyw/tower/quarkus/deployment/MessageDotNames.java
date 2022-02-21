package io.iamcyw.tower.quarkus.deployment;


import io.iamcyw.tower.commandhandling.CommandHandle;
import io.iamcyw.tower.messaging.Message;
import io.iamcyw.tower.messaging.MetaData;
import io.iamcyw.tower.messaging.handle.resolve.MetaDataValue;
import io.iamcyw.tower.messaging.predicate.MessagePredicate;
import io.iamcyw.tower.messaging.predicate.MessagePredicates;
import io.iamcyw.tower.messaging.predicate.PredicateHandle;
import io.iamcyw.tower.queryhandling.QueryHandle;
import org.jboss.jandex.DotName;

public interface MessageDotNames {

    DotName COMMANDHANDLER = DotName.createSimple(CommandHandle.class.getName());

    DotName QUERYHANDLER = DotName.createSimple(QueryHandle.class.getName());

    DotName PREDICATEHANDLE = DotName.createSimple(PredicateHandle.class.getName());

    DotName METADATA = DotName.createSimple(MetaData.class.getName());

    DotName METADATAVALUE = DotName.createSimple(MetaDataValue.class.getName());

    DotName MESSAGE = DotName.createSimple(Message.class.getName());

    DotName MESSAGE_PREDICATE = DotName.createSimple(MessagePredicate.class.getName());

    DotName MESSAGE_PREDICATES = DotName.createSimple(MessagePredicates.class.getName());

}
