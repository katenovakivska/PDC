import java.util.ArrayList;

public class Main
{
    public static void main(String[] args) {
        Group is71 = new Group("IS-71");
        Group is72 = new Group("IS-72");
        Group is73 = new Group("IS-73");
        is71.students.add(new Student("Student1"));
        is71.students.add(new Student("Student2"));
        is71.students.add(new Student("Student3"));
        is71.students.add(new Student("Student4"));
        is71.students.add(new Student("Student5"));
        is71.students.add(new Student("Student6"));
        is72.students.add(new Student("Student7"));
        is72.students.add(new Student("Student8"));
        is72.students.add(new Student("Student9"));
        is72.students.add(new Student("Student10"));
        is72.students.add(new Student("Student11"));
        is72.students.add(new Student("Student12"));
        is73.students.add(new Student("Student13"));
        is73.students.add(new Student("Student14"));
        is73.students.add(new Student("Student15"));
        is73.students.add(new Student("Student16"));
        ArrayList<Group> groups = new ArrayList<>();
        groups.add(is71); groups.add(is72); groups.add(is73);
        Magazine magazine = new Magazine(groups, TypeOfMagazine.FOR_TESTING);
        /*MarkThread lector = new MarkThread("Lector", magazine, TypeOfThread.ASYNCHRONOUS);
        MarkThread assistant1 = new MarkThread("Assistant 1", magazine, TypeOfThread.ASYNCHRONOUS);
        MarkThread assistant2 = new MarkThread("Assistant 2", magazine, TypeOfThread.ASYNCHRONOUS);
        MarkThread assistant3 = new MarkThread("Assistant 3", magazine, TypeOfThread.ASYNCHRONOUS);*/
        /*MarkThread lector = new MarkThread("Lector", magazine, TypeOfThread.SYNCHRONIZE_THIS);
        MarkThread assistant1 = new MarkThread("Assistant 1", magazine, TypeOfThread.SYNCHRONIZE_THIS);
        MarkThread assistant2 = new MarkThread("Assistant 2", magazine, TypeOfThread.SYNCHRONIZE_THIS);
        MarkThread assistant3 = new MarkThread("Assistant 3", magazine, TypeOfThread.SYNCHRONIZE_THIS);*/
        MarkThread lector = new MarkThread("Lector", magazine, TypeOfThread.SYNCHRONIZE_STUDENT);
        MarkThread assistant1 = new MarkThread("Assistant 1", magazine, TypeOfThread.SYNCHRONIZE_STUDENT);
        MarkThread assistant2 = new MarkThread("Assistant 2", magazine, TypeOfThread.SYNCHRONIZE_STUDENT);
        MarkThread assistant3 = new MarkThread("Assistant 3", magazine, TypeOfThread.SYNCHRONIZE_STUDENT);
        magazine.threads.add(lector);
        magazine.threads.add(assistant1);
        magazine.threads.add(assistant2);
        magazine.threads.add(assistant3);
        magazine.startStudyingProcess();
    }
}
