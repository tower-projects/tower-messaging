package io.iamcyw.tower.messaging;


/**
 * Abstract implementation of a {@link Message} that delegates to an existing message. Extend this decorator class to
 * extend the message with additional features.
 */
public abstract class MessageDecorator implements Message {

    private static final long serialVersionUID = 3969631713723578521L;

    private final Message delegate;

    /**
     * Initializes a new decorator with given {@code delegate} message. The decorator delegates to the delegate for
     * the message's payload, metadata and identifier.
     *
     * @param delegate the message delegate
     */
    protected MessageDecorator(Message delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getIdentifier() {
        return delegate.getIdentifier();
    }

    @Override
    public MetaData getMetaData() {
        return delegate.getMetaData();
    }

    @Override
    public Object getPayload() {
        return delegate.getPayload();
    }

    @Override
    public Class getPayloadType() {
        return delegate.getPayloadType();
    }


    /**
     * Returns the wrapped message delegate.
     *
     * @return the delegate message
     */
    protected Message getDelegate() {
        return delegate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(describeType()).append("{");
        describeTo(sb);
        return sb.append("}").toString();
    }

    /**
     * Describe the message specific properties to the given {@code stringBuilder}. Subclasses should override this
     * method, calling the super method and appending their own properties to the end (or beginning).
     * <p>
     * As convention, String values should be enclosed in single quotes, Objects in curly brackets and numeric values
     * may be appended without enclosing. All properties should be preceded by a comma when appending, or finish with a
     * comma when prefixing values.
     *
     * @param stringBuilder the builder to append data to
     */
    protected void describeTo(StringBuilder stringBuilder) {
        stringBuilder.append("payload={").append(getPayload()).append('}').append(", metadata={").append(getMetaData())
                     .append('}').append(", messageIdentifier='").append(getIdentifier()).append('\'');
    }

    /**
     * Describe the type of message, used in {@link #toString()}.
     * <p>
     * Defaults to the simple class name of the actual instance.
     *
     * @return the type of message
     */
    protected String describeType() {
        return getClass().getSimpleName();
    }

}
