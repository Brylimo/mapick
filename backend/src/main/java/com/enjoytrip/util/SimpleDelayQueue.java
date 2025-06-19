package com.enjoytrip.util;

import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
public class SimpleDelayQueue {
    private static final SimpleDelayQueue instance = new SimpleDelayQueue();
    private final PriorityQueue<DelayedTask> queue = new PriorityQueue<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static SimpleDelayQueue getInstance() {
        return instance;
    }

    public synchronized void add(DelayedTask task) {
        queue.offer(task);
        notify(); // 대기 중인 스레드 깨움
    }

    private SimpleDelayQueue() {
        Thread worker = new Thread(() -> {
            while (true) {
                try {
                    DelayedTask task = null;
                    synchronized (this) {
                        while (queue.isEmpty() || !queue.peek().isReady()) {
                            long waitTime = queue.isEmpty() ? 0 : queue.peek().delayUntil();
                            wait(waitTime > 0 ? waitTime : 1000); // 최소 대기 시간
                        }
                        task = queue.poll();
                    }

                    if (task != null) {
                        executor.submit(task);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        worker.setDaemon(true); // 백그라운드로 실행
        worker.start();
    }

    public static class DelayedTask implements Comparable<DelayedTask>, Runnable {
        private final long scheduledAt; // ms

        private final Runnable task;

        public DelayedTask(long delayMillis, Runnable task) {
            this.scheduledAt = System.currentTimeMillis() + delayMillis;
            this.task = task;
        }

        public boolean isReady() {
            return System.currentTimeMillis() >= scheduledAt;
        }

        public long delayUntil() {
            return scheduledAt - System.currentTimeMillis();
        }

        @Override
        public int compareTo(DelayedTask o) {
            return Long.compare(this.scheduledAt, o.scheduledAt);
        }

        @Override
        public void run() {
            task.run();
        }
    }
}
