package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;

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