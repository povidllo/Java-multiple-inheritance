package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RootInterface
interface C3Example {
    void action();
}

@Supers({})
class O extends C3ExampleRootClass {
    public O() {
    }

    public void action() {
        CallLog.log.add("O");
        nextAction();
    }
}

@Supers({O.class})
class A extends C3ExampleRootClass {
    public A() {
    }

    public void action() {
        CallLog.log.add("A");
        nextAction();
    }
}

@Supers({O.class})
class B extends C3ExampleRootClass {
    public B() {
    }

    public void action() {
        CallLog.log.add("B");
        nextAction();
    }
}

@Supers({O.class})
class C extends C3ExampleRootClass {
    public C() {
    }

    public void action() {
        CallLog.log.add("C");
        nextAction();
    }
}

@Supers({O.class})
class D extends C3ExampleRootClass {
    public D() {
    }

    public void action() {
        CallLog.log.add("D");
        nextAction();
    }
}

@Supers({O.class})
class E extends C3ExampleRootClass {
    public E() {
    }

    public void action() {
        CallLog.log.add("E");
        nextAction();
    }
}

@Supers({C.class, A.class, B.class})
class K1 extends C3ExampleRootClass {
    public K1() {
    }

    public void action() {
        CallLog.log.add("K1");
        nextAction();
    }
}

@Supers({B.class, D.class, E.class})
class K2 extends C3ExampleRootClass {
    public K2() {
    }

    public void action() {
        CallLog.log.add("K2");
        nextAction();
    }
}

@Supers({A.class, D.class})
class K3 extends C3ExampleRootClass {
    public K3() {
    }

    public void action() {
        CallLog.log.add("K3");
        nextAction();
    }
}

@Supers({K1.class, K3.class, K2.class})
class Z extends C3ExampleRootClass {
    public Z() {
    }

    public void action() {
        CallLog.log.add("Z");
        nextAction();
    }
}

public class WikiTest {

    @Test
    public void testC3Linearization() {
        var mro = C3ExampleHierarchy.getMRO(Z.class);

        assertNotNull(mro);

        assertEquals(
                List.of(
                        Z.class, K1.class, C.class, K3.class, A.class, K2.class,
                        B.class, D.class, E.class, O.class
                ),
                mro
        );
    }

    @Test
    public void testNextChainOrder() {
        CallLog.clear();

        Z z = new Z();
        z.action();

        assertEquals(
                List.of(
                        "Z", "K1", "C", "K3", "A", "K2", "B", "D", "E", "O"
                ),
                CallLog.log
        );
    }
}