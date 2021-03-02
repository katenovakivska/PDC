import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

enum TypeOfMagazine
{
    FOR_COMPARE,
    FOR_TESTING
}
public class Magazine
{
    private int sumOfMarks = 0;
    public ArrayList<Group> groups;
    public ArrayList<MarkThread> threads;
    public long time;
    public TypeOfMagazine typeOfMagazine;

    public Magazine(ArrayList<Group> groups, TypeOfMagazine typeOfMagazine)
    {
        threads = new ArrayList<MarkThread>();
        this.groups = groups;
        this.typeOfMagazine = typeOfMagazine;
        this.time = 0;
    }

    public void startStudyingProcess()
    {
        var start = System.nanoTime();
        for (int i = 0; i < threads.size(); i++)
        {
            threads.get(i).start();
        }
        try
        {
            for (int i = 0; i < threads.size(); i++)
            {
                threads.get(i).join();
            }
        }
        catch (InterruptedException ex)
        {
        }
        this.time = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        if(typeOfMagazine == TypeOfMagazine.FOR_TESTING)
        {
            System.out.println("Total: " + sumOfMarks);
            for (Group g : groups) {
                System.out.println(g.name + " Total: " + g.total);
                for (Student s : g.students) {
                    System.out.print(s.name + ": ");
                    if (s.marks != null) {
                        for (Integer m : s.marks) {
                            System.out.print(m + " ");
                        }
                    }
                    System.out.print("\n");
                }
            }
            System.out.println("Time: " + time);
        }
    }
    public void syncAddMark(int mark, String role, int group, int student)
    {

            synchronized (groups.get(group).students.get(student))
            {
                groups.get(group).students.get(student).marks.add(mark);
            }

            synchronized (groups.get(group))
            {
                groups.get(group).total += mark;
            }

            synchronized (this)
            {
                sumOfMarks += mark;
                if(typeOfMagazine == TypeOfMagazine.FOR_TESTING)
                {
                    System.out.println("Total: " + sumOfMarks + " " + role + ": " + mark + " " + groups.get(group).students.get(student).name
                            + ": " + groups.get(group).students.get(student).marks.stream().collect(Collectors.summingInt(Integer::intValue)));
                }
            }

    }
    public void syncAddMark1(int mark, String role, int group, int student)
    {
        synchronized (this)
        {
            groups.get(group).students.get(student).marks.add(mark);
            groups.get(group).total += mark;
            sumOfMarks += mark;
            if(typeOfMagazine == TypeOfMagazine.FOR_TESTING)
            {
                System.out.println("Total: " + sumOfMarks + " " + role + ": " + mark + " " + groups.get(group).students.get(student).name
                        + ": " + groups.get(group).students.get(student).marks.stream().collect(Collectors.summingInt(Integer::intValue)));
            }
        }
    }
    public void asyncAddMark(int mark, String role, int group, int student)
    {
        sumOfMarks += mark;
        groups.get(group).students.get(student).marks.add(mark);
        groups.get(group).total += mark;
        if(typeOfMagazine == TypeOfMagazine.FOR_TESTING)
        {
            System.out.println("Total: " + sumOfMarks + " " + role + ": " + mark + " " + groups.get(group).students.get(student).name
                    + ": " + groups.get(group).students.get(student).marks.stream().collect(Collectors.summingInt(Integer::intValue)));
        }
    }
    public void startComparingProcess()
    {
        for (int j = 0; j < 4; j++)
        {
            var start = System.nanoTime();
            for (int i = 0; i < threads.size(); i++)
            {
                threads.get(i).start();
            }
            try
            {
                for (int i = 0; i < threads.size(); i++)
                {
                    threads.get(i).join();
                }
            }
            catch (InterruptedException ex)
            {
            }
            this.time += TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        }
        time /= 4;
    }
}
