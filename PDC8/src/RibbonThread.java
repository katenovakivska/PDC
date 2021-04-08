import java.util.ArrayList;

public class RibbonThread extends Thread {
    public Column column;
    public ArrayList<Row> rows;

    public RibbonThread(ArrayList<Row> rows, Column column)
    {
        this.column = column;
        this.rows = rows;
    }

    @Override
    public void run()
    {
            for(int j = 0; j < rows.size(); j++) {
                for (int i = 0; i < rows.get(0).array.length; i++) {
                    var sum = 0;
                    for (int k = 0; k < column.array.length; k++) {
                        sum += (rows.get(j).array[k] * column.array[k]);
                    }
                    synchronized (Result.matrix) {
                        Result.matrix[rows.get(j).index][column.index] = sum;
                    }
                   /* synchronized (column) {*/
                        column.next = column;
                        column = column.previous;
                   /* }*/
                }
            }

    }


}
