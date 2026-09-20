package com.sun.tools.xjc.addon.krasa.validations;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import junit.framework.TestCase;
import org.junit.Test;
import static org.junit.Assert.fail;

/**
 * Fails when a JUnit4 {@code @Test} method hides in a JUnit3 test class.
 *
 * <p>
 * The harness classes extend {@link RunXJC2MojoTestHelper}, whose parent
 * {@code org.jvnet.jaxb2.maven2.test.RunXJC2Mojo} is a {@code TestCase}.
 * Surefire runs a {@code TestCase} with {@code JUnit38ClassRunner}, which
 * collects the methods named {@code test*} and ignores the annotations: in
 * such a class a {@code @Test} annotation is silently discarded and the
 * method never executes, however convincing the assertion in it may look.
 * The suite then stays green while the assertion does not run, which is
 * worse than a missing test, because nothing in the report says so.
 *
 * <p>
 * The compiled test classes are read rather than a list of names, so a new
 * harness class, or a new {@code @Test} method added to an existing one, is
 * covered without touching this file. A guard that cannot find the classes
 * fails instead of passing: an empty check is no check.
 *
 * <p>
 * The remedy is to name the method {@code test...}, which is what the
 * JUnit3 collection looks for, or to move the class off {@code TestCase},
 * which is what makes {@code @Test} work.
 *
 * @author Francesco Illuminati
 */
public class HarnessJUnit4MethodsCheckTest {

    /** The package of this project, where the {@code @Test} rule is checked. */
    private static final String PROJECT_PACKAGE = "com.sun.tools.xjc.addon.krasa";

    private static final String CLASS_SUFFIX = ".class";

    @Test
    public void noJUnit4TestMethodMustHideInAJUnit3TestClass() throws Exception {
        Path root = testClassesRoot();
        List<Path> classFiles = classFilesUnder(root);
        if (classFiles.isEmpty()) {
            fail("no test class found under " + root + ": the check cannot have run");
        }

        List<String> deadMethods = new ArrayList<>();
        List<String> unreadable = new ArrayList<>();
        int jUnit3Classes = 0;
        for (Path classFile : classFiles) {
            String className = classNameFor(root, classFile);
            Class<?> clazz;
            try {
                clazz = load(className);
            } catch (Throwable throwable) {
                // a class that cannot be loaded cannot be checked, and a stale
                // target/test-classes is worth reporting instead of skipped
                unreadable.add(className + " (" + throwable + ")");
                continue;
            }
            if (TestCase.class.isAssignableFrom(clazz)) {
                jUnit3Classes++;
                deadMethods.addAll(deadTestMethodsOf(clazz));
            }
        }
        if (jUnit3Classes == 0) {
            fail("no JUnit3 test class found under " + root + ": the check cannot have run");
        }

        if (!unreadable.isEmpty()) {
            Collections.sort(unreadable);
            fail("cannot check " + unreadable.size() + " test class(es) under " + root + ": "
                    + unreadable);
        }

        if (!deadMethods.isEmpty()) {
            Collections.sort(deadMethods);
            fail(deadMethods.size() + " method(s) annotated with @Test are never executed, "
                    + "because surefire runs a TestCase with JUnit38ClassRunner, which "
                    + "collects the methods named 'test*' and ignores the annotations; name "
                    + "the method 'test...' or move the class off TestCase: " + deadMethods);
        }
    }

    /**
     * The methods annotated with {@code @Test} that the JUnit3 collection
     * skips, declared by the class itself or by one of its parents of this
     * project. The parents coming from a dependency are out of our control
     * and cannot be acted upon.
     */
    private List<String> deadTestMethodsOf(Class<?> clazz) {
        List<String> dead = new ArrayList<>();
        for (Class<?> c = clazz; isProjectClass(c); c = c.getSuperclass()) {
            for (Method method : c.getDeclaredMethods()) {
                // collection is by name, so a test* method runs whatever it is
                // annotated with, and only the other names are lost
                if (method.getAnnotation(Test.class) != null
                        && !method.getName().startsWith("test")) {
                    dead.add(c.getName() + "#" + method.getName());
                }
            }
        }
        return dead;
    }

    private boolean isProjectClass(Class<?> clazz) {
        return clazz != null && clazz.getName().startsWith(PROJECT_PACKAGE + ".");
    }

    /**
     * Loading without running the static initialisers: only the shape of
     * the class is inspected, so no test class is set up twice.
     */
    private Class<?> load(String className) throws ClassNotFoundException {
        return Class.forName(className, false, getClass().getClassLoader());
    }

    private List<Path> classFilesUnder(Path root) throws Exception {
        try (Stream<Path> paths = Files.walk(root)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(CLASS_SUFFIX))
                    .filter(path -> !path.getFileName().toString().contains("$"))
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    private String classNameFor(Path root, Path classFile) {
        String relativeName = root.relativize(classFile).toString();
        return relativeName
                .substring(0, relativeName.length() - CLASS_SUFFIX.length())
                .replace(File.separatorChar, '.');
    }

    /**
     * The directory holding the compiled test classes: the one this class
     * was loaded from, or the build directory when that is not a directory
     * at all (a jar, for instance).
     */
    private Path testClassesRoot() {
        CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            try {
                Path location = Paths.get(codeSource.getLocation().toURI());
                if (Files.isDirectory(location)) {
                    return location;
                }
            } catch (URISyntaxException ex) {
                // not a path: the build directory is the only thing left
            }
        }
        Path buildDirectory = Paths.get("target", "test-classes");
        if (Files.isDirectory(buildDirectory)) {
            return buildDirectory;
        }
        throw new AssertionError("cannot locate the compiled test classes: neither "
                + codeSource + " nor " + buildDirectory.toAbsolutePath() + " is a directory");
    }
}
