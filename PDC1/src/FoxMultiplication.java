import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class FoxMultiplication {
    public double[][] firstMatrix;
    public double[][] secondMatrix;
    public Result result;
    public ArrayList<FoxThread> threads = new ArrayList<>();
    public Block[][] blocksA;
    public Block[][] blocksB;
    public int poolSize;
    public long time;

    public FoxMultiplication(double[][] firstMatrix, double[][] secondMatrix, int poolSize)
    {
        this.firstMatrix = firstMatrix;
        this.secondMatrix = secondMatrix;
        this.result = new Result(firstMatrix.length);
        this.poolSize = poolSize;
        blocksA = new Block[poolSize][poolSize];
        blocksB = new Block[poolSize][poolSize];
    }
    public void count()
    {
        var blockSize = firstMatrix.length / poolSize;
        var amountInRow = firstMatrix.length / blockSize;
        createBlocks(blockSize, amountInRow);
        //setNextPrevious(amountInRow);
        var count = 0;
        var start = System.nanoTime();
        for(int i = 0; i < amountInRow; i++)
        {
            for (int j = 0; j < amountInRow; j++)
            {
                threads.add(new FoxThread(blocksA[i][j], blocksB[i][j], i * blockSize, j * blockSize, poolSize));
                threads.get(count).start();
                count++;
            }
        }
        try {
            for (Thread thread : threads)
            {
                thread.join();
            }
        }
        catch (InterruptedException ex)
        {

        }
        this.time = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        //Result.printResult();
        //System.out.println("Time of work for FoxAlgo(size: " + firstMatrix.length + "): "+ this.time);
    }

    public void createBlocks(int blockSize, int amountInRow)
    {
        var row = 0;
        var count = 0;
        for(int i = 0; i < amountInRow; i++)
        {
            var column = 0;
            for(int j = 0; j < amountInRow; j++)
            {
                double[][] matrixA = new double[blockSize][blockSize];
                double[][] matrixB = new double[blockSize][blockSize];
                for (int x = 0; x < blockSize; x++)
                {
                    for (int y = 0; y < blockSize; y++)
                    {
                        matrixA[x][y] = firstMatrix[row + x][column + y];
                        matrixB[x][y] = secondMatrix[row + x][column + y];
                    }
                }
                column += blockSize;
                blocksA[i][j] = new Block(matrixA);
                blocksB[i][j] = new Block(matrixB);
                setNextPrevious(i, j, amountInRow);
            }
            row += blockSize;
        }
    }

    public void setNextPrevious(int i, int j, int amountInRow)
    {
        if (i != 0)
        {
            blocksB[i - 1][j].next = blocksB[i][j];
            blocksB[i][j].previous = blocksB[i - 1][j];
        }
        if(i == amountInRow - 1)
        {
            blocksB[0][j].previous = blocksB[i][j];
            blocksB[i][j].next = blocksB[0][j];
        }
        if (j != 0)
        {
            blocksA[i][j - 1].next = blocksA[i][j];
            blocksA[i][j].previous = blocksA[i][j - 1];
        }
        if(j == amountInRow - 1)
        {
            blocksA[i][0].previous = blocksA[i][j];
            blocksA[i][j].next = blocksA[i][0];
        }
    }

    public void FoxSequential()
    {
        var start = System.nanoTime();
        for(int i = 0; i < firstMatrix.length; i++)
        {
            for(int j = 0; j < firstMatrix.length; j++)
            {
                for (int k = 0; k < firstMatrix.length; k++)
                {
                    Result.matrix[i][j] += firstMatrix[i][k] * secondMatrix[k][j];
                }
            }
        }
        this.time = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    }
}
