package ru.netology;
import java.util.*;

public class Main {
    final static String LETTERS = "RLRFR";
    final static int ROUTE_LENGTH = 100;
    final static int THREADS_AMOUNT = 1000;
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threadList = new ArrayList<>();

        Thread printer = new Thread(() -> {
            while(!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                    printLeader();
                }
            }
        });

        printer.start();

        for (int i = 0; i < THREADS_AMOUNT; i++) {
            threadList.add(getThread());
        }
        for (Thread thread : threadList) {
            thread.start();
        }
        for (Thread thread : threadList) {
            thread.join();
        }

        printer.interrupt();
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void printLeader() {
        Map.Entry<Integer, Integer> max = sizeToFreq
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get();
        System.out.println("Current leader is " + max.getKey() +
                " (observed " + max.getValue() + " times)");
    }

    public static Thread getThread() {
        return new Thread(() -> {
            String route = generateRoute(LETTERS, ROUTE_LENGTH);
            int frequency = (int) route.chars().filter(ch -> ch == 'R').count();

            synchronized (sizeToFreq) {
                if (sizeToFreq.containsKey(frequency)) {
                    sizeToFreq.put(frequency, sizeToFreq.get(frequency) + 1);
                } else {
                    sizeToFreq.put(frequency, 1);
                }
                sizeToFreq.notify();
            }
        });
    }
}