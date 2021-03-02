package FirstTask;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class WordCounter {

    String[] wordsIn(String line) {
        return line.trim().split("(\\s|\\p{Punct})+");
    }

    Statistics occurrencesCount(Document document) {
        Statistics statistics = new Statistics();
        long[] array = new long[2];
        for (String line : document.getLines()) {
            statistics.countOfWords += wordsIn(line).length;
            for (String word : wordsIn(line)) {
                statistics.lengthes += word.length();
                if(word.length() > statistics.maxLength )
                {
                    statistics.maxLength = word.length();
                }
                if(word.length() < statistics.minLength)
                {
                    statistics.minLength = word.length();
                }
                if(word.length() != 0 && word.length() < 30) {
                    statistics.counter[word.length() - 1] += 1;
                }
            }
        }

        return statistics;
    }

    Statistics countOccurrencesOnSingleThread(Folder folder) {
        Statistics statistics = new Statistics();
        for (Folder subFolder : folder.getSubFolders()) {
            Statistics results = countOccurrencesOnSingleThread(subFolder);
            statistics.countOfWords += results.countOfWords;
            statistics.lengthes += results.lengthes;
            statistics.minLength = results.minLength;
            statistics.maxLength = results.maxLength;
            statistics.counter = results.counter;
        }
        for (Document document : folder.getDocuments()) {
            Statistics results = occurrencesCount(document);
            statistics.countOfWords += results.countOfWords;
            statistics.lengthes += results.lengthes;
            statistics.minLength = results.minLength;
            statistics.maxLength = results.maxLength;
            statistics.counter = results.counter;
        }

        return statistics;
    }

   class DocumentSearchTask extends RecursiveTask<Statistics> {
        private final Document document;

        DocumentSearchTask(Document document) {
            super();
            this.document = document;
        }

        @Override
        protected Statistics compute() {
            return occurrencesCount(document);
        }
    }

    class FolderSearchTask extends RecursiveTask<Statistics> {
        private final Folder folder;

        FolderSearchTask(Folder folder) {
            super();
            this.folder = folder;
        }

        @Override
        protected Statistics compute() {
            Statistics statistics = new Statistics();
            List<RecursiveTask<Statistics>> forks = new LinkedList<>();
            for (Folder subFolder : folder.getSubFolders()) {
                FolderSearchTask task = new FolderSearchTask(subFolder);
                forks.add(task);
                task.fork();
            }
            for (Document document : folder.getDocuments()) {
                DocumentSearchTask task = new DocumentSearchTask(document);
                forks.add(task);
                task.fork();
            }
            for (RecursiveTask<Statistics> task : forks) {
                statistics.countOfWords += task.join().countOfWords;
                statistics.lengthes += task.join().lengthes;
                statistics.minLength = task.join().minLength;
                statistics.maxLength = task.join().maxLength;
                statistics.counter = task.join().counter;
            }
            return statistics;
        }
    }

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    Statistics countOccurrencesInParallel(Folder folder) {
        return forkJoinPool.invoke(new FolderSearchTask(folder));
    }

    public static void main(String[] args) throws IOException {
        String[] ITwords = new String[] {"information", "informatics", "computer", "computing", "technology", "technologies", "systems", "data"};
        WordCounter wordCounter = new WordCounter();
        Folder folder = Folder.fromDirectory(new File("C://Users/Kateryna/Desktop/копеи не для слабонервных/ПРО/PDC4"));

        final int repeatCount = Integer.decode("5");
        Statistics statistics = new Statistics();
        long startTime;
        long stopTime;

        double[] singleThreadTimes = new double[repeatCount];
        double[] forkedThreadTimes = new double[repeatCount];
        double[] wordLength = new double[repeatCount];
        double[] speedUp = new double[repeatCount];

        statistics = wordCounter.countOccurrencesOnSingleThread(folder);
        System.out.println("Average length: " + (double)statistics.lengthes/statistics.countOfWords);
        System.out.println("Min length: " + statistics.minLength);
        System.out.println("Max length: " + statistics.maxLength);
        System.out.println("--------------------Amount of words of different length--------------------");
        System.out.printf("%-10s %-10s %-15s\n", "length: ", "amount: ","frequency: ");

        for (int i = 0; i < statistics.counter.length; i++)
        {
            System.out.printf("%-10s %-10s %-15s\n", (i + 1), statistics.counter[i],(double)statistics.counter[i]/statistics.countOfWords);
        }

        for (int i = 0; i < repeatCount; i++) {
            startTime = System.currentTimeMillis();
            statistics = wordCounter.countOccurrencesOnSingleThread(folder);
            stopTime = System.currentTimeMillis();
            singleThreadTimes[i] = (stopTime - startTime);
            wordLength[i] = (double)statistics.lengthes / statistics.countOfWords;
        }

        for (int i = 0; i < repeatCount; i++) {
            startTime = System.currentTimeMillis();
            statistics = wordCounter.countOccurrencesInParallel(folder);
            stopTime = System.currentTimeMillis();
            forkedThreadTimes[i] = (stopTime - startTime);
            speedUp[i] = singleThreadTimes[i] / forkedThreadTimes[i];
            //System.out.println((double)array[1] / array[0] + " , fork / join search took " + forkedThreadTimes[i] + "ms");
        }

       /* System.out.println("\nCSV Output:\n");
        System.out.println("Single thread,Fork/Join");
        for (int i = 0; i < repeatCount; i++) {
            System.out.println(singleThreadTimes[i] + "," + forkedThreadTimes[i]);
        }*/
        System.out.println();
        System.out.println("-----------------------------------------------------------Compare time-----------------------------------------------------------");
        System.out.format("%32s%32s%32s%32s", "Average length", "Sequential", "Parallel", "SpeedUp");
        for (int i = 0; i < repeatCount; i++) {
            System.out.println();
            System.out.format("%32f%32f%32f%32f", wordLength[i], (double)singleThreadTimes[i], (double) forkedThreadTimes[i], (double) (singleThreadTimes[i] / forkedThreadTimes[i]));
        }
        System.out.println();
        System.out.println();
        System.out.format("%32f%32f%32f%32f", wordLength[0], (double)Arrays.stream(singleThreadTimes).sum()/repeatCount,
                (double) Arrays.stream(forkedThreadTimes).sum()/repeatCount, (double) Arrays.stream(speedUp).sum()/repeatCount);
    }
}