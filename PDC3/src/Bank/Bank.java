package Bank;

import java.util.concurrent.locks.ReentrantLock;

class Bank
{
    public static final int NTEST = 10000;
    private final int[] accounts;
    private long ntransacts = 0;
    public  int counter = 0;
    public ReentrantLock locker = new ReentrantLock();

    public Bank(int n, int initialBalance)
    {
        accounts = new int[n];
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
        locker.lock();
        try {
            accounts[from] -= amount;
            accounts[to] += amount;
            ntransacts++;
            if (ntransacts % NTEST == 0)
                test();
        }
        finally
        {
            locker.unlock();
        }
    }
    public void test()
    {
        int sum = 0;
        for (int i = 0; i < accounts.length; i++)
            sum += accounts[i] ;
        System.out.println("Transactions:" + ntransacts + " Sum: " + sum);
        counter++;
    }
    public int size()
    {
        return accounts.length;
    }
}
