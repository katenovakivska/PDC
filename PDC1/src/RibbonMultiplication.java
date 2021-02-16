import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class RibbonMultiplication {
    public ArrayList<Row> firstMatrix;
    public ArrayList<Column> secondMatrix;
    public Result result;
    public ArrayList<RibbonThread> threads = new ArrayList<>();
    public int poolSize;
    public long time;

    public RibbonMultiplication(ArrayList<Row> firstMatrix, ArrayList<Column> secondMatrix, int poolSize)
    {
        this.firstMatrix = firstMatrix;
        this.secondMatrix = secondMatrix;
        this.result = new Result(firstMatrix.size());
        this.poolSize = poolSize;
        this.time = 0;
    }
    public void count()
    {
        setNextPrevious();
        var rowsAmount = firstMatrix.size() / poolSize;
        var index = 0;
        var start = System.nanoTime();
        for(int i = 0; i < poolSize; i++)
        {
            ArrayList<Row> rows = new ArrayList<>();
            for (int j = 0; j < rowsAmount; j++)
            {
                rows.add(firstMatrix.get(index));
                index++;
            }
            threads.add(new RibbonThread(rows, secondMatrix.get(i)));
            threads.get(i).start();
        }
        try {
            for (int i = 0; i < poolSize; i++) {
                threads.get(i).join();
            }
        }
        catch (InterruptedException ex)
        {

        }
        this.time = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        //Result.printResult();
        //System.out.println("Time of work for RibbonAlgo(size: " + firstMatrix.size() + "): "+ (time));
    }

    public void setNextPrevious()
    {
        for(int i = 0; i < firstMatrix.size(); i++)
        {
            if (i != 0) {
                secondMatrix.get(i - 1).next = secondMatrix.get(i);
                secondMatrix.get(i).previous = secondMatrix.get(i - 1);
            }
        }
        secondMatrix.get(0).previous = secondMatrix.get(firstMatrix.size() - 1);
        secondMatrix.get(firstMatrix.size() - 1).next = secondMatrix.get(0);
    }

    public void RibbonSequential()
    {
        var start = System.nanoTime();
        for(int i = 0; i < firstMatrix.size(); i++)
        {
            for(int j = 0; j < firstMatrix.size(); j++)
            {
                for (int k = 0; k < firstMatrix.size(); k++)
                {
                    Result.matrix[i][j] += firstMatrix.get(i).array[k] * secondMatrix.get(k).array[j];
                }
            }
        }
        this.time = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    }
}
