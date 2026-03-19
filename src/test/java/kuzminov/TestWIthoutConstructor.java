package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@RootInterface
interface Test {
    void action();
}

@Supers({})
class TestA extends TestRootClass {

    public void action() {
        nextAction();
    }
}

@Supers({TestA.class})
class TestB extends TestRootClass {

    public void action() {
        nextAction();
    }
}

public class TestWIthoutConstructor {
    @org.junit.jupiter.api.Test
    public void testLinearInheritance() {

        assertDoesNotThrow(() -> {
            (new TestB()).action();
        });
    }
}
