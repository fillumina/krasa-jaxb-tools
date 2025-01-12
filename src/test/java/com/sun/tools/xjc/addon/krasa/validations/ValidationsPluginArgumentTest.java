/*
 * Copyright 2024 Francesco Illuminati .
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sun.tools.xjc.addon.krasa.validations;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.addon.krasa.JaxbValidationsPlugin;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati
 */
public class ValidationsPluginArgumentTest {
    private final JaxbValidationsPlugin plugin;
    private final Options opt;

    public ValidationsPluginArgumentTest() {
        plugin = new JaxbValidationsPlugin();
        opt = new Options();
    }

    @Test
    public void shouldSetVersbose() throws BadCommandLineException, IOException {
        setArguments(ValidationsArgument.verbose.withValue("true"));
        ValidationsOptions options = plugin.buildOptions();
        assertTrue(options.isVerbose());
    }

    @Test
    public void shouldSetVersboseWithCapital() throws BadCommandLineException, IOException {
        setArguments(ValidationsArgument.verbose.withValue("TRue"));
        ValidationsOptions options = plugin.buildOptions();
        assertTrue(options.isVerbose());
    }

    @Test(expected = BadCommandLineException.class)
    public void shouldNotSetErroneousVerbose() throws BadCommandLineException, IOException {
        setArguments(ValidationsArgument.verbose.withValue("ERROR"));
    }

    @Test
    public void shouldSetTargetNamespace() throws BadCommandLineException, IOException {
        String ns = "target.namespace";
        setArguments(ValidationsArgument.targetNamespace.withValue(ns));
        ValidationsOptions options = plugin.buildOptions();
        assertEquals(options.getTargetNamespace(), ns);
    }

    private void setArguments(String... args) throws BadCommandLineException, IOException {
        for (int i=0; i<args.length; i++) {
            plugin.parseArgument(opt , args, i);
        }
        plugin.buildOptions();
    }

}
