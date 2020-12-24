/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

/**
 * Package to test JOrphanUtils methods 
 */
package org.apache.jorphan.exec;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestKeyToolUtils {

    private File keystore;
    private String password = RandomStringUtils.randomAlphabetic(32);
    private int validity = 1;

    @Before
    public void setup() throws IOException {
        keystore = File.createTempFile("dummy-keystore", "jks");
        keystore.deleteOnExit();
        KeyToolUtils.generateProxyCA(keystore, password, validity );
    }

    @After
    public void cleanup() {
        if (keystore.exists()) {
            keystore.delete();
        }
    }

    /*
     * Check the assumption that a missing executable will generate
     * either an IOException or status which is neither 0 nor 1 
     *
     */
    @Test
    public void testCheckKeytool() throws Exception {
        SystemCommand sc = new SystemCommand(null, null);
        List<String> arguments = new ArrayList<>();
        arguments.add("xyzqwas"); // should not exist
        try {
            int status = sc.run(arguments);
            if (status == 0 || status ==1) {
                fail("Unexpected status " + status);
            }
        } catch (IOException expected) {
        }
    }

    @Test
    public void testIPBasedCert() throws Exception {
        KeyToolUtils.generateHostCert(keystore, password, "10.1.2.3", validity);
    }

    @Test
    public void testDNSNameBasedCert() throws Exception {
        KeyToolUtils.generateHostCert(keystore, password, "www.example.invalid", validity);
    }

}
