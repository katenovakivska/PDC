package Task1;

import Task1.Channel;

import java.util.concurrent.BlockingQueue;

public class SeizeTask  implements Runnable
{
    public Channel Channel;
    public boolean IsRunning;

    public SeizeTask(Channel channel)
    {
        Channel = channel;
        IsRunning = false;
    }

    public void run() {
        try
        {
            while (Channel.TimeOut < Channel.Process.TimeOfModeling) {
                double delay = Channel.Delay;
                Channel.Drop.put("in channel");
                IsRunning = true;
                Thread.sleep((long) delay);
            }
            if(Channel.TimeOut > Channel.Process.TimeOfModeling)
            {
                Channel.Drop.put("DONE");
            }
        }
        catch (InterruptedException e)
        {
        }
    }
}
