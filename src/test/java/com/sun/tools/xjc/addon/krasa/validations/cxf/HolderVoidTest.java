package com.sun.tools.xjc.addon.krasa.validations.cxf;

import com.sun.tools.xjc.addon.krasa.validations.PathUtil;
import com.sun.tools.xjc.addon.krasa.validations.ValidSEIGeneratorTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Checks the generated interface, including holders shared by both directions. */
public class HolderVoidTest {

    @Test
    public void holdersAndVoidReturnsInBothLibraries() throws Exception {
        for (ValidationsAnnotation library : ValidationsAnnotation.values()) {
            for (String direction : new String[] {"in", "out", "inout"}) {
                String folder = "holder-void-" + library.name() + "-" + direction;
                new ValidSEIGeneratorTestHelper(library, direction, "/holder-void.wsdl", "krasa", folder);
                String source = new String(Files.readAllBytes(Paths.get(
                        PathUtil.getAbsolutePathOfGeneratedTestSourcesDirectory(), folder,
                        "com/example/weather/WeatherServicePortType.java")), StandardCharsets.UTF_8);
                String update = declaration(source, "updateCity(");
                String notify = declaration(source, "notifyCity(");
                String lookup = declaration(source, "lookupCity(");
                String weather = declaration(source, "getWeather(");
                String namespace = library.name().toLowerCase();
                assertTrue(source, source.contains("import " + namespace + ".validation.Valid;"));
                assertTrue(source, !source.contains("import " + otherNamespace(namespace) + ".validation.Valid;"));
                assertTrue(weather, weather.contains("public java.lang.String getWeather("));
                assertEquals(weather, "inout".equals(direction) ? 2 : 1, count(weather, "@Valid"));
                assertEquals(weather, "in".equals(direction) ? 0 : 1,
                        count(weather.substring(0, weather.indexOf("getWeather(")), "@Valid"));
                assertTrue(lookup, lookup.contains("public void lookupCity("));
                assertEquals(lookup, "inout".equals(direction) ? 3 : "out".equals(direction) ? 2 : 1,
                        count(lookup, "@Valid"));
                assertEquals(lookup, 2, count(lookup, "WebParam.Mode.OUT"));
                String input = lookup.substring(lookup.indexOf("lookupCity("), lookup.indexOf("java.lang.String lookupCity,"));
                String outputWeather = lookup.substring(lookup.indexOf("java.lang.String lookupCity,"),
                        lookup.indexOf("Holder<java.lang.String> weather,"));
                String outputNotice = lookup.substring(lookup.indexOf("Holder<java.lang.String> weather,"),
                        lookup.indexOf("Holder<java.lang.String> notice"));
                assertEquals(input, "out".equals(direction) ? 0 : 1, count(input, "@Valid"));
                assertEquals(outputWeather, "in".equals(direction) ? 0 : 1, count(outputWeather, "@Valid"));
                assertEquals(outputNotice, "in".equals(direction) ? 0 : 1, count(outputNotice, "@Valid"));
                assertEquals(lookup, 0, count(lookup.substring(0, lookup.indexOf("lookupCity(")), "@Valid"));
                assertTrue(update, update.contains("WebParam.Mode.INOUT"));
                assertTrue(update, update.contains("public void updateCity("));
                assertEquals(update, 1, count(update, "WebParam.Mode.INOUT"));
                assertTrue(notify, notify.contains("public void notifyCity("));
                assertEquals(update, 1, count(update, "@Valid"));
                assertEquals(notify, "out".equals(direction) ? 0 : 1, count(notify, "@Valid"));
                assertEquals(update, 0, count(update.substring(0, update.indexOf("updateCity(")), "@Valid"));
                assertEquals(notify, 0, count(notify.substring(0, notify.indexOf("notifyCity(")), "@Valid"));
            }
        }
    }

    private static String otherNamespace(String namespace) {
        return "javax".equals(namespace) ? "jakarta" : "javax";
    }

    private static String declaration(String source, String name) {
        int start = source.indexOf(name);
        assertTrue(source, start >= 0);
        return source.substring(source.lastIndexOf("@WebMethod", start), source.indexOf(");", start));
    }

    private static int count(String source, String token) {
        return (source.length() - source.replace(token, "").length()) / token.length();
    }
}
