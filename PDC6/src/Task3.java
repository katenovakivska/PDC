/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdc6;

import mpi.*;

/**
 *
 * @author Kateryna
 */
public class Task3 {

    final static int NRA = 100;
    /* number of rows in matrix A */
    final static int NCA = 100;
    /* number of columns in matrix A */
    final static int NCB = 100;
    /* number of columns in matrix B */
    final static int MASTER = 0;
    final static int FROM_MASTER = 1;
    /* setting a message type */
    final static int FROM_WORKER = 2;

    /* setting a message type */

    public static void main(String[] args) {
        MPI.Init(args);
        int amount = 4;
        int iterations = 4;
        int size = 400;
        int[] processes = new int[] {20, 50, 100, 200};
        double[] blockingTime = new double[amount];
        double[] notBlockingTime = new double[amount];
        for (int i = 0; i < amount; i++)
        {
            double sequential = 0;

            double[] results = BlockingMethod(args, size*(i+1));
            double[] results1 = NotBlockingMethod(args, size*(i+1));
            blockingTime[i] = results[0];
            notBlockingTime[i] = results1[0];
            sequential = Sequential(size*(i+1));

            if(blockingTime[i] != 0)
            {
                if(i == 0)
                {
                    System.out.println("---------------------------------Compare time in dependent of size---------------------------------");
                    System.out.printf("%-15s %-15s %-15s %-15s %-20s %-20s %-20s %-20s\n","Size", "Blocking", "Not blocking", "Sequential", "SpeedUp Blocking"
                            , "SpeedUp NotBlocking", "Efficiency Blocking", "Efficiency NotBlocking");
                }
                double speedUpB = sequential/blockingTime[i];
                double speedUpN = sequential/notBlockingTime[i];
                double efficiencyB = speedUpB/8;
                double efficiencyN = speedUpN/8;
                if(efficiencyB > 1)
                {
                    efficiencyB = 1;
                }
                if(efficiencyN > 1)
                {
                    efficiencyN = 1;
                }
                System.out.printf("%-15f %-15f %-15f %-15f %-20f %-20f %-20f %-20f\n", (double)size*(i+1), (double)blockingTime[i], (double)notBlockingTime[i], sequential,
                        speedUpB, speedUpN, efficiencyB, efficiencyN);
            }
        }

        MPI.Finalize();
    }

    public static double[] BlockingMethod(String[] args, int mSize) {
        double[] results = new double[2];
        double[][] a = new double[mSize][mSize];
        double[][] b = new double[mSize][mSize];
        double[][] c = new double[mSize][mSize];
        int[] offsetBuf = new int[1];
        int[] rowsBuf = new int[1];
        long startTime = 0;
        long endTime = 0;
        final int rank = MPI.COMM_WORLD.Rank();
        final int size = MPI.COMM_WORLD.Size();
        //numworkers--;
        int numworkers = size - 1;
        if (rank == MASTER) {
            for (int i = 0; i < mSize; i++) {
                for (int j = 0; j < mSize; j++) {
                    a[i][j] = i + 1;
                }
            }
            for (int i = 0; i < mSize; i++) {
                for (int j = 0; j < mSize; j++) {
                    if (i == j) {
                        b[i][j] = 1;
                    } else {
                        b[i][j] = 0;
                    }
                }
            }

            int averow = mSize / numworkers;
            int extra = mSize % numworkers;
            int offset = 0;
            for (int dest = 1; dest <= numworkers; dest++) {
                int rows;
                if (dest <= extra) {
                    rows = averow + 1;
                } else {
                    rows = averow;
                }

                offsetBuf[0] = offset;
                rowsBuf[0] = rows;
                double[][] a_buf = new double[rows][NCA];
                for (int i = 0; i < rows; i++) {
                    a_buf[i] = a[offset + i];
                }
                startTime = System.currentTimeMillis();
                MPI.COMM_WORLD.Send(offsetBuf, 0, 1, MPI.INT, dest, FROM_MASTER);
                MPI.COMM_WORLD.Send(rowsBuf, 0, 1, MPI.INT, dest, FROM_MASTER);
                MPI.COMM_WORLD.Send(a_buf, 0, rows, MPI.OBJECT, dest, FROM_MASTER);
                MPI.COMM_WORLD.Send(b, 0, mSize, MPI.OBJECT, dest, FROM_MASTER);
                offset += rows;
            }
            /* Receive results from worker tasks */
            for (int source = 1; source <= numworkers; source++) {
                MPI.COMM_WORLD.Recv(offsetBuf, 0, 1, MPI.INT, source, FROM_WORKER);
                MPI.COMM_WORLD.Recv(rowsBuf, 0, 1, MPI.INT, source, FROM_WORKER);
                MPI.COMM_WORLD.Recv(c, offsetBuf[0], rowsBuf[0], MPI.OBJECT, source, FROM_WORKER);
            }
            /* Print results */
            /*System.out.println("****\n");
	    System.out.println("Result Matrix:\n");
            for (int k = 0; k < NRA; k++)
            {
                System.out.println("\n");
                    for (int t = 0; t < NCB; t++)
                    System.out.printf("%6.2f ", c[k][t]);
            }
            System.out.print("\n********\n");
            System.out.print("Done.\n");*/
            endTime = System.currentTimeMillis();
            results[0] = (endTime - startTime);
            //System.out.println("Time for blocking multiplication:"+(timef-time));

        } //******** worker *****************
        else {
            MPI.COMM_WORLD.Recv(offsetBuf, 0, 1, MPI.INT, MASTER, FROM_MASTER);
            MPI.COMM_WORLD.Recv(rowsBuf, 0, 1, MPI.INT, MASTER, FROM_MASTER);
            a = new double[rowsBuf[0]][mSize];
            MPI.COMM_WORLD.Recv(a, 0, rowsBuf[0], MPI.OBJECT, MASTER, FROM_MASTER);
            MPI.COMM_WORLD.Recv(b, 0, mSize, MPI.OBJECT, MASTER, FROM_MASTER);
            for (int k = 0; k < mSize; k++) {
                for (int i = 0; i < rowsBuf[0]; i++) {
                    c[i][k] = 0.0;
                    for (int j = 0; j < mSize; j++) {
                        c[i][k] = c[i][k] + a[i][j] * b[j][k];
                    }
                }
            }
            MPI.COMM_WORLD.Send(offsetBuf, 0, 1, MPI.INT, MASTER, FROM_WORKER);
            MPI.COMM_WORLD.Send(rowsBuf, 0, 1, MPI.INT, MASTER, FROM_WORKER);
            MPI.COMM_WORLD.Send(c, 0, rowsBuf[0], MPI.OBJECT, MASTER, FROM_WORKER);
        }
        return results;
    }

    public static double[] NotBlockingMethod(String[] args, int mSize) {
        double[][] a = new double[mSize][mSize];
        double[][] b = new double[mSize][mSize];
        double[][] c = new double[mSize][mSize];
        double[] results = new double[2];
        int[] offsetBuf = new int[1];
        int[] rowsBuf = new int[1];
        long endTime = 0;
        long startTime = 0;
        final int rank = MPI.COMM_WORLD.Rank();
        final int size = MPI.COMM_WORLD.Size();
        int numworkers = size - 1;
        if (rank == MASTER) {
            for (int i = 0; i < mSize; i++) {
                for (int j = 0; j < mSize; j++) {
                    a[i][j] = i + 1;
                }
            }
            for (int i = 0; i < mSize; i++) {
                for (int j = 0; j < mSize; j++) {
                    if (i == j) {
                        b[i][j] = 1;
                    } else {
                        b[i][j] = 0;
                    }
                }
            }
            int averow = mSize / numworkers;
            int extra = mSize % numworkers;
            int offset = 0;
            for (int dest = 1; dest <= numworkers; dest++) {
                int rows = (dest <= extra) ? averow + 1 : averow;
                offsetBuf[0] = offset;
                rowsBuf[0] = rows;
                double[][] a_buf = new double[rows][mSize];
                for (int i = 0; i < rows; i++) {
                    a_buf[i] = a[offset + i];
                }
                startTime = System.currentTimeMillis();
                MPI.COMM_WORLD.Isend(offsetBuf, 0, 1, MPI.INT, dest, FROM_MASTER + 1);
                MPI.COMM_WORLD.Isend(rowsBuf, 0, 1, MPI.INT, dest, FROM_MASTER + 2);
                MPI.COMM_WORLD.Isend(a_buf, 0, rows, MPI.OBJECT, dest, FROM_MASTER + 3);
                MPI.COMM_WORLD.Isend(b, 0, NCA, MPI.OBJECT, dest, FROM_MASTER + 4);
                offset = offset + rows;
            }
            /* Receive results from worker tasks */
            Request[] reqfWork = new Request[numworkers];
            for (int source = 1; source <= numworkers; source++) {
                Request[] requests = new Request[2];
                requests[0] = MPI.COMM_WORLD.Irecv(offsetBuf, 0, 1, MPI.INT, source, FROM_WORKER + 10);
                requests[1] = MPI.COMM_WORLD.Irecv(rowsBuf, 0, 1, MPI.INT, source, FROM_WORKER + 11);
                Request.Waitall(requests);
                reqfWork[source - 1] = MPI.COMM_WORLD.Irecv(c, offsetBuf[0], rowsBuf[0], MPI.OBJECT, source, FROM_WORKER + 12);
            }
            Request.Waitall(reqfWork);
            /* Print results */
 /*System.out.println("****\n");
	    System.out.println("Result Matrix:\n");
            for (int k = 0; k < NRA; k++)
            {
                System.out.println("\n");
                    for (int t = 0; t < NCB; t++)
                    System.out.printf("%6.2f ", c[k][t]);
            }
            System.out.print("\n********\n");
            System.out.print("Done.\n");*/
            endTime = System.currentTimeMillis();
            results[0] = (endTime - startTime);
            //System.out.println("TIME nonblocking program:"+ (timef-time) +"ms");

        }
        //******** worker *****************
        else {
            Request[] requests = new Request[4];
            requests[0] = MPI.COMM_WORLD.Irecv(offsetBuf, 0, 1, MPI.INT, MASTER, FROM_MASTER + 1);
            requests[1] = MPI.COMM_WORLD.Irecv(rowsBuf, 0, 1, MPI.INT, MASTER, FROM_MASTER + 2);
            requests[3] = MPI.COMM_WORLD.Irecv(b, 0, mSize, MPI.OBJECT, MASTER, FROM_MASTER + 4);
            requests[1].Wait();
            a = new double[rowsBuf[0]][mSize];
            requests[2] = MPI.COMM_WORLD.Irecv(a, 0, rowsBuf[0], MPI.OBJECT, MASTER, FROM_MASTER + 3);
            Request.Waitall(requests);
            for (int k = 0; k < mSize; k++) {
                for (int i = 0; i < rowsBuf[0]; i++) {
                    c[i][k] = 0.0;
                    for (int j = 0; j < mSize; j++) {
                        c[i][k] = c[i][k] + a[i][j] * b[j][k];
                    }
                }
            }
            MPI.COMM_WORLD.Isend(offsetBuf, 0, 1, MPI.INT, MASTER, FROM_WORKER + 10);
            MPI.COMM_WORLD.Isend(rowsBuf, 0, 1, MPI.INT, MASTER, FROM_WORKER + 11);
            MPI.COMM_WORLD.Isend(c, 0, rowsBuf[0], MPI.OBJECT, MASTER, FROM_WORKER + 12);
        }
        return results;
    }
    public static double Sequential(int mSize)
    {
        double result = 0;
        double[][] a = new double[mSize][mSize];
        double[][] b = new double[mSize][mSize];
        double[][] c = new double[mSize][mSize];
        long endTime = 0;
        long startTime = 0;
        for (int i = 0; i < mSize; i++) {
            for (int j = 0; j < mSize; j++) {
                a[i][j] = i + 1;
            }
        }
        for (int i = 0; i < mSize; i++) {
            for (int j = 0; j < mSize; j++) {
                if (i == j) {
                    b[i][j] = 1;
                } else {
                    b[i][j] = 0;
                }
            }
        }
        startTime = System.currentTimeMillis();
        for(int i = 0; i < mSize; i++)
        {
            for(int j = 0; j < mSize; j++)
            {
                for (int k = 0; k < mSize; k++)
                {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        endTime = System.currentTimeMillis();
        result = (endTime - startTime);
        return result;
    }
}
