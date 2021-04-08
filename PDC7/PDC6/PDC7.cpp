#include "PDC7.h"
#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <iostream>
#include <iomanip>
#include <chrono>
using namespace std;
using namespace std::chrono;
#pragma warning(disable : 4996)
#pragma warning(disable : 4703)

void CollectiveMultiplication(int n, char* argv[]);
//аааааааааа памагити € так больше не могу
int main(int argc, char* argv[])
{
    MPI_Init(&argc, &argv);
    //double s = MPI_Wtime();
    CollectiveMultiplication(200, argv);
    //double e = MPI_Wtime();
    //cout << (e - s);
    MPI_Finalize();
    
    return 0;
}

void CollectiveMultiplication(int n, char* argv[])
{
    int processID, amountOfProcesses;

    int* sendA = 0, * sendB = 0, * resultC = 0, * receiveA = 0, * receiveC = 0;

    MPI_Comm_size(MPI_COMM_WORLD, &amountOfProcesses);
    MPI_Comm_rank(MPI_COMM_WORLD, &processID);

    if (processID == 0) {

        FILE* conditionFile = fopen("C:/Users/Kateryna/source/repos/PDC6/PDC6/data.txt", "w");

        sendA = (int*)malloc(n * n * sizeof(int*));
        sendB = (int*)malloc(n * n * sizeof(int*));
        resultC = (int*)malloc(n * n * sizeof(int*));
        fprintf(conditionFile, "%d", n);
        fprintf(conditionFile, "\n");
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                fprintf(conditionFile, " %d ", i + 1);
            }
            fprintf(conditionFile, "\n");
        }
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (i == j)
                {
                    fprintf(conditionFile, " %d ", 1);
                }
                else
                {
                    fprintf(conditionFile, " %d ", 0);
                }
            }
            fprintf(conditionFile, "\n");
        }
        fclose(conditionFile);
        conditionFile = fopen("C:/Users/Kateryna/source/repos/PDC6/PDC6/data.txt", "r");

        for (int r = 0; r < n; r++)
        {
            for (int c = 0; c < n; c++)
            {
                fscanf(conditionFile, "%d", &sendA[r * n + c]);
            }
        }
        for (int r = 0; r < n; r++)
        {
            for (int c = 0; c < n; c++)
            {
                fscanf(conditionFile, "%d", &sendB[r * n + c]);
            }
        }
        fclose(conditionFile);
    }
    //бродкаст сообщени€ из процесса мастер всем другим с соблюдением синхронизации
    MPI_Barrier(MPI_COMM_WORLD);

    //процес мастер делает бродкаст сообщени€ n всем остальным процессам, всем одинаково (коммунизм)
    MPI_Bcast(&n, 1, MPI_INT, 0, MPI_COMM_WORLD);

    receiveA = (int*)malloc(n / amountOfProcesses * n * sizeof(int*));
    if (sendB == NULL)
    {
        sendB = (int*)malloc(n * n * sizeof(int*));
    }
    receiveC = (int*)malloc(n / amountOfProcesses * n * sizeof(int*));

    //процесс мастер кидает матрицу sendA всем остальным процессам, как кн€гин€ ќльга голубей в  оростене
    MPI_Scatter(sendA, n * n / amountOfProcesses, MPI_INT, receiveA, n * n / amountOfProcesses, MPI_INT, 0, MPI_COMM_WORLD);

    //процес мастер делает бродкаст сообщени€ sendB всем остальным процессам, всем одинаково (коммунизм)
    MPI_Bcast(sendB, n * n, MPI_INT, 0, MPI_COMM_WORLD);

    //все процессы это выполн€ют (умножение)
    for (int i = 0; i < (n / amountOfProcesses); i++)
    {
        for (int j = 0; j < n; j++)
        {
            receiveC[i * n + j] = 0;
            for (int k = 0; k < n; k++)
            {
                receiveC[i * n + j] += receiveA[i * n + k] * sendB[k * n + j];
            }
        }
    }

    //процесс мастер собирает инфу от других процессов, как государство налоги у фопов
    MPI_Gather(receiveC, n * n / amountOfProcesses, MPI_INT, resultC, n * n / amountOfProcesses, MPI_INT, 0, MPI_COMM_WORLD);

    //бродкаст сообщени€ из процесса мастер всем другим с соблюдением синхронизации
    MPI_Barrier(MPI_COMM_WORLD);
    
    if (processID == 0) {

        FILE* out = fopen("C:/Users/Kateryna/source/repos/PDC6/PDC6/matrix_result.txt", "w");

        for (int i = 0; i < n; i++)
        {
            for (int j = 2; j < n; j++)
            {
                fprintf(out, " %d ", resultC[i * n + j]);
            }
            fprintf(out, "\n");
        }
        fclose(out);
    }
}
