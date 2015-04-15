/*
 * Copyright 2015 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onlab.stc;

import org.junit.BeforeClass;
import org.junit.Test;
import org.onlab.util.Tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.io.Files.write;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.onlab.stc.Scenario.loadScenario;

/**
 * Test of the test scenario compiler.
 */
public class CompilerTest {

    static final File TEST_DIR = new File("/tmp/junit-stc");

    @BeforeClass
    public static void setUpClass() throws IOException {
        Tools.removeDirectory(TEST_DIR);
        TEST_DIR.mkdirs();
        stageTestResource("scenario.xml");
        stageTestResource("simple-scenario.xml");
        stageTestResource("one-scenario.xml");
        stageTestResource("two-scenario.xml");

        System.setProperty("prop.foo", "Foobar");
        System.setProperty("prop.bar", "Barfoo");
        System.setProperty("OC1", "1.2.3.1");
        System.setProperty("OC2", "1.2.3.2");
        System.setProperty("OC3", "1.2.3.3");
    }

    public static FileInputStream getStream(String name) throws FileNotFoundException {
        return new FileInputStream(new File(TEST_DIR, name));
    }

    private static void stageTestResource(String name) throws IOException {
        byte[] bytes = toByteArray(CompilerTest.class.getResourceAsStream(name));
        write(bytes, new File(TEST_DIR, name));
    }

    @Test
    public void basics() throws Exception {
        Scenario scenario = loadScenario(getStream("scenario.xml"));
        Compiler compiler = new Compiler(scenario);
        compiler.compile();
        ProcessFlow flow = compiler.processFlow();

        assertSame("incorrect scenario", scenario, compiler.scenario());
        assertEquals("incorrect step count", 25, flow.getVertexes().size());
        assertEquals("incorrect dependency count", 21, flow.getEdges().size());
        assertEquals("incorrect logDir", "/tmp/junit-stc/foo", compiler.logDir().getPath());

        Step step = compiler.getStep("there");
        assertEquals("incorrect edge count", 2, flow.getEdgesFrom(step).size());
        assertEquals("incorrect edge count", 0, flow.getEdgesTo(step).size());

        Step group = compiler.getStep("three");
        assertEquals("incorrect edge count", 2, flow.getEdgesFrom(group).size());
        assertEquals("incorrect edge count", 0, flow.getEdgesTo(group).size());
    }

}