import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CompareTimеSizes {
    public static void main(String[] args) {
        double[] sums1 = new double[6];
        double[] sums2 = new double[6];
        int size = 400;
        int[] processors = new int[]{8, 10};
        for(int k = 0; k < processors.length; k++) {
            for (int i = 1; i < 5; i++) {
                ArrayList<Row> m1 = generateMatrix1(size * i);
                ArrayList<Column> m2 = generateMatrix2(size * i);
                var sum1 = 0;
                var sum2 = 0;
                for (int j = 0; j < 4; j++) {
                    RibbonMultiplication multiplication = new RibbonMultiplication(m1, m2, processors[k]);
                    multiplication.count();
                    sum1 += (double)(multiplication.time / 4);
                    multiplication.RibbonSequential();
                    sum2 += (double)(multiplication.time / 4);
                }
                sums1[i - 1] = sum1;
                sums2[i - 1] = sum2;
            }
            System.out.println("Compare time for Ribbon algo for "+processors[k]+" processors");
            System.out.format("%32s%32s%32s%32s", "Size", "Sequential", "Ribbon Parallel", "SpeedUp Ribbon");
            for (int i = 1; i < 5; i++) {
                System.out.println();
                System.out.format("%32f%32f%32f%32f", (double) (size * i), (double) sums2[i - 1], (double) sums1[i - 1], (double) (sums2[i - 1] / sums1[i - 1]));
            }
            System.out.println();
        }
        for(int k = 0; k < processors.length; k++) {
            for (int i = 1; i < 5; i++) {
                double[][] m1 = getMatrix(size * i);
                double[][] m2 = getMatrix(size * i);
                var sum1 = 0;
                var sum2 = 0;
                for (int j = 0; j < 4; j++) {
                    FoxMultiplication multiplication = new FoxMultiplication(m1, m2, processors[k]);
                    multiplication.count();
                    sum1 += (double)(multiplication.time / 4);
                    multiplication = new FoxMultiplication(m1, m2, processors[k]);
                    multiplication.FoxSequential();
                    sum2 += (double)(multiplication.time / 4);
                }
                sums1[i - 1] = sum1;
                sums2[i - 1] = sum2;
            }
            System.out.println("Compare time for Fox algo for "+processors[k]+" processors");
            System.out.format("%32s%32s%32s%32s", "Size", "Sequential", "Fox Parallel", "SpeedUp Fox");
            for (int i = 1; i < 5; i++) {
                System.out.println();
                System.out.format("%32f%32f%32f%32f", (double) (size * i), (double) sums2[i - 1], (double) sums1[i - 1], (double) (sums2[i - 1] / sums1[i - 1]));
            }
            System.out.println();
        }
    }

    public static ArrayList<Row> generateMatrix1(int size)
    {
        Random r = new Random();
        int rangeMin = 10;
        int rangeMax = 100;
        ArrayList<Row> m1 = new ArrayList<>();
        for(int i = 0; i < size; i++)
        {
            double[] a = new double[size];
            for(int j = 0; j < size; j++)
            {
                a[j] = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
            }
            m1.add(new Row(a, i));
        }
        return m1;
    }

    public static ArrayList<Column> generateMatrix2(int size)
    {
        Random r = new Random();
        int rangeMin = 10;
        int rangeMax = 100;
        ArrayList<Column> m2 = new ArrayList<>();
        for(int i = 0; i < size; i++)
        {
            double[] a = new double[size];
            for(int j = 0; j < a.length; j++)
            {
                if(i == j) {
                    a[i] = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
                }
            }
            m2.add(new Column(a, i));
        }
        return m2;
    }

    public static double[][] getMatrix(int size)
    {
        Random r = new Random();
        int rangeMin = 10;
        int rangeMax = 100;
        double[][] m1 = new double[size][size];
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                m1[i][j] = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
            }
        }
        return m1;
    }
}
