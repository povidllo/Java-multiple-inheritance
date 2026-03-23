package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RootInterface
interface ThirdPartyNextAnotherMethod {
    void action();

    void testAction();
}

@Supers({})
class ThirdPartyNextAnotherMethodA extends ThirdPartyNextAnotherMethodRootClass {

    public void testAction() {
        CallLog.log.add("TestMethodA");
        nextTestAction();
    }

    public void action() {
        CallLog.log.add("ThirdPartyNextMethodA");
        nextAction();
    }
}

@Supers({ThirdPartyNextAnotherMethodA.class})
class ThirdPartyNextAnotherMethodB extends ThirdPartyNextAnotherMethodRootClass {
    public void testAction() {
        CallLog.log.add("TestMethodB");
        nextTestAction();
    }

    public void action() {
        CallLog.log.add("ThirdPartyNextMethodB");
        nextAction();
    }
}

@Supers({ThirdPartyNextAnotherMethodB.class})
class ThirdPartyNextAnotherMethodC extends ThirdPartyNextAnotherMethodRootClass {
    public void testAction() {
        CallLog.log.add("TestMethodC");

        nextTestAction();
        nextAction();
    }

    public void action() {
        CallLog.log.add("ThirdPartyNextMethodC");
        testAction();

        nextAction();
        nextTestAction();
    }
}

public class ThirdPartyMethodCallAnotherNextMethod {
    @Test
    public void testThirdPartyNextMethod() {
        CallLog.clear();

        ThirdPartyNextAnotherMethod test = new ThirdPartyNextAnotherMethodC();
        test.action();

        assertEquals(
                List.of(
                        "ThirdPartyNextMethodC", "TestMethodC", "TestMethodB", "TestMethodA", "ThirdPartyNextMethodB",
                        "ThirdPartyNextMethodA", "ThirdPartyNextMethodB", "ThirdPartyNextMethodA",
                        "TestMethodB", "TestMethodA"
                ),
                CallLog.log
        );
        System.out.println(CallLog.log);
    }
}
