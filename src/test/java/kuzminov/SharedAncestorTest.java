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
    public void action() { nextAction(); }
}

@Supers({O3.class})
class A3 extends SharedRootClass {
    public void action() { nextAction(); }
}

@Supers({A3.class})
class B3 extends SharedRootClass {
    public void action() { nextAction(); }
}

@Supers({A3.class})
class C3 extends SharedRootClass {
    public void action() { nextAction(); }
}

@Supers({B3.class, C3.class})
class D3 extends SharedRootClass {
    public void action() { nextAction(); }
}

public class SharedAncestorTest {
    @Test
    public void testSharedAncestor() {
        var mro = SharedHierarchy.getMRO(D3.class);
        assertEquals(5, mro.size());
        assertEquals(List.of(
                D3.class, B3.class, C3.class, A3.class, O3.class
        ), mro);
    }
}
