package annotation.app;

import annotation.Test;
import annotation.TesterInfo;

@TesterInfo(
        priority = TesterInfo.Priority.HIGH,
        createdBy = "Kevin",
        tags = {"test", "demo"}
)
public class TestExample {

    @Test
    public void testA() {
        if (true) {
            throw new RuntimeException("This test always fails");
        }
    }

    @Test(enabled = false)
    public void testB() {
        if (false) {
            throw new RuntimeException("This test always passes");
        }
    }

    @Test(enabled = true)
    public void testC() {
        if (10 > 1) {
            // do nothing
        }
    }
}
