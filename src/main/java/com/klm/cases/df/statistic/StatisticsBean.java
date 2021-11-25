package com.klm.cases.df.statistic;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class StatisticsBean {

    private Logger logger = LoggerFactory.getLogger(StatisticsBean.class);

    private final AtomicReference<StatisticReport> report = new AtomicReference<>(
            new StatisticReport(0, 0, 0, 0, 0, Long.MAX_VALUE, Long.MIN_VALUE));
    private final LinkedBlockingQueue<RequestReport> queue = new LinkedBlockingQueue<>();
    private final Thread statisticThread = new Thread(() -> {
        try {
            logger.info("Statistics Handler was started");

            while (true) {
                RequestReport requestReport = queue.take();
                StatisticReport oldReport = report.get();
                StatisticReport newReport = new StatisticReport(
                        oldReport.getRequestCount() + 1,
                        oldReport.getRequest200Count() + (requestReport.getHttpCode() == 200 ? 1 : 0),
                        oldReport.getRequest4XXCount() + (requestReport.getHttpCode() / 100 == 4 ? 1 : 0),
                        oldReport.getRequest5XXCount() + (requestReport.getHttpCode() / 100 == 5 ? 1 : 0),
                        oldReport.getTotalResponseTimeMillis() + requestReport.getExecutionTime(),
                        Math.min(oldReport.getMinResponseTimeMillis(), requestReport.getExecutionTime()),
                        Math.max(oldReport.getMaxResponseTimeMillis(), requestReport.getExecutionTime()));
                report.set(newReport);
            }
        } catch (InterruptedException e) {
            logger.info("Statistics Handler was interrupted");
        }
    });

    {
        statisticThread.setName("Statistics-1");
    }


    @PostConstruct
    private void init() {
        statisticThread.start();
    }


    @PreDestroy
    private void destroy() {
        statisticThread.interrupt();
    }

    void addRequestReport(RequestReport report) {
        queue.add(report);
    }

    public StatisticReport getStatisticReport() {
        return report.get();
    }

}
