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

    @Override
    public void draw() {
        System.out.println("Base");
        nextDraw();
    }
}

@Supers({Base.class})
class Colored extends ShapeRootClass {
    public Colored() {}

    @Override
    public void draw() {
        System.out.println("Colored");
        nextDraw();
    }
}

@Supers({Base.class})
class Bordered extends ShapeRootClass {
    public Bordered() {}

    @Override
    public void draw() {
        System.out.println("Bordered");
        nextDraw();
    }
}

@Supers({Colored.class, Bordered.class})
class Fancy extends ShapeRootClass {
    public Fancy() {}

    @Override
    public void draw() {
        System.out.println("Fancy");
        nextDraw();
    }
}