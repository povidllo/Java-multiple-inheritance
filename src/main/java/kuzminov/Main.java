package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;

public class Main {

    public static void main(String[] args) {
        System.out.println("Linear:\n");

        Shape s = new Fancy();

        s.draw();

        System.out.println("\nDiamond:\n");

        DiamondInterface k = new Upper();
        k.doSomething();
//
        System.out.println("\nWiki Test:\n");

        C3Example z = new Z();
        z.action();
    }

}

//написать тесты какие-нибудь на разное
//написать чтобы не обязательно самому создавать конструктор базовый
//в этот базовый конструктор в начале вбивается создание цепочки