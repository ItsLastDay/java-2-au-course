package ru.spbau.mit.application;

import ru.spbau.mit.library.HelloWorldPrinter;

/**
 * Created by dsavvinov on 06.09.2017.
 */
public class Application {
    public static void main(String[] args) {
        HelloWorldPrinter helloWorldPrinter = new HelloWorldPrinter();
        helloWorldPrinter.printHelloWorld();
    }
}
