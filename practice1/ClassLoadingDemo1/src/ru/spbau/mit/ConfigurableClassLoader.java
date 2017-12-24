package ru.spbau.mit;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.function.Function;

public class ConfigurableClassLoader extends URLClassLoader {
    private final boolean shouldCheckLocalFirst;

    public ConfigurableClassLoader(URL[] urls, ClassLoader parent, boolean shouldCheckLocalFirst) {
        super(urls, parent);
        this.shouldCheckLocalFirst = shouldCheckLocalFirst;
    }

    public ConfigurableClassLoader(URL[] urls, boolean shouldCheckLocalFirst) {
        super(urls);
        this.shouldCheckLocalFirst = shouldCheckLocalFirst;
    }

    public ConfigurableClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory, boolean shouldCheckLocalFirst) {
        super(urls, parent, factory);
        this.shouldCheckLocalFirst = shouldCheckLocalFirst;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // First, check if the class has already been loaded
        Class<?> alreadyLoaded = findLoadedClass(name);
        if (alreadyLoaded != null) {
            return alreadyLoaded;
        }

        // Decide in which order parent and local classes should be checked
        Function<String, Class<?>> firstMethodToInvoke;
        Function<String, Class<?>> secondMethodToInvoke;
        if (shouldCheckLocalFirst) {
            firstMethodToInvoke = this::tryFindLocally;
            secondMethodToInvoke = this::tryFindInParent;
        } else {
            firstMethodToInvoke = this::tryFindInParent;
            secondMethodToInvoke = this::tryFindLocally;
        }

        // Check in the place with higher priority
        Class<?> result = firstMethodToInvoke.apply(name);
        if (result != null) {
            return result;
        }

        // If haven't found, check in the second place
        result = secondMethodToInvoke.apply(name);

        // If still haven't found anything, throw
        if (result == null) {
            throw new ClassNotFoundException(name);
        }

        // Otherwise, return result
        return result;
    }

    /**
     * Tries to find class with given FQN in parent.
     * Returns resulting class, if found, or null otherwise.
     * Returned class is resolved.
     */
    private Class<?> tryFindInParent(String name) {
        try {
            Class<?> loadedByParent = getParent().loadClass(name);
            if (loadedByParent != null) {
                return loadedByParent;
            }
        } catch (ClassNotFoundException ignored) { }
        return null;
    }

    /**
     * Tries to find class with given FQN in local URLs.
     * Returns resulting class, if found, or null otherwise.
     * Returned class is resolved.
     */
    private Class<?> tryFindLocally(String name) {
        try {
            Class<?> providedLocally = findClass(name);
            if (providedLocally != null) {
                return providedLocally;
            }
        } catch (ClassNotFoundException ignored) { }
        return null;
    }
}
