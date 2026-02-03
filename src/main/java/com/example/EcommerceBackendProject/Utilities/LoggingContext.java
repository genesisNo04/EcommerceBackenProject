package com.example.EcommerceBackendProject.Utilities;

import org.slf4j.MDC;

public class LoggingContext {

    public static void setCallerContext(Long callerId, boolean isAdmin) {
        MDC.put("callerId", callerId == null ? "UNKNOWN" : String.valueOf(callerId));
        MDC.put("callerType", isAdmin ? "ADMIN" : "USER");
    }

    public static void clear() {
        MDC.clear();
    }
}
