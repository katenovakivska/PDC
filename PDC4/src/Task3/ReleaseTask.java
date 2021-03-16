package Task3;

import java.util.concurrent.BlockingQueue;

public class ReleaseTask implements Runnable {
    private BlockingQueue<String> Drop;
    public Channel Channel;
    public ReleaseTask(Channel channel)
    {
        Drop = channel.Drop;
        Channel = channel;
    }

    public void run()
    {
        Channel.IsFree = true;
    }
}
