package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RootInterface
interface TestCancel {
    void action();
}

@Supers({})
class TestCancelA extends TestCancelRootClass {

    public void action() {
        CallLog.log.add("A");
        nextAction();
    }
}

@Supers({TestCancelA.class})
class TestCancelB extends TestCancelRootClass {
    public void action() {
        CallLog.log.add("B");
        nextAction();
    }
}

@Supers({TestCancelB.class})
class TestCancelC extends TestCancelRootClass {
    public void action() {
        CallLog.log.add("C");
    }

}

@Supers({TestCancelC.class})
class TestCancelD extends TestCancelRootClass {

    public void action() {
        CallLog.log.add("D");
        nextAction();
    }
}

public class CancelNextMethodTest {
    @org.junit.jupiter.api.Test
    public void testCancelOverrideMethod() {
        CallLog.clear();

        TestCancel test = new TestCancelD();
        test.action();

        assertEquals(
                List.of(
                        "D", "C"
                ),
                CallLog.log
        );
    }
}
