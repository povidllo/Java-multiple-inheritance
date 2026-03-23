package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RootInterface
interface InterfaceFieldsInterface {
    int x = 15;

    void action();
}

@Supers({})
class InterfaceFieldsA extends InterfaceFieldsInterfaceRootClass {

    public void action() {
        CallLog.log.add(String.valueOf(x));
        nextAction();
    }
}

@Supers({InterfaceFieldsA.class})
class InterfaceFieldsB extends InterfaceFieldsInterfaceRootClass {
    int x = 1;

    public void action() {
        CallLog.log.add(String.valueOf(x));
        nextAction();
    }
}

@Supers({InterfaceFieldsB.class})
class InterfaceFieldsC extends InterfaceFieldsInterfaceRootClass {

    public void action() {
        CallLog.log.add(String.valueOf(x));
        nextAction();
    }
}

public class InterfaceFieldsTests {

    @Test
    public void testNextChainOrder() {
        CallLog.clear();

        InterfaceFieldsInterface test = new InterfaceFieldsC();
        test.action();

        assertEquals(
                List.of(
                        "15", "1", "15"
                ),
                CallLog.log
        );
    }
}
