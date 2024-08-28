package com.ericsson.nms.security.taf.test.operators;

import javax.ejb.EJBException;
import javax.inject.Singleton;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.handlers.AsRmiHandler;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.security.aicore.api.AICoreService;
import com.ericsson.nms.security.aicore.api.exception.AICoreServiceException;
import com.ericsson.nms.security.taf.test.helpers.AICoreOperation;

@Operator(context = Context.API)
@Singleton
public class AICoreOperatorEjb implements AICoreOperator {

    //Default host, can be changed via setHost
    private Host host = HostConfigurator.getSecServiceUnit0();

    public void setHost(final Host host) {
        this.host = host;
    }

    private AICoreService locateEjb() {
        final String jndiString = (String) DataHandler.getAttribute("aicore.jndi");

        try {
            final AsRmiHandler asRmiHandler = new AsRmiHandler(host);
            return (AICoreService) asRmiHandler.getServiceViaJndiLookup(jndiString);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to locate AI-Core EJB!", e);
        }
    }

    public String deleteIntegrationData(final String serialNumber) {
        return invokeEjb(serialNumber, null, AICoreOperation.DELETE);

    }

    public String storeIntegrationData(final String serialNumber, final byte[] integrationData) {
        return invokeEjb(serialNumber, integrationData, AICoreOperation.WRITE);

    }

    private String invokeEjb(final String serialNo, final byte[] integrationData, final AICoreOperation operation) {

        // Need to look it up each time, otherwise JBoss starts to throw
        // exceptions with EJBCLIENT000025 error
        final AICoreService aiCoreServiceEjb = locateEjb();
        switch (operation) {
        case READ:
            try {
                aiCoreServiceEjb.storeIntegrationData(serialNo, integrationData);
            } catch (EJBException e) {

                return AICoreOperator.EJB_EXCEPTION;

            } catch (AICoreServiceException e) {
                return AICoreOperator.AI_CORE_READ_EXCEPTION;

            }
            break;
        case DELETE:
            try {
                aiCoreServiceEjb.deleteIntegrationData(serialNo);
            } catch (EJBException e) {

                return AICoreOperator.EJB_EXCEPTION;

            } catch (AICoreServiceException e) {
                return AICoreOperator.AI_CORE_DELETE_EXCEPTION;

            }
            break;
        case WRITE:
            try {
                // TODO: change to the appropriate READ operation
                aiCoreServiceEjb.storeIntegrationData(serialNo, integrationData);
            } catch (EJBException e) {

                return AICoreOperator.EJB_EXCEPTION;

            } catch (AICoreServiceException e) {
                return AICoreOperator.AI_CORE_WRITE_EXCEPTION;

            }
            break;
        }
        return AICoreOperator.OPERATION_SUCCESS;
    }
}
