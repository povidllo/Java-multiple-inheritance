package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RootInterface
interface Shape {
    void draw();
}

@Supers({})
class Base extends ShapeRootClass {
    public Base() {
    }

    public void draw() {
        CallLog.log.add("Base");
        nextDraw();
    }
}

@Supers({Base.class})
class Colored extends ShapeRootClass {
    public Colored() {
    }

    public void draw() {
        CallLog.log.add("Colored");
        nextDraw();
    }
}

@Supers({Colored.class})
class Fancy extends ShapeRootClass {
    public Fancy() {
    }

    public void draw() {
        CallLog.log.add("Fancy");
        nextDraw();
    }
}

public class LinearTest {

    @Test
    public void testLinearInheritance() {
        var mro = ShapeHierarchy.getMRO(Fancy.class);
        assertEquals(List.of(
                Fancy.class, Colored.class, Base.class
        ), mro);
    }

    @Test
    public void testNextChainOrder() {
        CallLog.clear();

        Fancy fancy = new Fancy();
        fancy.draw();

        assertEquals(
                List.of(
                        "Fancy", "Colored", "Base"
                ),
                CallLog.log
        );
    }
}