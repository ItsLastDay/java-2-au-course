package ru.spbau.mit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Usage:
 * 1. Run "Build - Build Artifacts - All artifacts" to regenerate .jar-files
 *    NB. You will have to rebuild artifacts each time you change something
 *    in 'application'-module or 'library'-module
 *
 * 2. Run "Main" from *this* module (not from the "application"-module!)
 *
 */
public class Main {
    // Note how setting this flag to 'false' fixes exception
    private static final boolean shouldCheckLocalFirst = true;

    private static final Path artifactsDir = Paths.get("out/artifacts");
    private static final Path applicationJar = artifactsDir.resolve("application.jar");
    private static final Path libraryJar = artifactsDir.resolve("library.jar");

    public static void main(String[] args) {
        try {
            assert libraryJar.toFile().exists() : "library.jar wasn't found at " + libraryJar.toAbsolutePath() +
                    " Did you forget to build project artifacts?";
            assert applicationJar.toFile().exists() : "application.jar wasn't found at " + applicationJar.toAbsolutePath() +
                    " Did you forget to build project artifacts?";

            // Create child of the system classloader, which will search in 'library.jar'
            ConfigurableClassLoader libraryLoader = new ConfigurableClassLoader(
                    new URL[]{libraryJar.toUri().toURL()}, shouldCheckLocalFirst
            );

            // Create child of the 'libraryLoader', which will search in 'application.jar'
            ConfigurableClassLoader applicationLoader = new ConfigurableClassLoader(
                    new URL[]{applicationJar.toUri().toURL()}, libraryLoader, shouldCheckLocalFirst
            );

            Class<?> entryPoint = applicationLoader.loadClass("ru.spbau.mit.application.Application");
            Method main = entryPoint.getMethod("main", String[].class);
            main.invoke(null, (Object) args);
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        } catch (MalformedURLException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
