package com.standarddeviation.standarddeviationcalculate.Calculator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StandardDeviation {
    public static void main(String[] args) throws Exception {
        short[] shorts = new short[]{10, 8, 10, 8, 8, 4};
        double res = standartSapmaHesapla(shorts, 4);

        System.out.println("Standard Deviation : " + res);
    }

    public static double totalResult;

    public static double standartSapmaHesapla(short[] numbers, int threadCount) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        int chunkSize = numbers.length / threadCount;

        Collection<List<Short>> values = IntStream.range(0, numbers.length)
                .boxed()
                .collect(Collectors.groupingBy(partition -> (partition / chunkSize), Collectors.mapping(elementIndex -> numbers[elementIndex], Collectors.toList())))
                .values();

        List<Callable<Short>> listOfCallable = new ArrayList<>();
        short totalNumbers = 0;
        for (int i = 0; i < numbers.length; i++) {
            totalNumbers += numbers[i];
        }
        short termNumber = (short) (totalNumbers / numbers.length);

        for (int i = 0; i < values.size(); i++) {

            short[] arr = new short[values.stream().toList().get(i).size()];
            for (int j = 0; j < values.stream().toList().get(i).size(); j++) {
                arr[j] = values.stream().toList().get(i).get(j);
            }

            listOfCallable.add(() -> calcAvg(arr, termNumber));
        }

        try {
            List<Future<Short>> results = executorService.invokeAll(listOfCallable);
            results.forEach(r -> {
                try {
                    System.out.println("Result of the thread: " + r.get());
                    totalResult = totalResult + r.get().doubleValue();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double avg = totalResult / (numbers.length - 1);
        avg = Math.sqrt(avg);

        executorService.shutdown();
        return avg;
    }

    public static short calcAvg(short[] partitionedArray, int termNumber) {

        short result = 0;

        for (int j = 0; j < partitionedArray.length; j++) {
            result += Math.pow((partitionedArray[j] - termNumber), 2);
        }

        return result;
    }
}
