package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RootInterface
interface ZigZag {
    void action();
}

@Supers({})
class O2 extends ZigZagRootClass {
    public O2() {
    }

    public void action() {
        CallLog.log.add("O2");
        nextAction();
    }
}

@Supers({O2.class})
class A2 extends ZigZagRootClass {
    public A2() {
    }

    public void action() {
        CallLog.log.add("A2");
        nextAction();
    }
}

@Supers({O2.class})
class B2 extends ZigZagRootClass {
    public B2() {
    }

    public void action() {
        CallLog.log.add("B2");
        nextAction();
    }
}

@Supers({A2.class})
class C2 extends ZigZagRootClass {
    public C2() {
    }

    public void action() {
        CallLog.log.add("C2");
        nextAction();
    }
}

@Supers({B2.class})
class D2 extends ZigZagRootClass {
    public D2() {
    }

    public void action() {
        CallLog.log.add("D2");
        nextAction();
    }
}

@Supers({C2.class, D2.class})
class E2 extends ZigZagRootClass {
    public E2() {
    }

    public void action() {
        CallLog.log.add("E2");
        nextAction();
    }
}

public class ZigZagTest {

    @Test
    public void testZigZagInheritance() {
        var mro = ZigZagHierarchy.getMRO(E2.class);
        assertEquals(6, mro.size());
        assertEquals(List.of(
                E2.class, C2.class, A2.class, D2.class, B2.class, O2.class
        ), mro);
    }

    @Test
    public void testNextChainOrder() {
        CallLog.clear();

        E2 test = new E2();
        test.action();

        assertEquals(
                List.of(
                        "E2", "C2", "A2", "D2", "B2", "O2"
                ),
                CallLog.log
        );
    }
}
