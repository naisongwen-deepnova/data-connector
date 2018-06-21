package com.netease.spring.handler;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.*;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.Method;

public class BeanDefinitionTest {
    public static void main(String[] args) {
        // 创建IOC容器上下文
        GenericApplicationContext context = new GenericApplicationContext();

        // 定义GenericBeanDefinition
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.getPropertyValues().add("proxyClassName", PrintClass.class.getCanonicalName());
        beanDefinition.setBeanClass(ProxyFactoryBean.class);

        // 向容器注入Bean并刷新上下文
        context.registerBeanDefinition("printClassBean", beanDefinition);
        context.refresh();

        // 从容器中获取代理proxyBean
        PrintClass proxyBean = (PrintClass) context.getBean(PrintClass.class.getCanonicalName());

        // 执行代理Bean的方法
        proxyBean.print();
    }
}

class ProxyFactoryBean<T> implements FactoryBean<T> {

    // 被代理的类className
    private String proxyClassName;

    public void setProxyClassName(String proxyClassName) {
        this.proxyClassName = proxyClassName;
    }

    // 返回代理类
    public T getObject() throws Exception {
        Class innerClass = Class.forName(proxyClassName);
        // 如果是接口则返回JDK动态代理
        if (innerClass.isInterface()) {
            return (T) InterfaceProxy.newInstance(innerClass);
        } else {
            // 返回基于CGLIB的动态代理
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(innerClass);
            enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
            enhancer.setCallback(new MethodInterceptorImpl());
            return (T) enhancer.create();
        }
    }

    // 返回实际的被代理对象类型，当调用getBean(xxx.class)时进行类型匹配
    public Class<?> getObjectType() {
        try {
            return Class.forName(proxyClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 默认是单例
    public boolean isSingleton() {
        return true;
    }
}


class InterfaceProxy implements InvocationHandler {

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("ObjectProxy execute:" + method.getName());
        return method.invoke(proxy, args);
    }

    public static <T> T newInstance(Class<T> innerInterface) {
        ClassLoader classLoader = innerInterface.getClassLoader();
        Class[] interfaces = new Class[]{innerInterface};
        InterfaceProxy proxy = new InterfaceProxy();
        return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);
    }

}

class MethodInterceptorImpl implements MethodInterceptor {

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws
            Throwable {
        System.out.println("MethodInterceptorImpl:" + method.getName());
        return methodProxy.invokeSuper(o, objects);
    }

}


class PrintClass {

    public void print() {
        System.out.println("PrintClass");
    }
}
