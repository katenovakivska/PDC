package ForthTask;

import FirstTask.Statistics;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class TopicAffiliation {

    String[] wordsIn(String line) {
        return line.trim().split("(\\s|\\p{Punct})+");
    }
    List<String> ITwords;
    String[] arr = new String[] {"information", "informatics", "computer", "computing", "technology", "technologies"
            , "system", "systems", "data", "digital", "technical", "programming", "communication"};

    public  TopicAffiliation()
    {
        ITwords = new ArrayList<String> ();
        for (String s: arr)
        {
            ITwords.add(s);
        }
    }

    List<DocumentWords> affiliationsCount(Document document) {
        DocumentWords doc = new DocumentWords();
        for (String line : document.getLines()) {
            for (String word : wordsIn(line)) {
                if(ITwords.contains(word.toLowerCase(Locale.ROOT)) && !(doc.containWords.contains(word.toLowerCase(Locale.ROOT))))
                {
                    doc.containWords.add(word.toLowerCase(Locale.ROOT));
                    doc.counter += 1;
                }
            }
        }
        List<DocumentWords> docs = new ArrayList<>();
        doc.name = document.name;
        docs.add(doc);
        return docs;
    }

    List<DocumentWords> affiliancesOnSingleThread(Folder folder) {
        List<DocumentWords> results = new ArrayList<>();
        for (Folder subFolder : folder.getSubFolders()) {
            results.addAll(affiliancesOnSingleThread(subFolder));
        }
        for (Document document : folder.getDocuments()) {
            results.addAll(affiliationsCount(document));
        }

        return results;
    }

   class DocumentSearchTask extends RecursiveTask<List<DocumentWords>> {
        private final Document document;

        DocumentSearchTask(Document document) {
            super();
            this.document = document;
        }

        @Override
        protected List<DocumentWords> compute() {
            return affiliationsCount(document);
        }
    }

    class FolderSearchTask extends RecursiveTask<List<DocumentWords>> {
        private final Folder folder;

        FolderSearchTask(Folder folder) {
            super();
            this.folder = folder;
        }

        @Override
        protected List<DocumentWords> compute() {
            List<DocumentWords> results = new ArrayList<>();
            List<RecursiveTask<List<DocumentWords>>> forks = new LinkedList<>();
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
            for (RecursiveTask<List<DocumentWords>> task : forks) {
                results.addAll(task.join());
            }
            return results;
        }
    }

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    List<DocumentWords> affiliancesInParallel(Folder folder) {
        return forkJoinPool.invoke(new FolderSearchTask(folder));
    }

    public static void main(String[] args) throws IOException {
        TopicAffiliation wordCounter = new TopicAffiliation();
        Folder folder = Folder.fromDirectory(new File("C://Users/Kateryna/Desktop/копеи не для слабонервных/ПРО/PDC4.4"));

        final int repeatCount = Integer.decode("5");
        long startTime;
        long stopTime;

        double[] singleThreadTimes = new double[repeatCount];
        double[] forkedThreadTimes = new double[repeatCount];
        double[] speedUp = new double[repeatCount];

        List<DocumentWords> results = wordCounter.affiliancesInParallel(folder);
        for (DocumentWords d : results) {
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("Filename: " + d.name);
            System.out.println("amount of affiliances: " + d.counter + "       conformity: " + (double) d.counter / wordCounter.ITwords.size());
            System.out.println("List of affiliances:");
            for (String s : d.containWords) {
                System.out.println(s);
            }
        }

        for (int i = 0; i < repeatCount; i++) {
            startTime = System.currentTimeMillis();
            results = wordCounter.affiliancesOnSingleThread(folder);
            stopTime = System.currentTimeMillis();
            singleThreadTimes[i] = (stopTime - startTime);
        }

        for (int i = 0; i < repeatCount; i++) {
            startTime = System.currentTimeMillis();
            results = wordCounter.affiliancesInParallel(folder);
            stopTime = System.currentTimeMillis();
            forkedThreadTimes[i] = (stopTime - startTime);
            speedUp[i] = singleThreadTimes[i] / forkedThreadTimes[i];
        }
        System.out.println("---------------------Compare time---------------------");
        System.out.printf("%-15s %-15s %-15s\n", "Sequential", "Parallel", "SpeedUp");

        for (int i = 0; i < repeatCount; i++)
        {
            speedUp[i] = (double)singleThreadTimes[i]/forkedThreadTimes[i];
            System.out.printf("%-15s %-15s %-15s\n", singleThreadTimes[i],forkedThreadTimes[i],speedUp[i]);
        }
        System.out.println();
        System.out.printf("%-15s %-15s %-15s\n", Arrays.stream(singleThreadTimes).sum() / repeatCount,Arrays.stream(forkedThreadTimes).sum()/repeatCount
                ,Arrays.stream(speedUp).sum()/repeatCount);
    }
}