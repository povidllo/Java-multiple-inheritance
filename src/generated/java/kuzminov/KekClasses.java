package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;

@RootInterface
interface kek {
    void makeSomething();
}

@Supers({})
class KekBase extends kekRootClass {
    public KekBase() {}

    @Override
    public void makeSomething() {
        System.out.println("KekBase");
        nextMakeSomething();
    }
}

@Supers({KekBase.class})
class KekExtended extends kekRootClass {
    public KekExtended() {}

    @Override
    public void makeSomething() {
        System.out.println("KekExtended");
        nextMakeSomething();
    }
}