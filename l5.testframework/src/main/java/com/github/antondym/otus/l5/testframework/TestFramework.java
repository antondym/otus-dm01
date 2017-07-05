package com.github.antondym.otus.l5.testframework;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

public class TestFramework {
    private final static Logger LOGGER = Logger.getLogger(TestFramework.class.getName());

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
                Method[] beforeMethods = findMethods(testClass, Before.class);
                Method[] afterMethods = findMethods(testClass, After.class);

                if (beforeMethods.length > 1)
                    throw new InvalidTestClassException("More than one @Before method in class " + testClass.getName());
                if (afterMethods.length > 1)
                    throw new InvalidTestClassException("More than one @After method in class " + testClass.getName());

                Method beforeMethod = beforeMethods.length > 0 ? beforeMethods[0] : null;
                Method afterMethod = afterMethods.length > 0 ? afterMethods[0] : null;

                if (beforeMethod != null && beforeMethod.getParameterCount() > 0)
                    throw new InvalidTestClassException("@Before method " + beforeMethod.getName() + " should not have parameters");
                if (afterMethod != null && afterMethod.getParameterCount() > 0)
                    throw new InvalidTestClassException("@After method " + afterMethod.getName() + " should not have parameters");

                Method[] methods = testClass.getMethods();
                Arrays.sort(methods, Comparator.comparing(Method::getName));

                for (Method method : methods) {
                    if (method.getAnnotation(Test.class) != null) {
                        Object instance = testClass.newInstance();

                        if (method.getParameterCount() > 0)
                            throw new InvalidTestClassException("@Test method " + method.getName() + " should not have parameters");

                        if (beforeMethod != null)
                            beforeMethod.invoke(instance);
                        method.invoke(instance);
                        if (afterMethod != null)
                            afterMethod.invoke(instance);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Method[] findMethods(Class<?> klass, Class<? extends Annotation> annotation) {
        List<Method> foundMethods = new ArrayList<>();
        for (Method method : klass.getMethods()) {
            if (method.getAnnotationsByType(annotation).length > 0) {
                foundMethods.add(method);
            }
        }
        return foundMethods.toArray(new Method[]{});
    }
}
