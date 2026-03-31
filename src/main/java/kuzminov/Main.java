package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;

@RootInterface
interface Payment {
    double process(double amount);
}

@Supers({})
class BasePayment extends PaymentRootClass {
    public double process(double amount) {
        return amount;
    }
}

@Supers({BasePayment.class})
class Fee extends PaymentRootClass {
    public double process(double amount) {
        return nextProcess(amount) - 1.0;
    }
}

@Supers({BasePayment.class})
class Tax extends PaymentRootClass {
    public double process(double amount) {
        return nextProcess(amount) * 1.2;
    }
}

@Supers({Fee.class, Tax.class})
class PaymentImpl extends PaymentRootClass {
}

public class Main {
    public static void main(String[] args) {
        PaymentImpl payment = new PaymentImpl();
        System.out.println(payment.process(32));
    }
}
