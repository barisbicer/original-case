package com.klm.cases.df.statistic;

public class StatisticReport {
    private final long requestCount;
    private final long request200Count;
    private final long request4XXCount;
    private final long request5XXCount;
    private final long totalResponseTimeMillis;
    private final long avgResponseTimeMillis;
    private final long minResponseTimeMillis;
    private final long maxResponseTimeMillis;

    StatisticReport(long requestCount,
                    long request200Count,
                    long request4XXCount,
                    long request5XXCount,
                    long totalResponseTimeMillis,
                    long minResponseTimeMillis,
                    long maxResponseTimeMillis) {
        this.requestCount = requestCount;
        this.request200Count = request200Count;
        this.request4XXCount = request4XXCount;
        this.request5XXCount = request5XXCount;
        this.totalResponseTimeMillis = totalResponseTimeMillis;
        this.avgResponseTimeMillis = totalResponseTimeMillis == 0 ? 0 : totalResponseTimeMillis / requestCount;
        this.minResponseTimeMillis = minResponseTimeMillis;
        this.maxResponseTimeMillis = maxResponseTimeMillis;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public long getRequest200Count() {
        return request200Count;
    }

    public long getRequest4XXCount() {
        return request4XXCount;
    }

    public long getRequest5XXCount() {
        return request5XXCount;
    }

    public long getTotalResponseTimeMillis() {
        return totalResponseTimeMillis;
    }

    public long getMinResponseTimeMillis() {
        return minResponseTimeMillis;
    }

    public long getMaxResponseTimeMillis() {
        return maxResponseTimeMillis;
    }

    public long getAvgResponseTimeMillis() {
        return avgResponseTimeMillis;
    }
}
