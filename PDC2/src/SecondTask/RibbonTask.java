package SecondTask;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class RibbonTask extends RecursiveTask<double[][]> {
    public Column column;
    public ArrayList<Row> rows = new ArrayList<>();
    public ArrayList<Row> starts = new ArrayList<>();
    public Row row;
    public int size;
    public int poolSize;

    public RibbonTask(Row row, Column column, int size, int poolSize)
    {
        this.column = column;
        this.row = row;
        this.size = size;
        this.poolSize = poolSize;
        int amount = row.array.length / poolSize;
        for (int j = 0; j < amount; j++)
        {
            if(j == 0)
                rows.add(row);
            else
            {
                rows.add(rows.get(j - 1).next);
            }
        }
    }
    public RibbonTask(ArrayList<Row> starts, Row row, Column column, int size, int poolSize)
    {
        this.column = column;
        this.row = row;
        this.size = size;
        this.poolSize = poolSize;
        this.starts = starts;
        int amount = row.array.length / poolSize;
        for (int j = 0; j < amount; j++)
        {
            if(j == 0)
                rows.add(row);
            else
            {
                rows.add(rows.get(j - 1).next);
            }
        }
    }
    @Override
    protected double[][] compute() {
        for(int j = 0; j < rows.size(); j++) {
            for (int i = 0; i < rows.get(0).array.length; i++) {
                var sum = 0;
                for (int k = 0; k < column.array.length; k++) {
                    sum += (rows.get(j).array[k] * column.array[k]);
                }
                Result.matrix[rows.get(j).index][column.index] = sum;

                column.next = column;
                column = column.previous;
            }
        }
        List<RecursiveTask<double[][]>> forks = new LinkedList<>();
            for (int i = 0; i < poolSize; i++) {
                RibbonTask task = new RibbonTask(starts.get(i).next, column, size, poolSize);
                forks.add(task);
                task.fork();
            }

        return Result.matrix;
    }
}
