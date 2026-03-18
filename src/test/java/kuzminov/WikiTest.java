package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@RootInterface
interface C3Example {
    void action();
}

@Supers({})
class O extends C3ExampleRootClass {
    public O(){}
    public void action() {
        System.out.println("O");
        nextAction();
    }
}

@Supers({O.class})
class A extends C3ExampleRootClass {
    public A(){}

    public void action() {
        System.out.println("A");
        nextAction();
    }
}

@Supers({O.class})
class B extends C3ExampleRootClass {
    public B(){}

    public void action() {
        System.out.println("B");
        nextAction();
    }
}

@Supers({O.class})
class C extends C3ExampleRootClass {
    public C(){}

    public void action() {
        System.out.println("C");
        nextAction();
    }
}

@Supers({O.class})
class D extends C3ExampleRootClass {
    public D(){}

    public void action() {
        System.out.println("D");
        nextAction();
    }
}

@Supers({O.class})
class E extends C3ExampleRootClass {
    public E(){}

    public void action() {
        System.out.println("E");
        nextAction();
    }
}

@Supers({C.class, A.class, B.class})
class K1 extends C3ExampleRootClass {
    public K1(){}

    public void action() {
        System.out.println("K1");
        nextAction();
    }
}

@Supers({B.class, D.class, E.class})
class K2 extends C3ExampleRootClass {
    public K2(){}

    public void action() {
        System.out.println("K2");
        nextAction();
    }
}

@Supers({A.class, D.class})
class K3 extends C3ExampleRootClass {
    public K3(){}

    public void action() {
        System.out.println("K3");
        nextAction();
    }
}

@Supers({K1.class, K3.class, K2.class})
class Z extends C3ExampleRootClass {
    public Z(){}

    public void action() {
        System.out.println("Z");
        nextAction();
    }
}

public class WikiTest {

    @Test
    public void testC3Linearization() {
        Z z = new Z();
        z.action();
        var mro = C3ExampleHierarchy.getMRO(Z.class);
        assertNotNull(mro);
        assertEquals(10, mro.size());
        assertEquals(Z.class, mro.get(0));
        assertEquals(K1.class, mro.get(1));
        assertEquals(C.class, mro.get(2));
        assertEquals(K3.class, mro.get(3));
        assertEquals(A.class, mro.get(4));
        assertEquals(K2.class, mro.get(5));
        assertEquals(B.class, mro.get(6));
        assertEquals(D.class, mro.get(7));
        assertEquals(E.class, mro.get(8));
        assertEquals(O.class, mro.get(9));
    }
}