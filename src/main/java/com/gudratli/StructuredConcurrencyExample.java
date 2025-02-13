package com.gudratli;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;

public class StructuredConcurrencyExample {
    public static void main() {
        doExecutorServiceExample();
    }

    @SuppressWarnings("preview")
    private static void doStructuredConcurrencyExample() {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var task1 = scope.fork(StructuredConcurrencyExample::fetchDataFromAPI);
            var task2 = scope.fork(StructuredConcurrencyExample::fetchDataFromDatabase);

            scope.join();
            scope.throwIfFailed();

            System.out.printf("API Result: %s\n", task1.get());
            System.out.printf("DB Result: %s\n", task2.get());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private static void doExecutorServiceExample() {
        try (var executor = Executors.newFixedThreadPool(2)) {
            Future<String> apiFuture = executor.submit(StructuredConcurrencyExample::fetchDataFromAPI);
            Future<String> dbFuture = executor.submit(StructuredConcurrencyExample::fetchDataFromDatabase);

            System.out.printf("API Result: %s\n", apiFuture.get());
            System.out.printf("DB Result: %s\n", dbFuture.get());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private static String fetchDataFromAPI() throws InterruptedException {
        Thread.sleep(2000);
        return "API data";
    }

    private static String fetchDataFromDatabase() throws InterruptedException {
        Thread.sleep(3000);
        throw new RuntimeException();
    }
}
