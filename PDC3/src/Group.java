import java.util.ArrayList;

public class Group
{
    public String name;
    public ArrayList<Student> students;
    public int total = 0;

    public Group(String name)
    {
        students = new ArrayList<Student>();
        this.name = name;
    }
}
