package com.netease.spring.handler.parser;

import org.springframework.beans.*;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/****
 *Author:wennaisong 2018/05/18
 */
public class BaseDefinitionParser extends AbstractSingleBeanDefinitionParser {

    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        NamedNodeMap namedNodeMap = element.getAttributes();
        for (int index = 0; index < namedNodeMap.getLength(); index++) {
            Node node = namedNodeMap.item(index);
            String name = node.getNodeName();
            String value = node.getNodeValue();
            bean.addPropertyValue(name, value);
        }
    }

    protected Object instantiateBean(AbstractBeanDefinition beanDefinition) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, IntrospectionException {
        Constructor<?> constructorToUse = beanDefinition.getBeanClass().getDeclaredConstructor((Class[]) null);
        Object instance = BeanUtils.instantiateClass(constructorToUse);
        MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
        BeanWrapperImpl bw = new BeanWrapperImpl(instance);
        BeanInfo beanInfo = Introspector.getBeanInfo(beanDefinition.getBeanClass());
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            PropertyValue propertyValue = propertyValues.getPropertyValue(pd.getName());
            if (propertyValue != null) {
                String propertyName = propertyValue.getName();
                Object value = propertyValue.getValue();
                TypeDescriptor td = new TypeDescriptor(new Property(beanDefinition.getBeanClass(), pd.getReadMethod(), pd.getWriteMethod(), pd.getName()));
                boolean convertible = bw.isWritableProperty(propertyName) && !PropertyAccessorUtils.isNestedOrIndexedProperty(propertyName);
                Object convertedValue = value;
                if (convertible) {
                    convertedValue = bw.convertForProperty(value, propertyName);
                }
                Method writeMethod = pd.getWriteMethod();
                writeMethod.invoke(instance, convertedValue);
            }
        }
        return instance;
    }

    public AbstractBeanDefinition parseBean(Element element, ParserContext parserContext) {
        AbstractBeanDefinition definition = parseInternal(element, parserContext);
        return definition;
    }
}
