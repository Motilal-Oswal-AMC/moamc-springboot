//package com.motilaloswal.scheduler;
//
//import com.motilaloswal.services.impl.AEMContentServiceImpl;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//public class AEMContentScheduler {
//
//    private static final Logger log = LoggerFactory.getLogger(AEMContentScheduler.class);
//
//    @Autowired
//    private AEMContentServiceImpl aemContentService; // Using implementation to access the new method
//
//
//     // scheduler to runs every day at 10:00 PM (22:00) IST.
//     // This will call the service to fetch the latest data from AEM and refresh the Redis cache.
//     // Cron format: "second minute hour day-of-month month day-of-week"
//    @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Kolkata")
//    public void triggerAemCacheRefresh() {
//        log.info("SCHEDULER: Initiating scheduled AEM content refresh.");
//        try {
//            aemContentService.refreshAemCache();
//            log.info("SCHEDULER: AEM content refresh completed successfully.");
//        } catch (Exception e) {
//            log.error("SCHEDULER: An error occurred during the scheduled AEM cache refresh.", e);
//        }
//    }
//
//    @Scheduled(cron = "0 5 22 * * *", zone = "Asia/Kolkata")
//    public void triggerAemFilteredCacheRefresh() {
//        log.info("SCHEDULER: Initiating scheduled AEM content refresh for FILTERED data.");
//        try {
//            aemContentService.refreshAemFilteredCache();
//            log.info("SCHEDULER: AEM content refresh for FILTERED data completed successfully.");
//        } catch (Exception e) {
//            log.error("SCHEDULER: An error occurred during the scheduled refresh for FILTERED data.", e);
//        }
//    }
//}
