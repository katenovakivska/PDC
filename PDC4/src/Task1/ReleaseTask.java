package Task1;

import Task1.Channel;

import java.util.concurrent.BlockingQueue;

public class ReleaseTask implements Runnable {
    public Channel Channel;
    public boolean IsRunning;
    public ReleaseTask(Channel channel)
    {
        Channel = channel;
        IsRunning = false;
    }

    public void run()
    {
        try {
            for (String message = Channel.Drop.take(); ! message.equals("DONE"); message = Channel.Drop.take())
            {
                IsRunning = true;
            }
        } catch (InterruptedException e) {}

    }
}
