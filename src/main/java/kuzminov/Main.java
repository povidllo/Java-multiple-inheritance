package kuzminov;

import kuzminov.annotations.RootInterface;
import kuzminov.annotations.Supers;

@RootInterface
interface IWidget {
    void render();

    void click();
}

@Supers({})
class Clickable extends IWidgetRootClass {

    public void click() {
        System.out.println("Clickable: обработка клика");
        nextClick();
    }

    public void render() {
        System.out.println("Clickable: добавляю эффект кнопки");
        nextRender();
    }
}

@Supers({})
class Bordered extends IWidgetRootClass {

    public void render() {
        System.out.println("Bordered: рисую рамку");
        nextRender();
    }

    public void click() {
        System.out.println("Bordered: обработка клика по рамке");
        nextClick();
    }
}

@Supers({})
class Logging extends IWidgetRootClass {

    public void render() {
        System.out.println("Logging: начало отрисовки");
        nextRender();
        System.out.println("Logging: окончание отрисовки");
    }
}

@Supers({Logging.class, Clickable.class, Bordered.class})
class MyButton extends IWidgetRootClass {

    public void render() {
        System.out.println("MyButton: базовая отрисовка");
        nextRender();
    }

    public void click() {
        System.out.println("MyButton: своя логика перед кликом");
        nextClick();
    }
}

public class Main {
    public static void main(String[] args) {
        IWidgetRootClass button = new MyButton();

        System.out.println("=== render() ===");
        button.render();

        System.out.println("\n=== click() ===");
        button.click();
    }
}
