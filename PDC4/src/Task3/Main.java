package Task3;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args)
    {
        //Task1.Create c = new Task1.Create(3.0, "CREATOR", "Exponential");
        //Task1.Process p1 = new Task1.Process(2.0, "PROCESSOR1", "Exponential", 2, 3);
        //Task1.Process p3 = new Task1.Process(2.0, "PROCESSOR3", "Exponential", 2, 3);
        //Task1.Process p4 = new Task1.Process(2.0, "PROCESSOR4", "Exponential", 2, 3);
        //Task1.Process p2 = new Task1.Process(2.0, "PROCESSOR2", "Exponential", 2, 3);
        //Task1.Process d = new Task1.Process(1.0, "DISPOSE", "", 0, 0);

        //c.NextElement = p1;

        //Task1.Process[] processes = new Task1.Process[] { p1, p2, p3, p4 };
        //List<Task1.Element> list = new List<Task1.Element>() { c, p1, p2, p3, p4, d };

        //p1.NextProcesses.Add(p2);
        //p1.NextProcesses.Add(p3);
        //p2.NextProcesses.Add(d);
        //p3.NextProcesses.Add(p4);
        //p4.NextProcesses.Add(p1);
        //p4.NextProcesses.Add(d);
        //p1.Probabilities = new List<Task1.Probability> { { new Task1.Probability(1, 0.4) }, { new Task1.Probability(2, 0.6) } };
        //p4.Probabilities = new List<Task1.Probability> { { new Task1.Probability(1, 0.2) }, { new Task1.Probability(2, 0.8) } };
        //Task1.Model model = new Task1.Model(list);
        //model.Simulate(1000.0);
        double timeModeling = 1000;
        Create c = new Create(2.0, "CREATOR", "Exponential");
        Process p1 = new Process(0.6, "PROCESSOR1", "Exponential", 2, 1, timeModeling);
        Process p2 = new Process(0.3, "PROCESSOR2", "Exponential", 2, 1, timeModeling);
        Process p3 = new Process(0.4, "PROCESSOR3", "Exponential", 2, 3, timeModeling);
        Process p4 = new Process(0.1, "PROCESSOR4", "Exponential", 2, 2, timeModeling);
        Process d = new Process(0, "DISPOSE", "Exponential", 0, 1, timeModeling);

        c.NextElement = p1;
        p1.NextProcesses = Arrays.asList(new Process[] { p2, p3, p4 }) ;
        p1.Probabilities = Arrays.asList(new Probability[]{ new Probability(0.15), new Probability(0.13), new Probability(0.3) });
        p2.NextProcesses = Arrays.asList(new Process[] { p1 });
        p3.NextProcesses = Arrays.asList(new Process[] { p1 });
        p4.NextProcesses = Arrays.asList(new Process[] { p1 });
        List<Element> elementsList = Arrays.asList(new Element[] { c, p1, p2, p3, p4 });
        Model model = new Model(elementsList);
        model.Simulate(timeModeling);
        List<Result> res = model.ReturnResult();                                                                                                                             System.exit(1);
    }
}
