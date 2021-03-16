package Task1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class Channel
{
    public String Name;
    public double TimeOut;
    public boolean IsFree;
    public Process Process;
    public SeizeTask Seize;
    public ReleaseTask Release;
    public BlockingQueue<String> Drop;
    public ThreadPoolExecutor Executor;
    public double Delay;
    public Channel(String name, double timeOut, boolean isFree, Process process)
    {
        Name = name;
        TimeOut = timeOut;
        IsFree = isFree;
        Process = process;
        Drop =  new SynchronousQueue<String>();
        Executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        Seize = new SeizeTask(this);
        Release = new ReleaseTask(this);
    }
}