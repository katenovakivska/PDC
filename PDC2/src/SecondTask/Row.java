package SecondTask;

public class Row {
    public double[] array;
    public int index;
    public Row next;
    public Row previous;

    public Row(double[] array, int index)
    {
        this.array = array;
        this.index = index;
    }
}
