package com.ericsson.nms.security.taf.test.cases;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.Output;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.cifwk.taf.handlers.RemoteFileHandler;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.security.taf.test.operators.AICoreOperator;

public class AICoreTest extends TorTestCaseHelper implements TestCase {

    @Inject
    private OperatorRegistry<AICoreOperator> aICoreProvider;
    private static Logger log = Logger.getLogger(AICoreTest.class);

    public static final String INT_FILES_DEF_LOC = "/ericsson/tor/data/autointegration/files/";
    public static final String TMP_LOCATION = INT_FILES_DEF_LOC + "tmp/";
    public static final String TAF_PRIV_KEY = TMP_LOCATION + "aiweb_taf_key.priv";
    public static final String TAF_DEFAULT_CERT = TMP_LOCATION + "aiweb_taf_SERIALNUMBER123.crt";
    public static final String SOURCE_KEY_FOLDER = "src/main/resources/keys/";

    String path = "";
    RemoteFileHandler rfh;

    @TestId(id = "taftest-aicore-func-1", title = "Verify ai-core can write files as expected")
    @Context(context = { Context.API })
    @Test(enabled = true)
    @DataDriven(name = "aicore_write")
    public void verifyAICoreWriteFiles(@Input("fileName") final String fileName, @Input("fileContents") final String fileContents,
            @Output("expected") final String expected) {
        log.info("Oasis taf test started calling TAF operator...............");
        final AICoreOperator aiCoreOperator = getOperator();
        final String result = aiCoreOperator.storeIntegrationData(fileName, fileContents.getBytes());
        assertEquals(expected, result);

    }

    @TestId(id = "taftest-aicore-func-2", title = "Verify ai-core can write multiple times the files as expected")
    @Context(context = { Context.API })
    @Test(enabled = true)
    @DataDriven(name = "aicore_write")
    public void verifyAICoreWriteFilesMultipleCalls(@Input("fileName") final String fileName, @Input("fileContents") final String fileContents,
            @Output("expected") final String expected) {
        log.info("Oasis taf test started calling TAF operator...............");
        final AICoreOperator aiCoreOperator = getOperator();
        String result = aiCoreOperator.storeIntegrationData(fileName, fileContents.getBytes());
        result = aiCoreOperator.storeIntegrationData(fileName, fileContents.getBytes());
        result = aiCoreOperator.storeIntegrationData(fileName, fileContents.getBytes());
        assertEquals(expected, result);

        result = aiCoreOperator.storeIntegrationData(fileName, fileContents.getBytes());
        assertEquals(expected, result);

    }

    @TestId(id = "taftest-aicore-func-3", title = "Verify ai-core can delete files as expected")
    @Context(context = { Context.API })
    @Test(enabled = true)
    @DataDriven(name = "aicore_delete")
    public void verifyAICoreDeleteFiles(@Input("fileName") final String fileName, @Input("fileContents") final String fileContents,
            @Output("expected") final String expected) {
        log.info("Oasis taf test started calling TAF operator...............");
        final AICoreOperator aiCoreOperator = getOperator();

        // first to insert the files
        String result = aiCoreOperator.storeIntegrationData(fileName, fileContents.getBytes());
        assertEquals(expected, result);

        result = aiCoreOperator.deleteIntegrationData(fileName);
        assertEquals(expected, result);

    }

    @TestId(id = "taftest-aicore-func-4", title = "Verify ai-core delete works fine when there is no file to delete")
    @Context(context = { Context.API })
    @Test(enabled = true)
    @DataDriven(name = "aicore_delete_nonexisting")
    public void verifyAICoreDeleteNonExistingFiles(@Input("fileName") final String fileName, @Input("fileContents") final String fileContents,
            @Output("expected") final String expected) {
        log.error("Oasis taf test started calling TAF operator...............");
        final AICoreOperator aiCoreOperator = getOperator();

        final String result = aiCoreOperator.deleteIntegrationData(fileName);
        assertEquals(expected, result);

    }

    @TestId(id = "taftest-aicore-func-5", title = "Verify ai-core can write concurrent files as expected")
    @Context(context = { Context.API })
    @Test(enabled = true)
    @DataDriven(name = "aicore_write")
    public void verifyAICoreConcurrentWritingFilesAsExpected(@Input("fileName") final String fileName,
            @Input("fileContents") final String fileContents, @Output("expected") final String expected) {
        log.error("Oasis taf test started calling TAF operator...............");
        final AICoreOperator aiCoreOperator = getOperator();
        final String result = aiCoreOperator.storeIntegrationData(fileName, fileContents.getBytes());
        assertEquals(expected, result);

    }

    AICoreOperator getOperator() {
        final AICoreOperator op = aICoreProvider.provide(AICoreOperator.class);
        op.setHost(HostConfigurator.getSecServiceUnit0());
        return op;
    }

    AICoreOperator getOperator(final Host jbi) {
        final AICoreOperator op = aICoreProvider.provide(AICoreOperator.class);
        op.setHost(jbi);
        return op;
    }
}
