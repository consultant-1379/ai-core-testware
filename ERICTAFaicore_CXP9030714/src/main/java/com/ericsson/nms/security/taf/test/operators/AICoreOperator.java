package com.ericsson.nms.security.taf.test.operators;

import com.ericsson.cifwk.taf.data.Host;

public interface AICoreOperator {

    String OPERATION_SUCCESS = "SUCCESS";
    String OPERATION_FAILED = "ERROR";
    String EJB_EXCEPTION = "EJB_EXCEPTION";
    String AI_CORE_EXCEPTION = "AI_CORE_EXCEPTION";

    String AI_CORE_READ_EXCEPTION = "AI_CORE_READ_EXCEPTION";
    String AI_CORE_WRITE_EXCEPTION = "AI_CORE_WRITE_EXCEPTION";
    String AI_CORE_DELETE_EXCEPTION = "AI_CORE_DELETE_EXCEPTION";

    String deleteIntegrationData(final String serialNumber);

    String storeIntegrationData(final String serialNumber, final byte[] integrationData);

    void setHost(final Host host);
}
