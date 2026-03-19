package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RootInterface
interface Shared {
    void action();
}

@Supers({})
class O3 extends SharedRootClass {
    public O3() {
    }

    public void action() {
        CallLog.log.add("O3");
        nextAction();
    }
}

@Supers({O3.class})
class A3 extends SharedRootClass {
    public A3() {
    }

    public void action() {
        CallLog.log.add("A3");
        nextAction();
    }
}

@Supers({A3.class})
class B3 extends SharedRootClass {
    public B3() {
    }

    public void action() {
        CallLog.log.add("B3");
        nextAction();
    }
}

@Supers({A3.class})
class C3 extends SharedRootClass {
    public C3() {
    }

    public void action() {
        CallLog.log.add("C3");
        nextAction();
    }
}

@Supers({B3.class, C3.class})
class D3 extends SharedRootClass {
    public D3() {
    }

    public void action() {
        CallLog.log.add("D3");
        nextAction();
    }
}

public class SharedAncestorTest {
    @Test
    public void testSharedAncestor() {
        var mro = SharedHierarchy.getMRO(D3.class);
        assertEquals(List.of(
                D3.class, B3.class, C3.class, A3.class, O3.class
        ), mro);
    }

    @Test
    public void testNextChainOrder() {
        CallLog.clear();

        D3 test = new D3();
        test.action();

        assertEquals(
                List.of(
                        "D3", "B3", "C3", "A3", "O3"),
                CallLog.log
        );
    }
}
