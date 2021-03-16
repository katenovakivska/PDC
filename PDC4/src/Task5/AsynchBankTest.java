package Task5;

import java.util.ArrayList;

public class AsynchBankTest
{
    public static final int NACCOUNTS = 3;
    public static final int INITIAL_BALANCE = 10000;
    public static void main(String[] args)
    {
        /*ArrayList<TransferThread> threads = new ArrayList<>();
        Bank b = new Bank(NACCOUNTS, INITIAL_BALANCE);
        int i;

        long startTime = System.currentTimeMillis();
        for (i = 0; i < NACCOUNTS; i++) {
            TransferThread t = new TransferThread(b, i, INITIAL_BALANCE);
            t.setPriority(Thread.NORM_PRIORITY);
            threads.add(t);
            t.start();
        }

        for (TransferThread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ex) {
            }
        }
        long stopTime = System.currentTimeMillis();
        System.out.println("Time: "+(stopTime - startTime));*/

        //Для вимірювання затримки на створення
        ArrayList<Double> transferTimes = new ArrayList<>();
        ArrayList<Double> lockTimes = new ArrayList<>();
        ArrayList<Double> unlockTimes = new ArrayList<>();
        for(int k = 0; k < 20; k++) {
            Bank b = new Bank(NACCOUNTS, INITIAL_BALANCE);
            ArrayList<TransferThread> threads = new ArrayList<>();
            for (int i = 0; i < NACCOUNTS; i++) {
                TransferThread t = new TransferThread(b, i, INITIAL_BALANCE);
                t.setPriority(Thread.NORM_PRIORITY);
                threads.add(t);
                t.start();
            }

            for (TransferThread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException ex) {
                }
            }
            //long stopTime = System.currentTimeMillis();
            //System.out.println("Time: "+(stopTime - startTime));
            double lockTime = (double) b.lockTime.stream()
                    .mapToLong(Long::valueOf)
                    .sum() / b.lockTime.size();
            double transferTime = (double) b.transferTime.stream()
                    .mapToLong(Long::valueOf)
                    .sum() / b.transferTime.size();
            lockTimes.add(lockTime);
            transferTimes.add(transferTime);
        }
        double ttime = (double) transferTimes.stream()
                .mapToDouble(Double::valueOf)
                .sum() / transferTimes.size();

        double ltime = (double) lockTimes.stream()
                .mapToDouble(Double::valueOf)
                .sum() / lockTimes.size();
        System.out.println("Avg delay on lock: "+ltime);
        System.out.println("Avg delay on transfer: "+ ttime);
    }
}
