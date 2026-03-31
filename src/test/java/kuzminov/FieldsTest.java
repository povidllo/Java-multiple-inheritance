package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RootInterface
interface FieldsInterface {
    void action();
}

@Supers({})
class FieldsA extends FieldsInterfaceRootClass {
    String x = "1";

    public FieldsA() {
    }

    public void action() {
        CallLog.log.add(x);
        nextAction();
    }
}

@Supers({FieldsA.class})
class FieldsB extends FieldsInterfaceRootClass {

    String x = "2";

    public FieldsB() {
    }

    public void action() {
        CallLog.log.add(x);
        nextAction();
    }
}


public class FieldsTest {
    @Test
    public void testFields() {
        CallLog.clear();

        FieldsB test = new FieldsB();
        test.action();

        assertEquals(
                List.of(
                        "2", "1"
                ),
                CallLog.log
        );
    }

}
