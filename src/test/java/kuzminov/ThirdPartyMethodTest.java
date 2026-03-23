package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RootInterface
interface ThirdPartyMethod {
    void action();
}

@Supers({})
class ThirdPartyMethodA extends ThirdPartyMethodRootClass {

    public void methodA() {
        CallLog.log.add("MethodA");
    }

    public void action() {
        CallLog.log.add("ThirdPartyMethodA");
        methodA();
        nextAction();
    }
}

@Supers({ThirdPartyMethodA.class})
class ThirdPartyMethodB extends ThirdPartyMethodRootClass {

    public void methodB() {
        CallLog.log.add("MethodB");
    }

    public void action() {
        CallLog.log.add("ThirdPartyMethodB");
        methodB();
        nextAction();
    }
}

@Supers({ThirdPartyMethodB.class})
class ThirdPartyMethodC extends ThirdPartyMethodRootClass {

    public void action() {
        CallLog.log.add("ThirdPartyMethodC");
        nextAction();
    }
}


public class ThirdPartyMethodTest {
    @Test
    public void testThirdPartyMethod() {
        CallLog.clear();

        ThirdPartyMethod test = new ThirdPartyMethodC();
        test.action();

        assertEquals(
                List.of(
                        "ThirdPartyMethodC", "ThirdPartyMethodB", "MethodB",
                        "ThirdPartyMethodA", "MethodA"
                ),
                CallLog.log
        );
    }
}
