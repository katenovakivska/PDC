package Task5;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TransferThread extends Thread
{
    private Bank bank;
    private int fromAccount;
    private int maxAmount;
    private static final int REPS = 100;
    public ReentrantLock locker = new ReentrantLock();
    public long startTime;
    public long stopTime;
    public int amount;
    public TransferThread(Bank b, int from, int max)
    {
        bank = b;
        fromAccount = from;
        maxAmount = max;
        amount = REPS;
    }
    public TransferThread(Bank b, int from, int max, int amount)
    {
        bank = b;
        fromAccount = from;
        maxAmount = max;
        this.amount = amount;
    }
    public void run()
    {
        try
        {
                for (int i = 0; i < amount; i++)
                {
                    int toAccount = (int)(bank.size()*Math.random());
                    int amount = (int)(maxAmount * Math.random()/this.amount);
                    startTime = System.currentTimeMillis();
                    bank.transfer4(fromAccount, toAccount, amount);
                    //Thread.sleep(1);
                    stopTime = System.currentTimeMillis();
                    bank.addTime(stopTime - startTime);
                }
        }
        catch(InterruptedException e)
        {
        }
    }
}
