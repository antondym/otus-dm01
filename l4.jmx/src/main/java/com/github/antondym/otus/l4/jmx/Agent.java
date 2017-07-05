package com.github.antondym.otus.l4.jmx;

import javax.management.MBeanServer;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

class Agent {
    private final static Logger LOGGER = Logger.getLogger(Agent.class.getName());

    private final Map<GarbageCollectorMXBean, GcStats> _lastValues = new HashMap<>();

    Agent() {
        new Thread(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
               try {
                   Thread.sleep(1000L);
               } catch (InterruptedException e) {
                    LOGGER.info("Agent thread killed");
               }
               printGCStats();
           }
        }).start();
    }

    private void printGCStats() {
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();

        for (GarbageCollectorMXBean bean : gcBeans) {
            long collectionCount = bean.getCollectionCount();
            long collectionTime = bean.getCollectionTime();

            GcStats stats = _lastValues.getOrDefault(bean, new GcStats());

            LOGGER.info(bean.getName() + " " + (collectionCount - stats.CollectionCount) + " collections, "
                    + (collectionTime - stats.CollectionTime) + " ms");

            stats.CollectionCount = collectionCount;
            stats.CollectionTime = collectionTime;

            _lastValues.putIfAbsent(bean, stats);
        }
    }

    private class GcStats {
        long CollectionCount;
        long CollectionTime;
    }
}
