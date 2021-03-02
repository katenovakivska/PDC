package SecondTask;

import java.util.ArrayList;

public class Main {
    public static int SIZE = 1000;
    public static void main(String[] args) {
        /*double[][] matrix1 = {{1, 2, 7},{7, 8, 9}, {4, 4, 3}};
        double[][] matrix2 = {{1, 2, 6},{5, 8, 9}, {1, 4, 3}};
        ArrayList<Column> columns = new ArrayList<>();
        double[] c1 = {1, 5, 1};
        double[] c2 = {2, 8, 4};
        double[] c3 = {6, 9, 3};
        columns.add(new Column(c1, 1));
        columns.add(new Column(c2, 2));
        columns.add(new Column(c3, 3));*/
        ArrayList<Row> m1 = new ArrayList<>();
        for(int i = 0; i < SIZE; i++)
        {
            double[] a = new double[SIZE];
            for(int j = 0; j < SIZE; j++)
            {
                a[j] = i + 1;
            }
            m1.add(new Row(a, i));
        }
        ArrayList<Column> m2 = new ArrayList<>();
        for(int i = 0; i < SIZE; i++)
        {
            double[] a = new double[SIZE];
            for(int j = 0; j < a.length; j++)
            {
                if(i == j) {
                    a[i] = 1;
                }
            }
            m2.add(new Column(a, i));
        }
        Multiplication multiplication = new Multiplication(m1, m2, 100);
        multiplication.count();
    }
}
