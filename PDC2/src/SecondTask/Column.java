package SecondTask;

public class Column {
    public double[] array;
    public Column next;
    public Column previous;
    public int index;

    public Column(double[] array, int index)
    {
        this.array = array;
        this.index = index;
    }
}