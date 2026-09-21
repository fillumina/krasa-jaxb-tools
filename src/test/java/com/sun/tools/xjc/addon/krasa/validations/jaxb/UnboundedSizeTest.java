package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.PathUtil;
import com.sun.tools.xjc.addon.krasa.validations.FixtureTest;
import com.sun.tools.xjc.addon.krasa.validations.ValidationsAnnotation;
import java.io.File;

/**
 *
 * @author Francesco Illuminati
 */
public class UnboundedSizeTest extends FixtureTest {

    // using an existing parsed XSD
    public UnboundedSizeTest(ValidationsAnnotation library) {
        super(library, "size/schema", "a");
    }

    @Override
    protected File getBindingDirectory() {
        String binding = PathUtil.getAbsolutePathOfResource("/size/bindings");
        return new File(binding);
    }


}
