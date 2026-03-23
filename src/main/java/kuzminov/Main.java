package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;

@RootInterface
interface MainInt {
    int a = 12;

    void doSomething();
}

@Supers({})
class MainB extends MainIntRootClass {
    public MainB() {
    }

    public void doSomething() {
        System.out.println(a);

        nextDoSomething();
    }
}

@Supers({MainB.class})
class MainL extends MainIntRootClass {
    //    public MainL() {
//    }
    String a = "be";

    public void doSomething() {
//        System.out.println("L");
        System.out.println(a);

        nextDoSomething();
    }
}

@Supers({MainB.class})
class MainR extends MainIntRootClass {
    public MainR() {
    }

    public void doSomething() {
//        System.out.println("R");
        System.out.println(a);

        nextDoSomething();
    }
}

@Supers({MainL.class, MainR.class})
class MainU extends MainIntRootClass {
//    public MainU() {
//    }

    public void doSomething() {
//        System.out.println("U");
        System.out.println(a);
        nextDoSomething();
    }
}

public class Main {
    public static void main(String[] args) {
        MainInt a = new MainU();
        a.doSomething();
        System.out.println("\n\n");
    }
}
