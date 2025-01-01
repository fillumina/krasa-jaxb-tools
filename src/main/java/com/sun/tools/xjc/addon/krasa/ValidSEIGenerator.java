package com.sun.tools.xjc.addon.krasa;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.addon.krasa.validations.ProcessorForJavaModel;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsOptions;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.tools.common.ToolConstants;
import org.apache.cxf.tools.common.ToolContext;
import org.apache.cxf.tools.common.ToolException;
import org.apache.cxf.tools.common.model.JavaModel;
import org.apache.cxf.tools.wsdlto.frontend.jaxws.generators.SEIGenerator;
import org.apache.cxf.tools.wsdlto.frontend.jaxws.processor.WSDLToJavaProcessor;

import javax.validation.Valid;
import javax.xml.namespace.QName;
import java.util.Arrays;
import java.util.Map;

/**
 * Performs validation on fields annotated with @{@link Valid} annotation.
 *
 * It works with the cxf-codegen-plugin, please see the krasa-jaxb-tools-example module
 * in the krasa-jaxb-tools-example project for an example.
 *
 * @author Vojtěch Krása
 */
public class ValidSEIGenerator extends SEIGenerator {

    @Override
	public String getName() {
		return "krasa";
	}

	@Override
	public void generate(ToolContext ctx) throws ToolException {
		ValidationsOptions validationsOptions = parseArguments(ctx);

		ProcessorForJavaModel processorForJavaModel = new ProcessorForJavaModel(validationsOptions);

		Map<QName, JavaModel> map = CastUtils.cast((Map<?, ?>) ctx.get(WSDLToJavaProcessor.MODEL_MAP));
		map
				.values()
				.forEach(processorForJavaModel::process);

		super.generate(ctx);
	}

	private ValidationsOptions parseArguments(ToolContext ctx) throws ToolException {
		ValidationsOptions.Builder optionsBuilder = ValidationsOptions.builder();

        String[] xjcArgs = (String[]) ctx.get(ToolConstants.CFG_XJC_ARGS);
		if (xjcArgs != null) {
			System.err.println("xjcArgs: \n" + Arrays.deepToString(xjcArgs));

			Arrays.stream(xjcArgs).forEachOrdered(xjcArg -> {
                try {
                    optionsBuilder.parseArgument(xjcArg);
                } catch (BadCommandLineException e) {
                    throw new ToolException("Unable to parse XJC Arguments when generating Java Interfaces. Options given: " + Arrays.deepToString(xjcArgs), e);
                }
            });
		}

		ValidationsOptions validationsOptions = optionsBuilder.build();

		validationsOptions.logActualOptions();

		return validationsOptions;
	}
}
