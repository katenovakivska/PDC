// PDC6.cpp : Этот файл содержит функцию "main". Здесь начинается и заканчивается выполнение программы.
//

#include "mpi.h" 
#include <stdio.h> 
#include <stdlib.h>
#define NRA 100 /* number of rows in matrix A */
#define NCA 100 /* number of columns in matrix A */ 
#define NCB 100 /* number of columns in matrix B */ 
#define MASTER 0 /* taskid of first task */
#define FROM_MASTER 1 /* setting a message type */ 
#define FROM_WORKER 2 /* setting a message type */
void MPI_NotBlocking(int argc, char* argv[]);
void MPI_Blocking(int argc, char* argv[]);
/*int main(int argc, char* argv[]) {
	
	MPI_Init(&argc, &argv);
	MPI_Blocking(argc, argv);
	MPI_Finalize();
}*/

void MPI_Blocking(int argc, char* argv[])
{
	MPI_Status status;
	int numtasks,
		taskid,
		numworkers,
		source,
		dest,
		rows, /* rows of matrix A sent to each worker */
		averow, extra, offset,
		i, j, k, rc = 0;
	double a[NRA][NCA], /* matrix A to be multiplied */
		b[NCA][NCB], /* matrix B to be multiplied */
		c[NRA][NCB]; /* result matrix C */
	MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
	MPI_Comm_rank(MPI_COMM_WORLD, &taskid);
	if (numtasks < 2) {
		printf("Need at least two MPI tasks. Quitting...\n");
		MPI_Abort(MPI_COMM_WORLD, rc);
		exit(1);
	}
	numworkers = numtasks - 1;
	if (taskid == MASTER) 
	{
		printf("mpi_mm has started with %d tasks.\n", numtasks);
		for (i = 0; i < NRA; i++)
		{
			for (j = 0; j < NCA; j++)
			{
				a[i][j] = i + 1;
			}
		}
		for (i = 0; i < NCA; i++)
		{
			for (j = 0; j < NCB; j++)
			{
				if (i == j)
				{
					b[i][j] = 1;
				}
				else
				{
					b[i][j] = 0;
				}
			}
		}

		averow = NRA / numworkers;
		extra = NRA % numworkers;
		offset = 0;
		for (dest = 1; dest <= numworkers; dest++) 
		{
			rows = (dest <= extra) ? averow + 1 : averow;
			printf("Sending %d rows to task %d offset=% d\n", rows, dest, offset);

			MPI_Send(&offset, 1, MPI_INT, dest, FROM_MASTER, MPI_COMM_WORLD);
			MPI_Send(&rows, 1, MPI_INT, dest, FROM_MASTER, MPI_COMM_WORLD);
			MPI_Send(&a[offset][0], rows * NCA, MPI_DOUBLE, dest, FROM_MASTER, MPI_COMM_WORLD);
			MPI_Send(&b, NCA * NCB, MPI_DOUBLE, dest, FROM_MASTER, MPI_COMM_WORLD);

			offset = offset + rows;
		}
		/* Receive results from worker tasks */
		for (source = 1; source <= numworkers; source++) {
			MPI_Recv(&offset, 1, MPI_INT, source, FROM_WORKER, MPI_COMM_WORLD, &status);
			MPI_Recv(&rows, 1, MPI_INT, source, FROM_WORKER, MPI_COMM_WORLD, &status);
			MPI_Recv(&c[offset][0], rows * NCB, MPI_DOUBLE, source, FROM_WORKER, MPI_COMM_WORLD,&status);

			printf("Received results from task %d\n", taskid);
		}
		/* Print results */
		printf("****\n");
		printf("Result Matrix:\n");
		for (i = 0; i < NRA; i++) 
		{
			printf("\n");
			for (j = 0; j < NCB; j++)
			{
				printf("%6.2f ", c[i][j]);
			}
		}
		printf("\n********\n");
		printf("Done.\n");
	}
	/******** worker task *****************/
	else {
		MPI_Recv(&offset, 1, MPI_INT, MASTER, FROM_MASTER, MPI_COMM_WORLD, &status);
		MPI_Recv(&rows, 1, MPI_INT, MASTER, FROM_MASTER, MPI_COMM_WORLD, &status);
		MPI_Recv(&a, rows * NCA, MPI_DOUBLE, MASTER, FROM_MASTER, MPI_COMM_WORLD, &status);
		MPI_Recv(&b, NCA * NCB, MPI_DOUBLE, MASTER, FROM_MASTER, MPI_COMM_WORLD, &status);

		for (k = 0; k < NCB; k++)
			for (i = 0; i < rows; i++) 
			{
				c[i][k] = 0.0;
				for (j = 0; j < NCA; j++)
				{
					c[i][k] = c[i][k] + a[i][j] * b[j][k];
				}
			}

		MPI_Send(&offset, 1, MPI_INT, MASTER, FROM_WORKER, MPI_COMM_WORLD);
		MPI_Send(&rows, 1, MPI_INT, MASTER, FROM_WORKER, MPI_COMM_WORLD);
		MPI_Send(&c, rows * NCB, MPI_DOUBLE, MASTER, FROM_WORKER, MPI_COMM_WORLD);
	}
}

void MPI_NotBlocking(int argc, char* argv[])
{
	MPI_Status status;
	int numtasks,
		taskid,
		numworkers,
		source,
		dest,
		rows = 0, /* rows of matrix A sent to each worker */
		averow, extra, offset,
		i, j, k, rc = 0;
	double a[NRA][NCA], /* matrix A to be multiplied */
		b[NCA][NCB], /* matrix B to be multiplied */
		c[NRA][NCB]; /* result matrix C */
	MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
	MPI_Comm_rank(MPI_COMM_WORLD, &taskid);
	if (numtasks < 2) {
		printf("Need at least two MPI tasks. Quitting...\n");
		MPI_Abort(MPI_COMM_WORLD, rc);
		exit(1);
	}
	numworkers = numtasks - 1;
	if (taskid == MASTER)
	{
		printf("mpi_mm has started with %d tasks.\n", numtasks);
		for (i = 0; i < NRA; i++)
		{
			for (j = 0; j < NCA; j++)
			{
				a[i][j] = i+1;
			}
		}
		for (i = 0; i < NCA; i++)
		{
			for (j = 0; j < NCB; j++)
			{
				if (i == j)
				{
					b[i][j] = 1;
				}
				else
				{
					b[i][j] = 0;
				}
			}
		}
		averow = NRA / numworkers;
		extra = NRA % numworkers;
		offset = 0;
		MPI_Request master_req, recv_req;
		for (dest = 1; dest <= numworkers; dest++)
		{
			rows = (dest <= extra) ? averow + 1 : averow;
			printf("Sending %d rows to task %d offset=% d\n", rows, dest, offset);

			MPI_Isend(&offset, 1, MPI_INT, dest, FROM_MASTER, MPI_COMM_WORLD, &master_req);
			MPI_Isend(&rows, 1, MPI_INT, dest, FROM_MASTER, MPI_COMM_WORLD, &master_req);
			MPI_Isend(&a[offset][0], rows * NCA, MPI_DOUBLE, dest, FROM_MASTER, MPI_COMM_WORLD, &master_req);
			MPI_Isend(&b, NCA * NCB, MPI_DOUBLE, dest, FROM_MASTER, MPI_COMM_WORLD, &master_req);
			
			offset = offset + rows;
			MPI_Waitall(numworkers - 1, &master_req, MPI_STATUS_IGNORE);

		}
		/* Receive results from worker tasks */
		for (source = 1; source <= numworkers; source++) {
			MPI_Irecv(&offset, 1, MPI_INT, source, FROM_WORKER, MPI_COMM_WORLD, &master_req);
			MPI_Irecv(&rows, 1, MPI_INT, source, FROM_WORKER, MPI_COMM_WORLD, &master_req);
			MPI_Irecv(&c[offset][0], rows * NCB, MPI_DOUBLE, source, FROM_WORKER, MPI_COMM_WORLD, &master_req);

			MPI_Waitall(numworkers - 1, &master_req, MPI_STATUS_IGNORE);
			printf("Received results from task %d\n", taskid);
		}
		
		/* Print results */
		printf("****\n");
		printf("Result Matrix:\n");
		for (i = 0; i < NRA; i++)
		{
			printf("\n");
			for (j = 0; j < NCB; j++)
			{                                                                                                                                                         c[i][j] = a[i][j];
				printf("%6.2f ", c[i][j]);
			}
		}
		printf("\n********\n");
		printf("Done.\n");
	}
	/******** worker task *****************/
	else {
		MPI_Request worker_req, recv_req;
		MPI_Irecv(&offset, 1, MPI_INT, MASTER, FROM_MASTER, MPI_COMM_WORLD, &worker_req);
		MPI_Irecv(&a, rows * NCA, MPI_DOUBLE, MASTER, FROM_MASTER, MPI_COMM_WORLD, &worker_req);
		MPI_Irecv(&b, NCA * NCB, MPI_DOUBLE, MASTER, FROM_MASTER, MPI_COMM_WORLD, &worker_req);
		MPI_Waitall(numworkers - 1, &worker_req, MPI_STATUS_IGNORE);
		for (k = 0; k < NCB; k++)
			for (i = 0; i < rows; i++)
			{
				c[i][k] = 0.0;
				for (j = 0; j < NCA; j++)
				{
					c[i][k] = c[i][k] + a[i][j] * b[j][k];
				}
			}

		MPI_Isend(&offset, 1, MPI_INT, MASTER, FROM_WORKER, MPI_COMM_WORLD, &worker_req);
		MPI_Isend(&rows, 1, MPI_INT, MASTER, FROM_WORKER, MPI_COMM_WORLD, &worker_req);
		MPI_Isend(&c, rows * NCB, MPI_DOUBLE, MASTER, FROM_WORKER, MPI_COMM_WORLD, &worker_req);

		//MPI_Waitall(numworkers - 1,&worker_req, MPI_STATUS_IGNORE);
	}
}
// Запуск программы: CTRL+F5 или меню "Отладка" > "Запуск без отладки"
// Отладка программы: F5 или меню "Отладка" > "Запустить отладку"

// Советы по началу работы 
//   1. В окне обозревателя решений можно добавлять файлы и управлять ими.
//   2. В окне Team Explorer можно подключиться к системе управления версиями.
//   3. В окне "Выходные данные" можно просматривать выходные данные сборки и другие сообщения.
//   4. В окне "Список ошибок" можно просматривать ошибки.
//   5. Последовательно выберите пункты меню "Проект" > "Добавить новый элемент", чтобы создать файлы кода, или "Проект" > "Добавить существующий элемент", чтобы добавить в проект существующие файлы кода.
//   6. Чтобы снова открыть этот проект позже, выберите пункты меню "Файл" > "Открыть" > "Проект" и выберите SLN-файл.
