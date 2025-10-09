package dev.lpa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class VisitorsList {
    private static final CopyOnWriteArrayList<Person> masterList;

    static {
        masterList = Stream.generate(Person::new)
                .distinct()
                .limit(2500)
                .collect(CopyOnWriteArrayList::new,
                        CopyOnWriteArrayList::add,
                        CopyOnWriteArrayList::addAll);
    }

    private static final ArrayBlockingQueue<Person> newVisitors = new ArrayBlockingQueue<>(5);
    public static void main(String[] args) {
        Runnable producer = () -> {
            Person visitor = new Person();
            System.out.println("Queueing visitor " + visitor);

            boolean queued = false;
//            try {
//                queued = newVisitors.add(visitor);
//            } catch (IllegalStateException e) {
//                System.out.println("Illegal State Exception");
//            }
//            try {
//                 newVisitors.put(visitor);
//                 queued = true;
//            } catch (InterruptedException e) {
//                System.out.println("Interrupted Exception");
//            }
            try {
                 queued = newVisitors.offer(visitor, 5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println("Timeout Exception");
            }

            if (queued) {
//                System.out.println(newVisitors.toString());
            } else {
                System.out.println("Queue is full, cannot add visitor " + visitor);
                System.out.println("Draining Queue and writing data to file");

                List<Person> tempList = new ArrayList<>();
                newVisitors.drainTo(tempList);
                List<String> lines = new ArrayList<>();
                tempList.forEach(person -> lines.add(person.toString()));
                lines.add(visitor.toString());

                try {
                    Files.write(Path.of("DrainedQueue.txt"), lines,
                            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Runnable consumer = () -> {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + " Polling queue " + newVisitors
                    .size());
            Person visitor = null;
            try {
                visitor = newVisitors.take();
//                visitor = newVisitors.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (visitor != null) {
                System.out.println(threadName + " Visitor " + visitor);
                if (!masterList.contains(visitor)) {
                    masterList.add(visitor);
                    System.out.println("New Visitor gets Coupon!;" + visitor);
                }
            }
            System.out.println(threadName + " done " + newVisitors.size());
        };

        ScheduledExecutorService producerExecutor = Executors.newSingleThreadScheduledExecutor();
        producerExecutor.scheduleWithFixedDelay(producer, 0, 3,
                TimeUnit.SECONDS);

        ScheduledExecutorService consumerPool = Executors.newScheduledThreadPool(3);
        for (int i = 0; i < 3; i++) {
            consumerPool.scheduleAtFixedRate(consumer, 6, 1, TimeUnit.SECONDS);
        }

        while (true) {
            try {
                if (!producerExecutor.awaitTermination(10, TimeUnit.SECONDS))
                    break;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        producerExecutor.shutdown();

        while (true) {
            try {
                if (!consumerPool.awaitTermination(3, TimeUnit.SECONDS))
                    break;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        consumerPool.shutdown();
    }
}
