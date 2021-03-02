package SecondTask;

import java.util.ArrayList;
import java.util.Random;

public class CompareTime {
    public static int SIZE = 1000;
    public static void main(String[] args) {
        double[] sums1 = new double[6];
        double[] sums2 = new double[6];
        double[] sums3 = new double[6];
        int size = 400;
        for (int i = 1; i < 5; i++) {
            ArrayList<Row> m1 = generateMatrix1(size * i);
            ArrayList<Column> m2 = generateMatrix2(size * i);
            var sum1 = 0;
            var sum2 = 0;
            var sum3 = 0;
            for (int j = 0; j < 4; j++) {
                Multiplication multiplication = new Multiplication(m1, m2, 50);
                multiplication.count();
                sum1 += (double)(multiplication.time / 4);
                multiplication.RibbonSequential();
                sum2 += (double)(multiplication.time / 4);
                multiplication.countNormal();
                sum3 += (double)(multiplication.time / 4);
            }
            sums1[i - 1] = sum1;
            sums2[i - 1] = sum2;
            sums3[i - 1] = sum3;
        }
        System.out.println("Compare time for ForkedRibbon algo and Sequential");
        System.out.format("%32s%32s%32s%32s", "Size", "Sequential", "Ribbon Forked", "SpeedUp Ribbon");
        for (int i = 1; i < 5; i++) {
            System.out.println();
            System.out.format("%32f%32f%32f%32f", (double) (size * i), (double) sums2[i - 1], (double) sums1[i - 1], (sums2[i - 1] / sums1[i - 1]));
        }
        System.out.println();
        System.out.println("Compare time for ForkedRibbon algo and Parallel");
        System.out.format("%32s%32s%32s%32s", "Size", "Parallel", "Ribbon Forked", "SpeedUp Ribbon");
        for (int i = 1; i < 5; i++) {
            System.out.println();
            System.out.format("%32f%32f%32f%32f", (double) (size * i), (double) sums3[i - 1], (double) sums1[i - 1], (sums3[i - 1] / sums1[i - 1]));
        }
        System.out.println();
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
