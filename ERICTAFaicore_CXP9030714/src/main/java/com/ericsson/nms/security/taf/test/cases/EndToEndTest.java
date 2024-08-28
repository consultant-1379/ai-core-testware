/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.taf.test.cases;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.Output;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.handlers.implementation.SshRemoteCommandExecutor;
import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.cifwk.taf.tools.http.HttpTool;
import com.ericsson.cifwk.taf.tools.http.HttpToolBuilder;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.security.taf.test.operators.AICoreOperator;

public class EndToEndTest extends AICoreTest implements TestCase {

    //private static Logger log = Logger.getLogger(EndToEndTest.class);

    public static final String TMP_LOCATION = INT_FILES_DEF_LOC + "tmp_taf_e2e/";
    public static final String TAF_PRIV_KEY = TMP_LOCATION + "aiweb_taf_key.priv";
    public static final String TAF_DEFAULT_CERT = TMP_LOCATION + "aiweb_taf.crt";
    public static final String SOURCE_KEY_FOLDER = new File("ERICTAFaicore_CXP9030714/src/main/resources/keys/").getAbsolutePath() + File.separator;

    @TestId(id = "taftest-aicore-endToEnd-1", title = "Verify ai-web and ai-core read, write and delete works fine using server sc2")
    @Context(context = { Context.API })
    @Test(enabled = true)
    @DataDriven(name = "aicore_endToend")
    public void verifyAutoIntegrationEndToEndSC2(@Input("fileName") final String fileName, @Input("fileContents") final String fileContents,
            @Output("expected") final String expected) {

        runE2ETest(HostConfigurator.getSecServiceUnit1(), fileName, fileContents, expected);
    }

    @TestId(id = "taftest-aicore-endToEnd-2", title = "Verify ai-web and ai-core read, write and delete works fine using server sc1")
    @Context(context = { Context.API })
    @Test(enabled = true)
    @DataDriven(name = "aicore_endToend")
    public void verifyAutoIntegrationEndToEndSC1(@Input("fileName") final String fileName, @Input("fileContents") final String fileContents,
            @Output("expected") final String expected) {

        runE2ETest(HostConfigurator.getSecServiceUnit0(), fileName, fileContents, expected);
    }

    /**
     * End 2 End test of AI-Core and AI-Web
     * 
     * DESCRIPTION: Store using AI-Core, retrieve using AI-Web, delete using
     * AI-Core
     * 
     * @param sc
     *            - Host to copy the keys and certificates needed for the test
     *            (it is stored on the common file system)
     * @param fileName
     *            - name of the file
     * @param fileContents
     *            - content to store
     * @param expected
     *            - expected result of AI-Core invocation
     */
    private void runE2ETest(final Host sc, final String fileName, final String fileContents, final String expected) {

        //fileName = fileName + "_" + System.currentTimeMillis() + "_" + rnd.nextInt(1000);

        final AICoreOperator aiCoreOperator = getOperator();

        //Step 1--- write the file contents via invoking AI-Core EJB storeIntegrationData
        String result = aiCoreOperator.storeIntegrationData(fileName, fileContents.getBytes());
        assertEquals(expected, result);

        //		//Step 2--- call AI-Web rest interface using the key and certificate

        System.out.println("SOURCE_KEY_FOLDER " + SOURCE_KEY_FOLDER.toString() + "aiweb_taf_key.pem" + SOURCE_KEY_FOLDER.toString()
                + "aiweb_taf_SERIALNUMBER123.pem");

        final HttpTool tool = HttpToolBuilder.newBuilder(sc).useHttpsIfProvided(true).trustSslCertificates(true)
                .setSslKeyAndCert(SOURCE_KEY_FOLDER + "aiweb_taf_key.pem", SOURCE_KEY_FOLDER + "aiweb_taf_SERIALNUMBER123.pem").build();

        final HttpResponse response = tool.get("/autobind/SERIALNUMBER123.ericsson.com");

        System.out.println("\n\n\n" + response.getBody());

        Assert.assertEquals(response.getBody(), fileContents);

        //Step 4--- delete the file contents via invoking AI-Core EJB deleteIntegrationData
        result = aiCoreOperator.deleteIntegrationData(fileName);
        assertEquals(expected, result);

        //        //TearDown - delete TMP folder
        //        removeTmpFiles(sc, fileName);
    }

    public void removeTmpFiles(final Host sc, final String fileName) {
        final SshRemoteCommandExecutor executor = new SshRemoteCommandExecutor(sc);
        //TearDown - delete tmp folder
        //executor.execute("rm -r -f", TMP_LOCATION);
        //Just to make sure - it is already deleted by deleteIntegrationData
        executor.execute("rm -r -f", INT_FILES_DEF_LOC + fileName);
    }
}
