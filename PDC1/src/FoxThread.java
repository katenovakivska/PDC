import java.util.ArrayList;

public class FoxThread extends Thread
{
    public Block A;
    public Block B;
    public int rowIndex;
    public int columnIndex;
    public int poolSize;

    public FoxThread(Block A, Block B, int rowIndex, int columnIndex, int poolSize)
    {
        this.A = A;
        this.B = B;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.poolSize = poolSize;
    }

    @Override
    public void run()
    {
        try
        {
            for(int t = 0; t < poolSize; t++)
            {
                for(int i = 0; i < A.matrix.length; i++)
                {
                    for(int j = 0; j < A.matrix.length; j++)
                    {
                        var sum = Result.matrix[i + rowIndex][j + columnIndex];
                        for (int k = 0; k < A.matrix.length; k++) {
                            sum += A.matrix[i][k] * B.matrix[k][j];
                        }
                        synchronized (Result.matrix) {
                            Result.matrix[i + rowIndex][j + columnIndex] = sum;
                        }
                    }
                }
                synchronized (A)
                {
                    A.previous = A;
                    A = A.next;
                }
                synchronized (B)
                {
                    B.previous = B;
                    B = B.next;
                }
            }
            Thread.sleep(2);
        }
        catch(InterruptedException ex)
        {
            System.out.println(ex.getMessage());
        }
    }
}
