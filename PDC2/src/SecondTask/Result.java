package SecondTask;

public class Result {
    public static double[][] matrix;

    public Result(int size)
    {
        this.matrix = new double[size][size];
    }

    public static void printResult()
    {
        for(int i = 0; i < matrix.length; i++)
        {
            for(int j = 0; j < matrix[0].length; j++)
            {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.print("\n");
        }
    }
}
