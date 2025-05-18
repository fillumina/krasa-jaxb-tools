package com.sun.tools.xjc.addon.krasa.validations.jaxb;

import com.sun.tools.xjc.addon.krasa.validations.PathUtil;
import com.sun.tools.xjc.addon.krasa.validations.RunXJC2MojoTestHelper;
import java.io.File;

/**
 *
 * @author Francesco Illuminati
 */
public class UnboundedSizeTest extends RunXJC2MojoTestHelper {

    // using an existing parsed XSD
    public UnboundedSizeTest() {
        super("size/schema", "a");
    }

    @Override
    public File getBindingDirectory() {
        String binding = PathUtil.getAbsolutePathOfResource("/size/bindings");
        return new File(binding);
    }


}
