package annotation.reflection;

import annotation.Test;
import annotation.app.TestExample;
import annotation.TesterInfo;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class RunTest {

    public static void main(String[] args) {
        System.out.println("Testing...");
        int passed = 0;
        int failed = 0;
        int count = 0;
        int ignore = 0;
        Class<TestExample> obj = TestExample.class;

        // Process @TesterInfo
        if (obj.isAnnotationPresent(TesterInfo.class)) {
            Annotation annotation = obj.getAnnotation(TesterInfo.class);
            TesterInfo testerInfo = (TesterInfo) annotation;
            System.out.println("Priority: " + testerInfo.priority());
            System.out.println("Created By: " + testerInfo.createdBy());
            System.out.println("Tags: ");
            for (String tag : testerInfo.tags()) {
                System.out.println(tag);
            }
            System.out.println("Last Modified: " + testerInfo.lastModified());
        }

        // Process @Test
        for (Method method : obj.getDeclaredMethods()) {
            // if method is annotated with @Test
            if (method.isAnnotationPresent(Test.class)) {
                Annotation annotation = method.getAnnotation(Test.class);
                Test test = (Test) annotation;
                if (test.enabled()) {
                    try {
                        method.invoke(obj.newInstance());
                        System.out.printf("%s - Test '%s' - passed %n", ++count, method.getName());
                        passed++;
                    } catch (Exception e) {
                        System.out.printf("%s - Test '%s' - failed: %s %n", ++count, method.getName(), e.getCause());
                        failed++;
                    }
                } else {
                    System.out.printf("%s - Test '%s' - ignored%n", ++count, method.getName());
                    ignore++;
                }
            }
        }
        System.out.printf("%nResult : Total : %d, Passed: %d, Failed %d, Ignore %d%n", count, passed, failed, ignore);
    }
}
