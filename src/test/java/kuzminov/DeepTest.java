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
class O4 extends DeepRootClass { public void action(){ nextAction(); } }

@Supers({O4.class})
class A4 extends DeepRootClass { public void action(){ nextAction(); } }

@Supers({A4.class})
class B4 extends DeepRootClass { public void action(){ nextAction(); } }

@Supers({B4.class})
class C4 extends DeepRootClass { public void action(){ nextAction(); } }

@Supers({A4.class})
class D4 extends DeepRootClass { public void action(){ nextAction(); } }

@Supers({C4.class, D4.class})
class E4 extends DeepRootClass { public void action(){ nextAction(); } }

public class DeepTest {
    @Test
    public void testDeepHierarchy() {
        var mro = DeepHierarchy.getMRO(E4.class);
        assertEquals(6, mro.size());
        assertEquals(List.of(
                E4.class, C4.class, B4.class, D4.class, A4.class, O4.class
        ), mro);
    }

}
