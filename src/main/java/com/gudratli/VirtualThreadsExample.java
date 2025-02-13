package com.gudratli;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Dunay Gudratli
 **/
public class VirtualThreadsExample {
    public static void main() {
        System.out.println("*******OS Threads*******");
        runWithTimeMeter(() ->
                runWithMemoryUsageMeter(VirtualThreadsExample::runOSThreadsExample));

        System.out.println();
        System.out.println("*******Virtual Threads*******");
        runWithTimeMeter(() ->
                runWithMemoryUsageMeter(VirtualThreadsExample::runVirtualThreadsExample));
    }

    private static void runOSThreadsExample (){
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

        try (var executor = Executors.newCachedThreadPool()) {
            doWork(executor);
            System.out.printf("Thread count: %d\n", threadBean.getThreadCount());
        }
    }

    private static void runVirtualThreadsExample (){
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            doWork(executor);
            System.out.printf("Thread count: %d\n", threadBean.getThreadCount());
        }
    }

    private static void doWork (ExecutorService executor){
        for (int i = 0; i < 50_000; i++) {
            executor.execute(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static void runWithMemoryUsageMeter(Runnable runnable) {
        long before = getMemoryUsage();
        runnable.run();
        long after = getMemoryUsage();
        System.out.printf("Memory: %d MB\n", (after - before) / (1024 * 1024));
    }

    private static void runWithTimeMeter(Runnable runnable) {
        long start = System.currentTimeMillis();
        runnable.run();
        System.out.printf("%d ms\n", System.currentTimeMillis() - start);
    }

    private static long getMemoryUsage() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
    }
}