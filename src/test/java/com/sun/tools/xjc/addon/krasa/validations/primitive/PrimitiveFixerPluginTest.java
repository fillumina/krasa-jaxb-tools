package com.sun.tools.xjc.addon.krasa.validations.primitive;

import com.sun.tools.xjc.addon.krasa.PrimitiveFixerPlugin;
import com.sun.tools.xjc.addon.krasa.validations.AnnotationCheckerTestHelper;
import com.sun.tools.xjc.addon.krasa.validations.ArgumentBuilder;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsArgument;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class PrimitiveFixerPluginTest extends AnnotationCheckerTestHelper {

    public PrimitiveFixerPluginTest() {
        super("primitive", "a", "Primitive");
    }

    public void testPrimitiveSubstitution() throws NoSuchFieldException {
        withElement("Primitive")
                .withField("abyte").assertClass(Byte.class).end()
                .withField("adecimal").assertClass(BigDecimal.class).end()
                .withField("aint").assertClass(Integer.class).end()
                .withField("ainteger").assertClass(BigInteger.class).end()
                .withField("along").assertClass(Long.class).end()
                .withField("anegativeInteger")
                    .assertClass(BigInteger.class)
                    .withAnnotation("DecimalMax")
                        .assertParam("value", "-1")
                        .assertParam("inclusive", true)
                    .end()
                .end()
                .withField("anonNegativeInteger")
                    .assertClass(BigInteger.class)
                    .withAnnotation("DecimalMin")
                        .assertParam("value", "0")
                        .assertParam("inclusive", true)
                    .end()
                .end()
                .withField("anonPositiveInteger")
                    .assertClass(BigInteger.class)
                    .withAnnotation("DecimalMax")
                        .assertParam("value", "0")
                        .assertParam("inclusive", true)
                    .end()
                .end()
                .withField("apositiveInteger")
                    .assertClass(BigInteger.class)
                    .withAnnotation("DecimalMin")
                        .assertParam("value", "1")
                        .assertParam("inclusive", true)
                    .end()
                .end()
                .withField("short")
                    .assertClass(Short.class)
                .end()
                .withField("aunsignedLong")
                    .assertClass(BigInteger.class)
                    .withAnnotation("DecimalMin")
                        .assertParam("value", "0")
                        .assertParam("inclusive", true)
                    .end()
                    .withAnnotation("DecimalMax")
                        .assertParam("value", "18446744073709551615")
                        .assertParam("inclusive", true)
                    .end()
                .end()
                .withField("aunsignedInt")
                    .assertClass(Long.class)
                    .withAnnotation("DecimalMin")
                        .assertParam("value", "0")
                        .assertParam("inclusive", true)
                    .end()
                    .withAnnotation("DecimalMax")
                        .assertParam("value", "4294967295")
                        .assertParam("inclusive", true)
                    .end()
                .end()
                .withField("aunsignedShort")
                    .assertClass(Integer.class)
                    .withAnnotation("DecimalMin")
                        .assertParam("value", "0")
                        .assertParam("inclusive", true)
                    .end()
                    .withAnnotation("DecimalMax")
                        .assertParam("value", "65535")
                        .assertParam("inclusive", true)
                    .end()
                .end()
                .withField("unsignedByte")
                    .assertClass(Short.class)
                    .withAnnotation("DecimalMin")
                        .assertParam("value", "0")
                        .assertParam("inclusive", true)
                    .end()
                    .withAnnotation("DecimalMax")
                        .assertParam("value", "255")
                        .assertParam("inclusive", true)
                    .end()
                .end();
    }

    @Override
    public List<String> getArgs() {
        return ArgumentBuilder.builder()
                .add("-" + PrimitiveFixerPlugin.PLUGIN_NAME)
                .add(ValidationsArgument.generateNotNullAnnotations, true)
                .add(ValidationsArgument.generateListAnnotations, true)
                .add(ValidationsArgument.targetNamespace, getNamespace())
                .add(ValidationsArgument.validationAnnotations, getAnnotation().name())
                .getOptionList();
    }

}
