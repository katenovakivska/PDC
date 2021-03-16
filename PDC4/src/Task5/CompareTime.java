package Task5;

import java.util.ArrayList;
import java.util.Arrays;

public class CompareTime {
    public static final int NACCOUNTS = 10;
    public static final int INITIAL_BALANCE = 10000;
    public static void main(String[] args) throws ExceptionInvalidNetStructure, ExceptionInvalidTimeDelay {
        int size = 5;
        double[] simpleParallel = new double[size];
        double[] petriParallel = new double[size];
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
        double[] modelTime = new double[size];
        for (int z = 0; z < size; z++) {
            double sum = 0;
            long sumTime = 0;
            for (int x = 0; x < 4; x++) {
                PetriObjModel model = ParallelBank.getModel(3, 500*(z+1), ltime, ttime);
                model.setIsProtokol(false);
                long startTime1 = System.currentTimeMillis();
                model.go(10000000);
                long stopTime1 = System.currentTimeMillis();
                sum += stopTime1 - startTime1;

            }
            petriParallel[z] = sum / 4;                                                                                                                             //petriParallel[z] /= 5;
            //simpleParallel[z] = (double) sumTime / 4;
        }                                                                                                                                                       //Arrays.sort(petriParallel);Arrays.sort(simpleParallel);

        System.out.printf("%-15s %-15s\n","Amount", "Petri parallel");
        for (int z = 0; z < size; z++) {
            System.out.printf("%-15f %-15f\n", (double)500*(z + 1), petriParallel[z]);
        }
    }

/*
ArrayList<TransferThread> threads = new ArrayList<>();
                Bank b = new Bank(NACCOUNTS, INITIAL_BALANCE);
                int i;
                long startTime = System.currentTimeMillis();
                for (i = 0; i < NACCOUNTS; i++) {
                    TransferThread t = new TransferThread(b, i, INITIAL_BALANCE, 300 * (z + 1));
                    t.setPriority(Thread.NORM_PRIORITY + i % 2);
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
                sumTime += stopTime - startTime;
*/
}
