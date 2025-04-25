package org.xiaoxingbomei.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class Spring_Utils implements BeanFactoryPostProcessor {
    private static ConfigurableListableBeanFactory beanFactory;


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        Spring_Utils.beanFactory = configurableListableBeanFactory;
    }


    public static <T> T getBean(String name) throws BeansException {
        return (T) beanFactory.getBean(name);
    }
    public static <T> T getBean(Class<T> clz) throws BeansException
    {
        T result = (T) beanFactory.getBean(clz);
        return result;
    }
}
