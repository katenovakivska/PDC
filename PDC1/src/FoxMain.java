public class FoxMain {
    public static int SIZE = 1000;
    public static void main(String[] args) {
        double[][] m1 = new double[SIZE][SIZE];
        for(int i = 0; i < SIZE; i++)
        {
            for(int j = 0; j < SIZE; j++)
            {
                m1[i][j] = i + 1;
            }
        }
        double[][] m2 = new double[SIZE][SIZE];
        for(int i = 0; i < SIZE; i++)
        {
            for(int j = 0; j < SIZE; j++)
            {
                if(i == j) {
                    m2[i][j] = 1;
                }
            }
        }
        FoxMultiplication multiplication = new FoxMultiplication(m1, m2, 10);
        multiplication.count();
    }
}
