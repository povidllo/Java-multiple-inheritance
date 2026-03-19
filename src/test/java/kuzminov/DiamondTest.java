package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RootInterface
interface DiamondInterface {
    void doSomething();
}

@Supers({})
class Bottom extends DiamondInterfaceRootClass {
    public Bottom() {
    }

    public void doSomething() {
        CallLog.log.add("Bottom");
        nextDoSomething();
    }
}

@Supers({Bottom.class})
class Left extends DiamondInterfaceRootClass {
    public Left() {
    }

    public void doSomething() {
        CallLog.log.add("Left");
        nextDoSomething();
    }
}

@Supers({Bottom.class})
class Right extends DiamondInterfaceRootClass {
    public Right() {
    }

    public void doSomething() {
        CallLog.log.add("Right");
        nextDoSomething();
    }
}

@Supers({Left.class, Right.class})
class Upper extends DiamondInterfaceRootClass {
    public Upper() {
    }

    public void doSomething() {
        CallLog.log.add("Upper");
        nextDoSomething();
    }
}

@Supers({Right.class, Left.class})
class UpperReverseParents extends DiamondInterfaceRootClass {
    public UpperReverseParents() {
    }

    public void doSomething() {
        CallLog.log.add("UpperReverseParents");
        nextDoSomething();
    }
}

public class DiamondTest {

    @Test
    public void testDiamondInheritance() {
        DiamondInterface upper = new Upper();
        upper.doSomething();
        var mro = DiamondInterfaceHierarchy.getMRO(Upper.class);
        assertEquals(List.of(
                Upper.class, Left.class, Right.class, Bottom.class
        ), mro);
    }

    @Test
    public void testDiamondReverseInheritance() {
        var mro = DiamondInterfaceHierarchy.getMRO(UpperReverseParents.class);
        assertEquals(List.of(
                UpperReverseParents.class, Right.class, Left.class, Bottom.class
        ), mro);
    }

    @Test
    public void testNextChainOrder() {
        CallLog.clear();

        Upper test = new Upper();
        test.doSomething();

        assertEquals(
                List.of(
                        "Upper", "Left", "Right", "Bottom"
                ),
                CallLog.log
        );
    }
}