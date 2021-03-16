package capitan;

import mpi.MPI;
import mpi.Status;

public class Main {
    static int NRA = 62;
    static int NCA = 15;
    static int NCB = 7;
    static int MASTER = 0;
    static int FROM_MASTER = 1;
    static int FROM_WORKER = 2;

    public static void main(String[] args) throws Exception {
        int numtasks =3,
                taskid=0 ,
                numworkers ,
                source ,
                dest ,
                rows =3,
                averow ,
                extra ,
                offset=0,
                i, j, k, rc = 0;
        double[][] a = new double[NRA][NCA];
        double[][] b = new double[NCA][NCB];
        double[][] c = new double[NRA][NCB];
        Status status;
        MPI.Init(args);
        int me = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        if (numtasks < 2) {
            System.out.println("Need at least two MPI tasks. Quitting...\n");
            MPI.COMM_WORLD.Abort(rc);
            System.exit(1);
        }
        numworkers = numtasks - 1;
        if (taskid == MASTER) {
            System.out.println(String.format("mpi_mm has started with %d tasks.\n", numtasks));
            for (i = 0; i < NRA; i++)
                for (j = 0; j < NCA; j++)
                    a[i][j] = 100;
            for (i = 0; i < NCA; i++)
                for (j = 0; j < NCB; j++)
                    b[i][j] = 100;

            averow = NRA / numworkers;
            extra = NRA % numworkers;
            offset = 0;
            for (dest = 1; dest <= numworkers; dest++) {
                rows = (dest <= extra) ? averow + 1 : averow;
                System.out.println(String.format("Sending %d rows to task %d offset=% d\n", rows, dest, offset));
                MPI.COMM_WORLD.Send(offset, 0, 1, MPI.INT, dest, FROM_MASTER);
                MPI.COMM_WORLD.Send(rows, 0, 1, MPI.INT, dest, FROM_MASTER);
                MPI.COMM_WORLD.Send(a[offset][0], 0, rows * NCA, MPI.DOUBLE, dest, FROM_MASTER);
                MPI.COMM_WORLD.Send(b, 0, NCA * NCB, MPI.DOUBLE, dest, FROM_MASTER);

                offset = offset + rows;
            }
            /* Receive results from worker tasks */
            for (source = 1; source <= numworkers; source++) {
                MPI.COMM_WORLD.Recv(offset, 0, 1, MPI.INT, source, FROM_WORKER);
                MPI.COMM_WORLD.Recv(rows, 0, 1, MPI.INT, source, FROM_WORKER);
                MPI.COMM_WORLD.Recv(c[offset][0], 0, rows * NCB, MPI.DOUBLE, source, FROM_WORKER);
                System.out.println(String.format("Received results from task %d\n", source));
            }
            /* Print results */
            System.out.println("****\n");
            System.out.println("Result Matrix:\n");
            for (i = 0; i < NRA; i++) {
                System.out.println("\n");
                for (j = 0; j < NCB; j++)
                    System.out.printf("%6.2f ", c[i][j]);
            }
            System.out.print("\n********\n");
            System.out.print("Done.\n");
        }
/******** worker task *****************/
        else { /* if (taskid > MASTER) */
            MPI.COMM_WORLD.Recv(offset, 0, 1, MPI.INT, MASTER, FROM_MASTER);
            MPI.COMM_WORLD.Recv(rows, 0, 1, MPI.INT, MASTER, FROM_MASTER);
            MPI.COMM_WORLD.Recv(a, 0, rows * NCA, MPI.DOUBLE, MASTER, FROM_MASTER);
            MPI.COMM_WORLD.Recv(b, 0, NCA * NCB, MPI.DOUBLE, MASTER, FROM_MASTER);
            for (k = 0; k < NCB; k++)
                for (i = 0; i < rows; i++) {
                    c[i][k] = 0.0;
                    for (j = 0; j < NCA; j++)
                        c[i][k] = c[i][k] + a[i][j] * b[j][k];
                }
            MPI.COMM_WORLD.Send(offset, 0, 1, MPI.INT, MASTER, FROM_WORKER);
            MPI.COMM_WORLD.Send(rows, 0, 1, MPI.INT, MASTER, FROM_WORKER);
            MPI.COMM_WORLD.Send(c, 0, rows * NCB, MPI.DOUBLE, MASTER, FROM_WORKER);
        }
        MPI.Finalize();

    }
}