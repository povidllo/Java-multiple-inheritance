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
    public void action() { nextAction(); }
}

@Supers({O2.class})
class A2 extends ZigZagRootClass {
    public void action() { nextAction(); }
}

@Supers({O2.class})
class B2 extends ZigZagRootClass {
    public void action() { nextAction(); }
}

@Supers({A2.class})
class C2 extends ZigZagRootClass {
    public void action() { nextAction(); }
}

@Supers({B2.class})
class D2 extends ZigZagRootClass {
    public void action() { nextAction(); }
}

@Supers({C2.class, D2.class})
class E2 extends ZigZagRootClass {
    public void action() { nextAction(); }
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
}
