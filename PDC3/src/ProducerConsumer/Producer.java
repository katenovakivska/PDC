package ProducerConsumer;

import ProducerConsumer.Drop;

import java.util.Random;

public class Producer implements Runnable
{
    private Drop drop;

    public Producer(Drop drop)
    {
        this.drop = drop;
    }

    public void run()
    {
        Random random = new Random();
        /*String importantInfo[] = {
                "Mares eat oats",
                "Does eat oats",
                "Little lambs eat ivy",
                "A kid will eat ivy too"
        };*/
        String importantInfo[] = new String[500];
        for (int i = 0; i < importantInfo.length; i++)
        {
            importantInfo[i] = Integer.toString(i + 1);
        }

        for (int i = 0; i < importantInfo.length; i++)
        {
            drop.put(importantInfo[i]);
            try
            {
                Thread.sleep(random.nextInt(5000));
            }
            catch (InterruptedException e)
            {
            }
        }
        drop.put("DONE");
    }
}

