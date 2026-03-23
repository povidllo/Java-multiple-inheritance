package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;

@RootInterface
interface MainInt {
    void doSomething();
}

@Supers({})
class MainB extends MainIntRootClass {
    public MainB() {
    }

    public void doSomething() {
        System.out.println("B");

        nextDoSomething();
    }
}

@Supers({MainB.class})
class MainL extends MainIntRootClass {
//    public MainL() {
//    }

    public void doSomething() {
        System.out.println("L");

        nextDoSomething();
    }
}

@Supers({MainB.class})
class MainR extends MainIntRootClass {
    public MainR() {
    }

    public void doSomething() {
        System.out.println("R");

        nextDoSomething();
    }
}

@Supers({MainL.class, MainR.class})
class MainU extends MainIntRootClass {
//    public MainU() {
//    }

    public void doSomething() {
        System.out.println("U");
        nextDoSomething();
    }
}

public class Main {
    public static void main(String[] args) {
        MainInt l = new MainL();
        MainInt a = new MainU();
        a.doSomething();
        System.out.println("\n\n");
        l.doSomething();
    }
}
