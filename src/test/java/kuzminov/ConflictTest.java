package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RootInterface
interface Conflict {
    void action();
}

@Supers({})
class O5 extends ConflictRootClass {
    public void action() {
        nextAction();
    }
}

@Supers({O5.class})
class A5 extends ConflictRootClass {
    public void action() {
        nextAction();
    }
}

@Supers({O5.class})
class B5 extends ConflictRootClass {
    public void action() {
        nextAction();
    }
}

@Supers({A5.class, B5.class})
class C5 extends ConflictRootClass {
    public void action() {
        nextAction();
    }
}

@Supers({B5.class, A5.class})
class D5 extends ConflictRootClass {
    public void action() {
        nextAction();
    }
}

@Supers({C5.class, D5.class})
class E5 extends ConflictRootClass {
    public void action() {
        nextAction();
    }
}

public class ConflictTest {
    @Test
    public void testConflict() {
        assertThrows(IllegalStateException.class, () -> {
            ConflictHierarchy.getMRO(E5.class);
        });
    }
}
