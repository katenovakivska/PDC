package ThirdTask;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class SameWords {

    String[] wordsIn(String line) {
        return line.trim().split("(\\s|\\p{Punct})+");
    }

    List<String> findAllWords(Document document) {
        List<String> commonWords = new ArrayList<>();
        for (String line : document.getLines()) {
            for (String word : wordsIn(line)) {
                if(!commonWords.contains(word.toLowerCase(Locale.ROOT)) && word.matches("^[a-zA-Z]*$"))
                {
                    commonWords.add(word.toLowerCase(Locale.ROOT));
                }
                if(!document.dictionary.contains(word.toLowerCase(Locale.ROOT)))
                {
                    document.dictionary.add(word.toLowerCase(Locale.ROOT));
                }
            }
        }

        return commonWords;
    }

    List<String> findRemoveWords(Document document, List<String> commonWords) {
        List<String> remove = new ArrayList<>();
        for(String word: commonWords)
        {
            if(!document.dictionary.contains(word.toLowerCase(Locale.ROOT)))
            {
                remove.add(word);
            }
        }

        return remove;
    }

    List<String> findAllWordsOnSingleThread(Folder folder) {
        List<String> commonWords = new ArrayList<String>();
        for (Folder subFolder : folder.getSubFolders()) {
            List<String> list = findAllWordsOnSingleThread(subFolder);
            commonWords.addAll(list);
        }
        for (Document document : folder.getDocuments()) {
            List<String> list = findAllWords(document);
            commonWords.addAll(list);
        }
        commonWords = commonWords.stream().distinct().collect(Collectors.toList());
        Collections.sort(commonWords);
        return commonWords;
    }

    List<String> distinctOnSingleThread(Folder folder, List<String> commonWords) {
        List<String> remove = new ArrayList<String>();
        for (Folder subFolder : folder.getSubFolders()) {
            List<String> list = distinctOnSingleThread(subFolder, commonWords);
            remove.addAll(list);
        }
        for (Document document : folder.getDocuments()) {
            List<String> list = findRemoveWords(document, commonWords);
            remove.addAll(list);
        }

        return remove;
    }

    List<String> removeWords(List<String> commonWords, List<String> remove)
    {
        for (String r: remove)
        {
            commonWords.remove(r);
        }
        return commonWords;
    }

   class DocumentSearchTask extends RecursiveTask<List<String>> {
        private final Document document;


        DocumentSearchTask(Document document) {
            super();
            this.document = document;
        }

        @Override
        protected List<String> compute() {
            return findAllWords(document);
        }
    }

    class FolderSearchTask extends RecursiveTask<List<String>> {
        private final Folder folder;

        FolderSearchTask(Folder folder) {
            super();
            this.folder = folder;
        }

        @Override
        protected List<String> compute() {
            List<String> commonWords = new ArrayList<>();
            List<RecursiveTask<List<String>>> forks = new LinkedList<>();
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
            for (RecursiveTask<List<String>> task : forks) {
                commonWords.addAll(task.join());
            }
            commonWords = commonWords.stream().distinct().collect(Collectors.toList());
            Collections.sort(commonWords);
            return commonWords;
        }
    }

    class DocumentDistinctTask extends RecursiveTask<List<String>> {
        private final Document document;
        public List<String> commonWords;

        DocumentDistinctTask(Document document, List<String> commonWords) {
            super();
            this.document = document;
            this.commonWords = commonWords;
        }

        @Override
        protected List<String> compute() {
            return findRemoveWords(document, commonWords);
        }
    }

    class FolderDistinctTask extends RecursiveTask<List<String>> {
        private final Folder folder;
        public List<String> commonWords;

        FolderDistinctTask(Folder folder, List<String> commonWords) {
            super();
            this.folder = folder;
            this.commonWords = commonWords;
        }

        @Override
        protected List<String> compute() {
            List<String> remove = new ArrayList<>();
            List<RecursiveTask<List<String>>> forks = new LinkedList<>();
            for (Folder subFolder : folder.getSubFolders()) {
                FolderDistinctTask task = new FolderDistinctTask(subFolder, commonWords);
                forks.add(task);
                task.fork();
            }
            for (Document document : folder.getDocuments()) {
                DocumentDistinctTask task = new DocumentDistinctTask(document, commonWords);
                forks.add(task);
                task.fork();
            }
            for (RecursiveTask<List<String>> task : forks) {
                remove.addAll(task.join());
            }

            return remove;
        }
    }

    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    List<String> findAllWordsInParallel(Folder folder) {
        return forkJoinPool.invoke(new FolderSearchTask(folder));
    }

    List<String> distinctWordsInParallel(Folder folder, List<String> commonWords) {
        return forkJoinPool.invoke(new FolderDistinctTask(folder, commonWords));
    }

    public static void main(String[] args) throws IOException {
        String[] ITwords = new String[] {"information", "informatics", "computer", "computing", "technology", "technologies", "systems", "data"};
        SameWords wordCounter = new SameWords();
        Folder folder = Folder.fromDirectory(new File("C://Users/Kateryna/Desktop/копеи не для слабонервных/ПРО/PDC4"));

        final int repeatCount = Integer.decode("5");
        List<String> commonWords = new ArrayList<>();
        long startTime;
        long stopTime;

        double[] singleThreadTimes = new double[repeatCount];
        double[] forkedThreadTimes = new double[repeatCount];
        int[] wordCountSequential = new int[repeatCount];
        int[] wordCountParallel = new int[repeatCount];
        double[] speedUp = new double[repeatCount];

        commonWords = wordCounter.findAllWordsInParallel(folder);
        List<String> forRemove = wordCounter.distinctWordsInParallel(folder, commonWords);
        commonWords = wordCounter.removeWords(commonWords, forRemove);
        System.out.println("--------------------------------Common words--------------------------------");
        for(String c: commonWords)
        {
            System.out.println(c);
        }

        for (int i = 0; i < repeatCount; i++) {
            startTime = System.currentTimeMillis();
            commonWords = wordCounter.findAllWordsOnSingleThread(folder);
            List<String> remove = wordCounter.distinctOnSingleThread(folder, commonWords);
            commonWords = wordCounter.removeWords(commonWords, remove);
            stopTime = System.currentTimeMillis();
            singleThreadTimes[i] = (stopTime - startTime);
            wordCountSequential[i] = commonWords.size();
        }

        for (int i = 0; i < repeatCount; i++) {
            startTime = System.currentTimeMillis();
            commonWords = wordCounter.findAllWordsInParallel(folder);
            List<String> remove = wordCounter.distinctWordsInParallel(folder, commonWords);
            commonWords = wordCounter.removeWords(commonWords, remove);
            stopTime = System.currentTimeMillis();
            forkedThreadTimes[i] = (stopTime - startTime);
            speedUp[i] = singleThreadTimes[i] / forkedThreadTimes[i];
            wordCountParallel[i] = commonWords.size();
        }
        System.out.println("------------------------Compare time------------------------");
        System.out.printf("%-15s %-15s %-15s\n", "Sequential", "Parallel", "SpeedUp");

        for (int i = 0; i < repeatCount; i++)
        {
            speedUp[i] = (singleThreadTimes[i] / forkedThreadTimes[i]);
            System.out.printf("%-15s %-15s %-15s\n", singleThreadTimes[i], forkedThreadTimes[i], speedUp[i]);
        }
        System.out.println();
        System.out.printf("%-15s %-15s %-15s\n", Arrays.stream(singleThreadTimes).sum() / repeatCount,Arrays.stream(forkedThreadTimes).sum()/repeatCount
                ,Arrays.stream(speedUp).sum()/repeatCount);
    }
}