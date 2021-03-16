package Task3;

import java.util.concurrent.BlockingQueue;

public class SeizeTask implements Runnable
{
    private BlockingQueue<String> Drop;
    public Channel Channel;
    public SeizeTask(Channel channel)
    {
        Drop = channel.Drop;
        Channel = channel;
    }

    public void run() {
        try
        {
            double delay = Channel.Process.GetDelay();
            Channel.TimeOut = Channel.Process.TCurrent + delay;
            Channel.IsFree = false;
            //System.out.println(Channel.Name + " is busy and will be free in t = " + Channel.TimeOut);
            Thread.sleep((long) delay);
        }
        catch (InterruptedException e)
        {
        }
    }
}
