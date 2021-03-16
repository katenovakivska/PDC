package Task3;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class Channel
{
    public String Name;
    public double TimeOut;
    public boolean IsFree;
    public Process Process;
    public SeizeTask Seize;
    public ReleaseTask Release;
    public BlockingQueue<String> Drop;
    public Channel(String name, double timeOut, boolean isFree, Process process)
    {
        Name = name;
        TimeOut = timeOut;
        IsFree = isFree;
        Process = process;
        Drop =  new SynchronousQueue<String>();
        Seize = new SeizeTask(this);
        Release = new ReleaseTask(this);
    }
}