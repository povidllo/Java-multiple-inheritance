package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RootInterface
interface TestSkip {
    void action();
}

@Supers({})
class TestSkipA extends TestSkipRootClass {

    public void action() {
        CallLog.log.add("A");
        nextAction();
    }
}

@Supers({TestSkipA.class})
class TestSkipB extends TestSkipRootClass {

}

@Supers({TestSkipB.class})
class TestSkipC extends TestSkipRootClass {

}

@Supers({TestSkipC.class})
class TestSkipD extends TestSkipRootClass {

    public void action() {
        CallLog.log.add("D");

        nextAction();
    }
}

public class SkipOverrideMethodTest {
    @org.junit.jupiter.api.Test
    public void testSkipOverrideMethod() {
        CallLog.clear();

        TestSkipD test = new TestSkipD();
        test.action();

        assertEquals(
                List.of(
                        "D", "A"
                ),
                CallLog.log
        );
    }
}
