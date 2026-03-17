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
@Supers({Colored.class})
class Fancy extends ShapeRootClass {
    public Fancy() {}

    public void draw() {
        System.out.println("Fancy");
        nextDraw();
    }

}
