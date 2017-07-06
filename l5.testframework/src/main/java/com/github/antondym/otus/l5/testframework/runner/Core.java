package com.github.antondym.otus.l5.testframework.runner;

import com.github.antondym.otus.l5.testframework.After;
import com.github.antondym.otus.l5.testframework.Before;
import com.github.antondym.otus.l5.testframework.InvalidTestClassException;
import com.github.antondym.otus.l5.testframework.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

public class Core {
    private final static Logger LOGGER = Logger.getLogger(Core.class.getName());

    public static void run(String packagePrefix) {
        assert packagePrefix != null;

        Reflections reflections = new Reflections(packagePrefix, new SubTypesScanner(false));

        String[] allTypes = reflections.getAllTypes().toArray(new String[] {});
        Arrays.sort(allTypes);

        List<Class<?>> testClasses = new ArrayList<>();

        for (String type : allTypes) {
            try {
                Class<?> aClass = Class.forName(type);
                testClasses.add(aClass);
            } catch (ClassNotFoundException e) {
                LOGGER.warning("Could not load class for " + type);
            }
        }

        run(testClasses.toArray(new Class[] {}));
    }

    public static void run(Class<?>... testClasses) {
        try {
            for (Class<?> testClass : testClasses) {
                try {
                    Method beforeMethod = findOnlyTestClassMethod(testClass, Before.class);
                    Method afterMethod = findOnlyTestClassMethod(testClass, After.class);

                    Method[] methods = testClass.getMethods();
                    Arrays.sort(methods, Comparator.comparing(Method::getName));

                    for (Method method : methods) {
                        if (method.getAnnotation(Test.class) != null) {
                            Object instance = testClass.newInstance();

                            if (method.getParameterCount() > 0)
                                throw new InvalidTestClassException("@Test method \"" + method.getName() + "\" should not have parameters");

                            if (beforeMethod != null)
                                beforeMethod.invoke(instance);
                            method.invoke(instance);
                            if (afterMethod != null)
                                afterMethod.invoke(instance);
                        }
                    }
                }
                catch (InvalidTestClassException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Method findOnlyTestClassMethod(Class<?> testClass, Class<? extends Annotation> annotation) throws InvalidTestClassException {
        Method[] methods = findMethods(testClass, annotation);

        if (methods.length > 1)
            throw new InvalidTestClassException("More than one @" + annotation.getSimpleName() + " method in class " + testClass.getName());

        Method method = methods.length > 0 ? methods[0] : null;

        if (method != null && method.getParameterCount() > 0)
            throw new InvalidTestClassException("@" + annotation.getSimpleName() + " method " + testClass.getName() + "." + method.getName() + " should not have parameters");

        return method;
    }

    private static Method[] findMethods(Class<?> aClass, Class<? extends Annotation> annotation) {
        List<Method> foundMethods = new ArrayList<>();
        for (Method method : aClass.getMethods()) {
            if (method.getAnnotationsByType(annotation).length > 0) {
                foundMethods.add(method);
            }
        }
        return foundMethods.toArray(new Method[]{});
    }
}
