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

import java.util.Random;

import javax.naming.NameNotFoundException;

import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.handlers.AsRmiHandler;
import com.ericsson.cifwk.taf.handlers.RemoteFileHandler;
import com.ericsson.cifwk.taf.handlers.implementation.SshRemoteCommandExecutor;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.security.aicore.api.AICoreService;
import com.ericsson.nms.security.aicore.api.exception.AICoreServiceException;

public class TafEnvironmentTest extends TorTestCaseHelper implements TestCase {

    Host sc1 = HostConfigurator.getSC1();
    Host sc1jbi = HostConfigurator.getSecServiceUnit0();

    Host sc2 = HostConfigurator.getSC2();
    Host sc2jbi = HostConfigurator.getSecServiceUnit1();

    static final String INT_FILES_DEF_LOC = "/ericsson/tor/data/autointegration/files/";
    static final String TMP_LOCATION = INT_FILES_DEF_LOC + "taf_env_tmp/";
    static final String SOURCE_KEY_FOLDER = "src/main/resources/keys/";

    @TestId(id = "taftest-aicore-env1", title = "Make sure remoteFileHandler works against sc1")
    @Test(enabled = true, priority = 1)
    public void testSC1RemoteFile() {

        final RemoteFileHandler sc1r = new RemoteFileHandler(sc1);
        sc1r.copyLocalFileToRemote(SOURCE_KEY_FOLDER + "aiweb_taf_key.priv", TMP_LOCATION + "taf_env_copy_sc1");
    }

    @TestId(id = "taftest-aicore-env2", title = "Make sure remoteFileHandler works against sc2")
    @Test(enabled = true, priority = 1)
    public void testSC2RemoteFile() {

        final RemoteFileHandler sc2r = new RemoteFileHandler(sc2);
        sc2r.copyLocalFileToRemote(SOURCE_KEY_FOLDER + "aiweb_taf_key.priv", TMP_LOCATION + "taf_env_copy_sc2");
    }

    @TestId(id = "taftest-aicore-env3", title = "Make sure EJB can be invoked against sc1")
    @Test(enabled = true, priority = 1)
    public void testSC1InvokeEJB() throws AICoreServiceException {

        final byte[] data = new byte[20];
        new Random().nextBytes(data);
        locateEjb(sc1jbi).storeIntegrationData("taf_env_ejb_store_sc1jbi", data);
    }

    @TestId(id = "taftest-aicore-env4", title = "Make sure EJB can be invoked against sc2")
    @Test(enabled = true, priority = 1)
    public void testSC2InvokeEJB() throws AICoreServiceException {

        final byte[] data = new byte[20];
        new Random().nextBytes(data);
        locateEjb(sc2jbi).storeIntegrationData("taf_env_ejb_store_sc2jbi", data);
    }

    @TestId(id = "taftest-aicore-env5", title = "Make sure RemoteExecutor works against sc1")
    @Test(enabled = true, priority = 1)
    public void testSC1ExecCommand() {

        final SshRemoteCommandExecutor esc1 = new SshRemoteCommandExecutor(sc1);
        esc1.simplExec("ls -l " + INT_FILES_DEF_LOC);
    }

    @TestId(id = "taftest-aicore-env6", title = "Make sure RemoteExecutor works against sc1")
    @Test(enabled = true, priority = 1)
    public void testSC2ExecCommand() {

        final SshRemoteCommandExecutor esc2 = new SshRemoteCommandExecutor(sc2);
        esc2.simplExec("ls -l " + INT_FILES_DEF_LOC);
    }

    @TestId(id = "taftest-aicore-env7", title = "Make sure tmp files can be deleted on sc1")
    @Test(priority = 999, enabled = true)
    public void testSC1DeleteTmpFiles() {

        final SshRemoteCommandExecutor esc1 = new SshRemoteCommandExecutor(sc1);
        esc1.execute("rm -r -f", TMP_LOCATION);
        esc1.execute("rm -r -f", INT_FILES_DEF_LOC + "/taf_env*");
    }

    @TestId(id = "taftest-aicore-env8", title = "Make sure tmp files can be deleted on sc2")
    @Test(priority = 999, enabled = true)
    public void testSC2DeleteTmpFiles() {

        final SshRemoteCommandExecutor esc2 = new SshRemoteCommandExecutor(sc2);
        esc2.execute("rm -r -f", TMP_LOCATION);
        esc2.execute("rm -r -f", INT_FILES_DEF_LOC + "/taf_env*");
    }

    private AICoreService locateEjb(final Host server) {
        final String jndiString = (String) DataHandler.getAttribute("aicore.jndi");
        final AsRmiHandler asRmiHandler = new AsRmiHandler(server);

        try {
            final Object service = asRmiHandler.getServiceViaJndiLookup(jndiString);
            return (AICoreService) service;
        } catch (NameNotFoundException e) {
            throw new IllegalStateException("Failed to locate AI-Core EJB: NameNotFoundException", e);
        } catch (Exception e) {
            throw new IllegalStateException("Exception was thrown while AsRmiHandler(TAF) JNDI lookup", e);
        }
    }
}
