package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@RootInterface
interface DiamondInterface {
    void doSomething();
}

@Supers({})
class Bottom extends DiamondInterfaceRootClass {
    public Bottom() {}
    public void doSomething() {
        System.out.println("Bottom");
        nextDoSomething();
    }
}

@Supers({Bottom.class})
class Left extends DiamondInterfaceRootClass {
    public Left() {}
    public void doSomething() {
        System.out.println("Left");
        nextDoSomething();
    }
}

@Supers({Bottom.class})
class Right extends DiamondInterfaceRootClass {
    public Right() {}
    public void doSomething() {
        System.out.println("Right");
        nextDoSomething();
    }
}

@Supers({Left.class, Right.class})
class Upper extends DiamondInterfaceRootClass {
    public Upper() {}
    public void doSomething() {
        System.out.println("Upper");
        nextDoSomething();
    }
}

@Supers({Right.class, Left.class})
class UpperReverseParents extends DiamondInterfaceRootClass {
    public UpperReverseParents() {}
    public void doSomething() {
        System.out.println("UpperReverseParents");
        nextDoSomething();
    }
}

public class DiamondTest {

    @Test
    public void testDiamondInheritance() {
        DiamondInterface upper = new Upper();
        upper.doSomething();
        var mro = DiamondInterfaceHierarchy.getMRO(Upper.class);
        assertNotNull(mro);
        assertEquals(Upper.class, mro.get(0));
        assertEquals(4, mro.size());
        assertEquals(Upper.class, mro.get(0));
        assertEquals(Left.class, mro.get(1));
        assertEquals(Right.class, mro.get(2));
        assertEquals(Bottom.class, mro.get(3));
    }
    @Test
    public void testDiamondReverseInheritance() {
        DiamondInterface upper = new UpperReverseParents();
        upper.doSomething();
        var mro = DiamondInterfaceHierarchy.getMRO(UpperReverseParents.class);
        assertNotNull(mro);
        assertEquals(4, mro.size());
        assertEquals(UpperReverseParents.class, mro.get(0));
        assertEquals(Left.class, mro.get(2));
        assertEquals(Right.class, mro.get(1));
        assertEquals(Bottom.class, mro.get(3));
    }
}