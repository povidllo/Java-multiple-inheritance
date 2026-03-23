package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RootInterface
interface ThirdPartyNextMethod {
    void action();

    void testAction();
}

@Supers({})
class ThirdPartyNextMethodA extends ThirdPartyNextMethodRootClass {

    public void testAction() {
        CallLog.log.add("TestMethodA");
        nextTestAction();
    }

    public void action() {
        CallLog.log.add("ThirdPartyNextMethodA");
        nextAction();
    }
}

@Supers({ThirdPartyNextMethodA.class})
class ThirdPartyNextMethodB extends ThirdPartyNextMethodRootClass {
    public void testAction() {
        CallLog.log.add("TestMethodB");
        nextTestAction();
    }

    public void action() {
        CallLog.log.add("ThirdPartyNextMethodB");
        nextAction();
    }
}

@Supers({ThirdPartyNextMethodB.class})
class ThirdPartyNextMethodC extends ThirdPartyNextMethodRootClass {
    public void testAction() {
        CallLog.log.add("TestMethodC");
        nextTestAction();
    }

    public void action() {
        CallLog.log.add("ThirdPartyNextMethodC");
        testAction();
        nextAction();
    }
}

public class ThirdPartyNextMethodTest {
    @Test
    public void testThirdPartyNextMethod() {
        CallLog.clear();

        ThirdPartyNextMethod test = new ThirdPartyNextMethodC();
        test.action();

        assertEquals(
                List.of(
                        "ThirdPartyNextMethodC", "TestMethodC", "TestMethodB", "TestMethodA", "ThirdPartyNextMethodB",
                        "ThirdPartyNextMethodA"
                ),
                CallLog.log
        );
    }
}