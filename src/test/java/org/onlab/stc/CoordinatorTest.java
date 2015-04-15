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

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.onlab.stc.CompilerTest.getStream;
import static org.onlab.stc.Coordinator.print;
import static org.onlab.stc.Scenario.loadScenario;

/**
 * Test of the test coordinator.
 */
public class CoordinatorTest {

    private Coordinator coordinator;
    private StepProcessListener listener = new Listener();

    @BeforeClass
    public static void setUpClass() throws IOException {
        CompilerTest.setUpClass();
        StepProcessor.launcher = "true ";
    }

    @Test
    public void simple() throws FileNotFoundException, InterruptedException {
        executeTest("simple-scenario.xml");
    }

    @Test
    public void complex() throws FileNotFoundException, InterruptedException {
        executeTest("scenario.xml");
    }

    private void executeTest(String name) throws FileNotFoundException, InterruptedException {
        Scenario scenario = loadScenario(getStream(name));
        Compiler compiler = new Compiler(scenario);
        compiler.compile();
        coordinator = new Coordinator(scenario, compiler.processFlow(), compiler.logDir());
        coordinator.addListener(listener);
        coordinator.start();
        coordinator.waitFor();
        coordinator.removeListener(listener);
    }

    private class Listener implements StepProcessListener {
        @Override
        public void onStart(Step step) {
            print("> %s: started", step.name());
        }

        @Override
        public void onCompletion(Step step, int exitCode) {
            print("< %s: %s", step.name(), exitCode == 0 ? "completed" : "failed");
        }

        @Override
        public void onOutput(Step step, String line) {
            print("  %s: %s", step.name(), line);
        }
    }
}