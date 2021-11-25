package com.klm.cases.df.statistic;

public class RequestReport {
    private final long executionTime;
    private final int httpCode;

    public RequestReport(long executionTime, int httpCode) {
        this.executionTime = executionTime;
        this.httpCode = httpCode;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public int getHttpCode() {
        return httpCode;
    }
}
