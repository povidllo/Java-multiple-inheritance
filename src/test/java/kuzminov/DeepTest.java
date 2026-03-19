package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RootInterface
interface Deep {
    void action();
}

@Supers({})
class O4 extends DeepRootClass {
    public O4() {
    }

    public void action() {
        CallLog.log.add("O4");
        nextAction();
    }
}

@Supers({O4.class})
class A4 extends DeepRootClass {
    public A4() {
    }

    public void action() {
        CallLog.log.add("A4");
        nextAction();
    }
}

@Supers({A4.class})
class B4 extends DeepRootClass {
    public B4() {
    }

    public void action() {
        CallLog.log.add("B4");
        nextAction();
    }
}

@Supers({B4.class})
class C4 extends DeepRootClass {
    public C4() {
    }

    public void action() {
        CallLog.log.add("C4");
        nextAction();
    }
}

@Supers({A4.class})
class D4 extends DeepRootClass {
    public D4() {
    }

    public void action() {
        CallLog.log.add("D4");
        nextAction();
    }
}

@Supers({C4.class, D4.class})
class E4 extends DeepRootClass {
    public E4() {
    }

    public void action() {
        CallLog.log.add("E4");
        nextAction();
    }
}

public class DeepTest {
    @Test
    public void testDeepHierarchy() {
        var mro = DeepHierarchy.getMRO(E4.class);
        assertEquals(6, mro.size());
        assertEquals(List.of(
                E4.class, C4.class, B4.class, D4.class, A4.class, O4.class
        ), mro);
    }

    @Test
    public void testNextChainOrder() {
        CallLog.clear();

        E4 test = new E4();
        test.action();

        assertEquals(
                List.of(
                        "E4", "C4", "B4", "D4", "A4", "O4"
                ),
                CallLog.log
        );
    }


}
