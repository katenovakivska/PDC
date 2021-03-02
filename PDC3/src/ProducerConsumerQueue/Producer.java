package ProducerConsumerQueue;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class Producer implements Runnable {
    private BlockingQueue<String> drop;

    public Producer(BlockingQueue<String> drop) {
        this.drop = drop;
    }

    public void run() {
        /*String importantInfo[] = {
                "Mares eat oats",
                "Does eat oats",
                "Little lambs eat ivy",
                "A kid will eat ivy too"
        };*/
        Random random = new Random();
        String importantInfo[] = new String[500];
        for (int i = 0; i < importantInfo.length; i++)
        {
            importantInfo[i] = Integer.toString(i + 1);
        }
        try {
            for (int i = 0; i < importantInfo.length; i++) {
                drop.put(importantInfo[i]);
                //Thread.sleep(random.nextInt(5000));
            }
            drop.put("DONE");
        } catch (InterruptedException e) {}
    }
}
