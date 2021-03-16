package Task5;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

class Bank
{
    public static final int NTEST = 10000;
    private final int[] accounts;
    public long ntransacts = 0;
    public ReentrantLock locker = new ReentrantLock();
    public List<Long> transferTime;
    public List<Long> unlockTime;
    public List<Long> lockTime;

    public Bank(int n, int initialBalance)
    {
        accounts = new int[n];
        transferTime = new ArrayList<>();
        unlockTime = new ArrayList<>();
        lockTime = new ArrayList<>();
        int i;
        for (i = 0; i < accounts.length; i++)
            accounts[i] = initialBalance;
        ntransacts = 0;
    }
    public void transfer(int from, int to, int amount) throws InterruptedException
    {
        accounts[from] -= amount;
        accounts[to] += amount;
        ntransacts++;
        if (ntransacts % NTEST == 0)
            test();
    }
    public synchronized void transfer1(int from, int to, int amount) throws InterruptedException
    {
        while (accounts[from] < amount)
            wait();
        accounts[from] -= amount;
        accounts[to] += amount;
        ntransacts++;
        notifyAll() ;
        if (ntransacts % NTEST == 0)
            test();

    }
    public void transfer2(int from, int to, int amount) throws InterruptedException
    {
        synchronized (accounts) {
            accounts[from] -= amount;
            accounts[to] += amount;
            ntransacts++;
            if (ntransacts % NTEST == 0)
                test();
        }
    }

    public synchronized void transfer3(int from, int to, int amount) throws InterruptedException
    {
        accounts[from] -= amount;
        accounts[to] += amount;
        ntransacts++;
        if (ntransacts % NTEST == 0)
            test();
    }
    public void transfer4(int from, int to, int amount) throws InterruptedException
    {
        try {
            long startTime = System.currentTimeMillis();
            boolean flag = locker.tryLock(100, TimeUnit.MILLISECONDS);
            lockTime.add(System.currentTimeMillis() - startTime);
            if (flag) {
                try {

                    accounts[from] -= amount;
                    accounts[to] += amount;
                    ntransacts++;
                    if (ntransacts % NTEST == 0)
                        test();
                } finally {
                    locker.unlock();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void test()
    {
        int sum = 0;
        for (int i = 0; i < accounts.length; i++)
            sum += accounts[i] ;
        //System.out.println("Transactions:" + ntransacts + " Sum: " + sum);
    }
    public int size()
    {
        return accounts.length;
    }
    public synchronized void addTime(long time)
    {
        transferTime.add(time);
    }
}
