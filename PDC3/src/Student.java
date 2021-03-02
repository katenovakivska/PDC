import java.util.ArrayList;

public class Student
{
    public String name;
    public ArrayList<Integer> marks;

    public Student(String name)
    {
        this.marks = new ArrayList<Integer>();
        this.name = name;
    }

    public synchronized void addMark(int mark)
    {
        marks.add(mark);
    }
}
