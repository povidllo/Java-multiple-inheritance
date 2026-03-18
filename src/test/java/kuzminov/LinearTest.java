package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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

@Supers({Colored.class})
class Fancy extends ShapeRootClass {
    public Fancy() {}

    public void draw() {
        System.out.println("Fancy");
        nextDraw();
    }
}

public class LinearTest {

    @Test
    public void testLinearInheritance() {
        Fancy fancy = new Fancy();
        fancy.draw();
        var mro = ShapeHierarchy.getMRO(Fancy.class);
        assertNotNull(mro);
        assertEquals(3, mro.size());
        assertEquals(Fancy.class, mro.get(0));
        assertEquals(Colored.class, mro.get(1));
        assertEquals(Base.class, mro.get(2));
    }
}