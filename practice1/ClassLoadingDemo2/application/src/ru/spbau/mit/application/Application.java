package ru.spbau.mit.application;

import ru.spbau.mit.library.UtilsFactory;
import ru.spbau.mit.util.Util;

/**
 * Created by dsavvinov on 06.09.2017.
 */
public class Application {
    public static void main(String[] args) {
        Util util = (Util) UtilsFactory.createUtil();
    }
}
