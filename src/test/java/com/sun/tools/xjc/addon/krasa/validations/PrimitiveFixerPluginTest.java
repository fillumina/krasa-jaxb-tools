package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.tools.xjc.addon.krasa.PrimitiveFixerPlugin;
import java.util.List;

/**
 *
 * @author Francesco Illuminati
 */
public class PrimitiveFixerPluginTest extends AnnotationCheckerTestHelper {

    public PrimitiveFixerPluginTest() {
        super("primitive", "a", "Primitive");
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
