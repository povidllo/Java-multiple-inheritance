package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;

@RootInterface
interface Shape {
    void draw();
}

@Supers({})
class Base extends ShapeRootClass {
    public Base() {}
    public void draw() {
        System.out.println("Base");
        nextDraw();
    }

}

@Supers({Base.class})
class Colored extends ShapeRootClass {
    public Colored() {}

    public void draw() {
        System.out.println("Colored");
        nextDraw();
    }

}
@Supers({Base.class, Colored.class})
class Fancy extends ShapeRootClass {
    public Fancy() {}

    public void draw() {
        System.out.println("Fancy");
        nextDraw();
    }

}

@RootInterface
interface Test {
    void doSomething();
}

@Supers({})
class TestBase extends TestRootClass {
    public TestBase() {}
    public void doSomething() {
        System.out.println("TestBase");
        nextDoSomething();
    }

}

@Supers({TestBase.class})
class TestColored extends TestRootClass {
    public TestColored() {}

    public void doSomething() {
        System.out.println("TestColored");
        nextDoSomething();
    }

}

@Supers({TestBase.class})
class TestBordered extends TestRootClass {
    public TestBordered() {}

    public void doSomething() {
        System.out.println("TestBordered");
        nextDoSomething();
    }

}

@Supers({TestColored.class, TestBordered.class})
class TestFancy extends TestRootClass {
    public TestFancy() {}

    public void doSomething() {
        System.out.println("TestFancy");
        nextDoSomething();
    }

}


public class Main {

    public static void main(String[] args) {
        System.out.println("Shapes:\n");

        Shape s = new Fancy();

        s.draw();

        System.out.println("\nTests:\n");

        Test k = new TestFancy();
        k.doSomething();
    }

}

//написать тесты какие-нибудь на разное

//package kuzminov;
//
//import java.lang.reflect.Constructor;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//interface Shape {
//    void draw();
//}
//
//// GENERATED
//abstract class ShapeRoot implements Shape {
//    protected volatile ShapeRoot next;
//    private volatile boolean chainInitialized;
//
//    @Override
//    public void draw() {
//        nextDraw();
//    }
//
//    protected final void nextDraw() {
//        ensureChainInitialized();
//        if (next != null) {
//            next.draw();
//        }
//    }
//
//    private void ensureChainInitialized() {
//        if (!chainInitialized) {
//            synchronized (this) {
//                if (!chainInitialized) {
//                    initChain();
//                    chainInitialized = true;
//                }
//            }
//        }
//    }
//
//    protected void initChain() {
//        List<Class<?>> mro = ShapeHierarchy.getMRO(getClass());
//        if (mro == null || mro.size() <= 1) {
//            return;
//        }
//
//        ShapeRoot current = null;
//
//        for (int i = mro.size() - 1; i >= 1; i--) {
//            Class<?> cls = mro.get(i);
//            try {
//                Constructor<?> cons = cls.getConstructor();
//                ShapeRoot instance = (ShapeRoot) cons.newInstance();
//                instance.next = current;
//                instance.chainInitialized = true;
//                current = instance;
//            } catch (Exception e) {
//                throw new RuntimeException("Cannot instantiate parent " + cls.getName(), e);
//            }
//        }
//
//        this.next = current;
//    }
//}
//
//class Base extends ShapeRoot {
//
//    public Base() {
//
//    }
//
//    @Override
//    public void draw() {
//        System.out.println("Base");
//        nextDraw();
//    }
//}
//
//class Colored extends ShapeRoot {
//
//    public Colored() {
//    }
//
//    @Override
//    public void draw() {
//        System.out.println("Colored");
//        nextDraw();
//    }
//}
//
//class Bordered extends ShapeRoot {
//
//    public Bordered() {
//    }
//
//    @Override
//    public void draw() {
//        System.out.println("Bordered");
//        nextDraw();
//    }
//}
//
//class Fancy extends ShapeRoot {
//
//    public Fancy() {
//
//    }
//
//    @Override
//    public void draw() {
//        System.out.println("Fancy");
//        nextDraw();
//    }
//}
//
//// GENERATED
//class ShapeHierarchy {
//    private static final Map<Class<?>, List<Class<?>>> MROS = new HashMap<>();
//
//    static {
//        MROS.put(Base.class,    Arrays.asList(Base.class));
//        MROS.put(Colored.class, Arrays.asList(Colored.class, Base.class));
//        MROS.put(Bordered.class,Arrays.asList(Bordered.class, Base.class));
//        MROS.put(Fancy.class,   Arrays.asList(Fancy.class, Colored.class, Bordered.class, Base.class));
//    }
//
//    public static List<Class<?>> getMRO(Class<?> cls) {
//        return MROS.getOrDefault(cls, List.of());
//    }
//}
//
//public class Main {
//    public static void main(String[] args) {
//        Shape s = new Fancy();
//        s.draw();
//        // Fancy
//        // Colored
//        // Bordered
//        // Base
//    }
//}
