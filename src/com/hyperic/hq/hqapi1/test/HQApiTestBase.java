package com.hyperic.hq.hqapi1.test;

import junit.framework.TestCase;
import com.hyperic.hq.hqapi1.HQApi;
import com.hyperic.hq.hqapi1.types.ResponseStatus;
import com.hyperic.hq.hqapi1.types.ServiceError;

public class HQApiTestBase  extends TestCase {

    private static final String  HOST        = "localhost";
    private static final int     PORT        = 7080;
    private static final boolean IS_SECURE   = false;
    private static final String  USER        = "hqadmin";
    private static final String  PASSWORD    = "hqadmin";

    public HQApiTestBase(String name) {
        super(name);
    }

    HQApi getApi() {
        return new HQApi(HOST, PORT, IS_SECURE, USER, PASSWORD);
    }

    HQApi getApi(String user, String password) {
        return new HQApi(HOST, PORT, IS_SECURE, user, password);
    }

    // Checks for status success or failure.

    void hqAssertSuccess(ResponseStatus status) {
        assertEquals(ResponseStatus.SUCCESS, status);
    }

    void hqAssertFailure(ResponseStatus status) {
        assertEquals(ResponseStatus.FAILURE, status);
    }

    // Error code checks
    
    void hqAssertErrorLoginFailure(ServiceError error) {
        assertEquals("LoginFailure", error.getErrorCode());
    }

    void hqAssertErrorObjectNotFound(ServiceError error) {
        assertEquals("ObjectNotFound", error.getErrorCode());
    }

    void hqAssertErrorObjectExists(ServiceError error) {
        assertEquals("ObjectExists", error.getErrorCode());
    }

    void hqAssertErrorInvalidParameters(ServiceError error) {
        assertEquals("InvalidParameters", error.getErrorCode());
    }

    // Unlikely, but here for completeness.
    void hqAssertErrorUnexpectedError(ServiceError error) {
        assertEquals("UnexpectedError", error.getErrorCode());
    }

    void hqAssertErrorPermissionDenied(ServiceError error) {
        assertEquals("PermissionDenied", error.getErrorCode());
    }
}
