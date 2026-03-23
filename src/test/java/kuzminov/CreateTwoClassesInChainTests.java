package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RootInterface
interface TwoInt {
    void doSomething();
}

@Supers({})
class TwoA extends TwoIntRootClass {
    public TwoA() {
    }

    public void doSomething() {
        CallLog.log.add("A");

        nextDoSomething();
    }
}

@Supers({TwoA.class})
class TwoB extends TwoIntRootClass {

    public void doSomething() {
        CallLog.log.add("B");

        nextDoSomething();
    }
}

@Supers({TwoA.class})
class TwoC extends TwoIntRootClass {
    public TwoC() {
    }

    public void doSomething() {
        CallLog.log.add("C");

        nextDoSomething();
    }
}

@Supers({TwoB.class, TwoC.class})
class TwoD extends TwoIntRootClass {

    public void doSomething() {
        CallLog.log.add("D");
        nextDoSomething();
    }
}

public class CreateTwoClassesInChainTests {
    @Test
    public void testTwoClassesInChainTests() {
        CallLog.clear();

        TwoInt test = new TwoD();
        test.doSomething();

        assertEquals(
                List.of(
                        "D", "B", "C", "A"
                ),
                CallLog.log
        );

        CallLog.clear();
        TwoInt nextTest = new TwoC();
        nextTest.doSomething();
        assertEquals(
                List.of(
                        "C", "A"
                ),
                CallLog.log
        );
    }
    
    @Test
    public void testTwoClassesInChainReversedTests() {
        CallLog.clear();
        TwoInt nextTest = new TwoC();
        nextTest.doSomething();
        assertEquals(
                List.of(
                        "C", "A"
                ),
                CallLog.log
        );

        CallLog.clear();

        TwoInt test = new TwoD();
        test.doSomething();

        assertEquals(
                List.of(
                        "D", "B", "C", "A"
                ),
                CallLog.log
        );
    }
}
