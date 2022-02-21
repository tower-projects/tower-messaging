package io.iamcyw.tower.messaging;

public class MessageBean {

    private String name;

    private BeanFactory<?> beanFactory;

    public MessageBean() {
        this(null, null);
    }

    public MessageBean(String name, BeanFactory<?> beanFactory) {
        this.beanFactory = beanFactory;
        this.name = name;
    }

    public BeanFactory<?> getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory<?> beanFactory) {
        this.beanFactory = beanFactory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}