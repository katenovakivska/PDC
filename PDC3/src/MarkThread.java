import java.util.Random;

enum TypeOfThread
{
    ASYNCHRONOUS,
    SYNCHRONIZE_THIS,
    SYNCHRONIZE_STUDENT
}
public class MarkThread extends Thread
{
    public String role;
    public Magazine magazine;
    public Random random = new Random();
    public TypeOfThread typeOfThread;

    public MarkThread(String role, Magazine magazine, TypeOfThread typeOfThread)
    {
        this.role = role;
        this.magazine = magazine;
        this.typeOfThread = typeOfThread;
    }
    @Override
    public void run() {
        for (int i = 0; i < 50; i++)
        {
            int groupIndex = random.nextInt(magazine.groups.size());
            int studentIndex = random.nextInt(magazine.groups.get(groupIndex).students.size());
            switch (typeOfThread)
            {
                case ASYNCHRONOUS:
                    magazine.asyncAddMark(/*random.nextInt(100)*/5, role, groupIndex, studentIndex);
                    break;
                case SYNCHRONIZE_STUDENT:
                    magazine.syncAddMark(/*random.nextInt(100)*/5, role, groupIndex, studentIndex);
                    break;
                case SYNCHRONIZE_THIS:
                    magazine.syncAddMark1(/*random.nextInt(100)*/5, role, groupIndex, studentIndex);
                    break;
                default:
                    break;
            }
        }
    }
}
