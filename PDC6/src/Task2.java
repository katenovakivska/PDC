/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdc6;

/**
 *
 * @author Kateryna
 */
import mpi.*;
public class Task2 {
    final static int NRA = 100;		     /* number of rows in matrix A */
    final static int NCA = 100;		     /* number of columns in matrix A */
    final static int NCB = 100;           /* number of columns in matrix B */
    final static int MASTER = 0;
    final static int FROM_MASTER = 1; /* setting a message type */
    final static int FROM_WORKER = 2; /* setting a message type */

    public static void main(String[] args) {
        MPI.Init(args);
        NotBlockingMultiplication(args);
        MPI.Finalize();
    }

    public static void NotBlockingMultiplication(String[] args) {
        double[][] a = new double[NRA][NCA];
        double[][] b = new double[NCA][NCB];
        double[][] c = new double[NRA][NCB];
        int[] offsetBuf = new int[1];
        int[] rowsBuf = new int[1];
        long time = 0;
        long timef=0;
        final int rank = MPI.COMM_WORLD.Rank();
        final int size = MPI.COMM_WORLD.Size();
        int numworkers = size-1;
        if (rank == MASTER) {
            for (int i=0; i<NRA; i++){
                for (int j=0; j<NCA; j++){
                    a[i][j]= i + 1;
                }
            }
            for (int i=0; i<NCA; i++){
                for (int j=0; j<NCB; j++) {
                    if(i == j)
                    {
                        b[i][j] = 1;
                    }
                    else
                    {
                        b[i][j] = 0;
                    }
                }
            }
            int averow = NRA/numworkers;
            int extra = NRA%numworkers;
            int offset = 0;
            for (int dest=1; dest<=numworkers; dest++) {
                int rows = (dest <= extra) ? averow+1 : averow;
                offsetBuf[0] = offset;
                rowsBuf[0] = rows;
                double[][] a_buf = new double[rows][NCA];
                for (int i = 0; i < rows; i++) {
                    a_buf[i] = a[offset+i];
                }
                time = System.currentTimeMillis();
                MPI.COMM_WORLD.Isend(offsetBuf,0,1,MPI.INT, dest, FROM_MASTER+1);
                MPI.COMM_WORLD.Isend(rowsBuf, 0,1, MPI.INT, dest, FROM_MASTER+2);
                MPI.COMM_WORLD.Isend(a_buf, 0, rows, MPI.OBJECT, dest, FROM_MASTER+3);
                MPI.COMM_WORLD.Isend(b, 0,NCA, MPI.OBJECT, dest, FROM_MASTER+4);
                offset = offset + rows;
            }
            /* Receive results from worker tasks */
            Request [] reqfWork=new Request[numworkers];
            for (int source=1; source<=numworkers; source++) {
                Request[] requests = new Request[2];
                requests[0] = MPI.COMM_WORLD.Irecv(offsetBuf,0, 1, MPI.INT, source, FROM_WORKER+10);
                requests[1] = MPI.COMM_WORLD.Irecv(rowsBuf, 0,1, MPI.INT, source, FROM_WORKER+11);
                Request.Waitall(requests);
                reqfWork[source-1]= MPI.COMM_WORLD.Irecv(c, offsetBuf[0],rowsBuf[0], MPI.OBJECT, source, FROM_WORKER+12);
            }
            Request.Waitall(reqfWork);
            /* Print results */
            System.out.println("****\n");
            System.out.println("Result Matrix:\n");
            for (int k = 0; k < NRA; k++)
            {
                System.out.println("\n");
                for (int t = 0; t < NCB; t++)
                    System.out.printf("%6.2f ", c[k][t]);
            }
            System.out.print("\n********\n");
            System.out.print("Done.\n");
            timef = System.currentTimeMillis();
            System.out.println("TIME nonblocking program:"+ (timef-time));

        }
        //******** worker *****************
        else {
            Request[] requests = new Request[4];
            requests[0] = MPI.COMM_WORLD.Irecv(offsetBuf, 0,1, MPI.INT, MASTER, FROM_MASTER+1);
            requests[1] = MPI.COMM_WORLD.Irecv(rowsBuf, 0,1, MPI.INT, MASTER, FROM_MASTER+2);
            requests[3] = MPI.COMM_WORLD.Irecv(b, 0, NCA, MPI.OBJECT, MASTER, FROM_MASTER+4);
            requests[1].Wait();
            a = new double[rowsBuf[0]][NCA];
            requests[2] = MPI.COMM_WORLD.Irecv(a, 0, rowsBuf[0], MPI.OBJECT, MASTER, FROM_MASTER+3);
            Request.Waitall(requests);
            for (int k=0; k<NCB; k++) {
                for (int i = 0; i < rowsBuf[0]; i++) {
                    c[i][k] = 0.0;
                    for (int j = 0; j < NCA; j++) {
                        c[i][k] = c[i][k] + a[i][j] * b[j][k];
                    }
                }
            }
            MPI.COMM_WORLD.Isend(offsetBuf, 0,1, MPI.INT, MASTER, FROM_WORKER+10);
            MPI.COMM_WORLD.Isend(rowsBuf, 0,1, MPI.INT, MASTER, FROM_WORKER+11);
            MPI.COMM_WORLD.Isend(c, 0,rowsBuf[0], MPI.OBJECT, MASTER, FROM_WORKER+12);
        }
    }

}
