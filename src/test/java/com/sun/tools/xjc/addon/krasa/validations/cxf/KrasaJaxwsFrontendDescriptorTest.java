package com.sun.tools.xjc.addon.krasa.validations.cxf;

import com.sun.tools.xjc.addon.krasa.JaxbValidationsPlugin;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.cxf.tools.wsdlto.frontend.jaxws.generators.SEIGenerator;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Keeps the {@code krasa-jaxws} frontend in step with the generators the CXF frontend declares.
 *
 * <p>
 * The list is copied from CXF's own {@code META-INF/tools-plugin.xml}: a CXF upgrade that adds,
 * renames or drops a generator changes what {@code krasa-jaxws} generates, so it has to fail here
 * rather than be noticed by whoever runs the tool.
 *
 * @author Francesco Illuminati
 */
public class KrasaJaxwsFrontendDescriptorTest {

    private static final String DESCRIPTOR = "META-INF/tools-plugin.xml";
    private static final String CXF_FRONTEND = "jaxws";
    private static final String KRASA_JAXWS_FRONTEND = "krasa-jaxws";
    private static final String KRASA_FRONTEND = "krasa";
    private static final String CXF_PACKAGE =
            "org.apache.cxf.tools.wsdlto.frontend.jaxws.generators";
    private static final String KRASA_PACKAGE = "com.sun.tools.xjc.addon.krasa";

    @Test
    public void shouldKrasaJaxwsDeclareTheCxfGeneratorsAndOurs() throws Exception {
        Map<String, List<String>> cxf = generatorsOf(SEIGenerator.class, CXF_FRONTEND);
        Map<String, List<String>> ours = generatorsOf(JaxbValidationsPlugin.class,
                KRASA_JAXWS_FRONTEND);

        assertEquals("krasa-jaxws must list the generators CXF's frontend lists",
                cxf.get(CXF_PACKAGE), ours.get(CXF_PACKAGE));
        assertEquals("krasa-jaxws must add our generator and nothing else",
                Collections.singletonList("ValidSEIGenerator"), ours.get(KRASA_PACKAGE));
        assertEquals("krasa-jaxws must declare no generator block other than these two",
                new HashSet<>(Arrays.asList(CXF_PACKAGE, KRASA_PACKAGE)), ours.keySet());
    }

    @Test
    public void shouldKrasaFrontendKeepListingOurGeneratorOnly() throws Exception {
        Map<String, List<String>> ours = generatorsOf(JaxbValidationsPlugin.class, KRASA_FRONTEND);

        assertEquals("the krasa frontend generates what it always generated",
                Collections.singletonMap(KRASA_PACKAGE,
                        Collections.singletonList("ValidSEIGenerator")),
                ours);
    }

    private Map<String, List<String>> generatorsOf(Class<?> anchor, String frontend)
            throws Exception {
        Map<String, List<String>> generators = generatorsOf(parse(descriptorOf(anchor)), frontend);
        if (generators.isEmpty()) {
            fail("no frontend named " + frontend + " in the " + DESCRIPTOR + " of "
                    + anchor.getName());
        }
        return generators;
    }

    private Map<String, List<String>> generatorsOf(Document document, String frontend) {
        Map<String, List<String>> generators = new LinkedHashMap<>();
        NodeList frontends = document.getElementsByTagName("frontend");
        for (int i = 0; i < frontends.getLength(); i++) {
            Element element = (Element) frontends.item(i);
            if (!frontend.equals(element.getAttribute("name"))) {
                continue;
            }
            NodeList blocks = element.getElementsByTagName("generators");
            for (int j = 0; j < blocks.getLength(); j++) {
                Element block = (Element) blocks.item(j);
                generators.put(block.getAttribute("package"), namesOf(block));
            }
        }
        return generators;
    }

    private List<String> namesOf(Element block) {
        List<String> names = new ArrayList<>();
        NodeList listed = block.getElementsByTagName("generator");
        for (int i = 0; i < listed.getLength(); i++) {
            names.add(((Element) listed.item(i)).getAttribute("name"));
        }
        Collections.sort(names);
        return names;
    }

    private Document parse(String descriptor) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        return factory.newDocumentBuilder()
                .parse(new ByteArrayInputStream(descriptor.getBytes(StandardCharsets.UTF_8)));
    }

    private String descriptorOf(Class<?> anchor) throws Exception {
        Path location = Paths.get(anchor.getProtectionDomain().getCodeSource().getLocation().toURI());
        if (Files.isDirectory(location)) {
            return new String(Files.readAllBytes(location.resolve(DESCRIPTOR)),
                    StandardCharsets.UTF_8);
        }
        try (JarFile jar = new JarFile(location.toFile())) {
            JarEntry entry = jar.getJarEntry(DESCRIPTOR);
            if (entry == null) {
                throw new AssertionError("no " + DESCRIPTOR + " in " + jar.getName());
            }
            try (InputStream stream = jar.getInputStream(entry)) {
                return new String(readAll(stream), StandardCharsets.UTF_8);
            }
        }
    }

    private byte[] readAll(InputStream stream) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = stream.read(buffer)) != -1) {
            bytes.write(buffer, 0, read);
        }
        return bytes.toByteArray();
    }
}
