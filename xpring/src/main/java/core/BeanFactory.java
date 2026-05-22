package core;

import java.util.Collection;
import java.util.List;

public interface BeanFactory {
    void reset();
    <T> T getBean(Class<T> clazz);
    Object getBean(String name);
    Collection<Object> getBeans();
    <T> List<T> getBeansOfType(Class<T> clazz);
    void registerBean(Object bean);
    void registerBean(String name, Object bean);
}
