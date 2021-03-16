package Task2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

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


        //Model model = new Model(elementsList);
        //model.Simulate(1000.0);

            ArrayList<ArrayList<Result>> futures = new ArrayList<ArrayList<Result>>();
            ExecutorService executor = Executors.newCachedThreadPool();
            Collection<Callable<ArrayList<Result>>> collection = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Create c = new Create(2.0, "CREATOR", "Exponential");
                Process p1 = new Process(0.6, "PROCESSOR1", "Exponential", 2, 1);
                Process p2 = new Process(0.3, "PROCESSOR2", "Exponential", 2, 1);
                Process p3 = new Process(0.4, "PROCESSOR3", "Exponential", 2, 3);
                Process p4 = new Process(0.1, "PROCESSOR4", "Exponential", 2, 2);
                Process d = new Process(0, "DISPOSE", "Exponential", 0, 1);

                c.NextElement = p1;
                p1.NextProcesses = Arrays.asList(new Process[] { p2, p3, p4 }) ;
                p1.Probabilities = Arrays.asList(new Probability[]{ new Probability(0.15), new Probability(0.13), new Probability(0.3) });
                p2.NextProcesses = Arrays.asList(new Process[] { p1 });
                p3.NextProcesses = Arrays.asList(new Process[] { p1 });
                p4.NextProcesses = Arrays.asList(new Process[] { p1 });
                List<Element> elementsList = Arrays.asList(new Element[] { c, p1, p2, p3, p4 });

                Callable<ArrayList<Result>> model = new Model(elementsList);
                collection.add(model);
            }
            try {
                List<Future<ArrayList<Result>>> future = executor.invokeAll(collection);
                int i = 0;
                ArrayList<Result> average = new ArrayList<>();
                for (Future<ArrayList<Result>> r: future)
                {
                    if(r.isDone())
                    {
                        System.out.println();
                        System.out.println("------------------------Imitation "+(i+1)+"------------------------");
                        System.out.format("%20s%20s%25s", "Name", "Average queue", "Probability of Failure");
                        double probability = 0; double averageQueue = 0;
                        int j = 0;
                        for (Result res : r.get())
                        {
                            if(i == 0)
                            {
                                average.add(new Result(res.Name, res.Average, res.Probability));
                            }
                            else
                            {
                                average.get(j).Average += res.Average;
                                average.get(j).Probability += res.Probability;
                            }
                            System.out.println();
                            System.out.format("%20s%20f%25f", res.Name, res.Average, res.Probability);
                            j++;
                        }
                        i++;
                    }
                }
                System.out.println();System.out.println();
                System.out.println("                  STATISTICALLY SIGNIFICANT SCORE");
                System.out.format("%20s%20s%25s", "Name", "Average queue", "Probability of Failure");
                for (Result res: average)
                {
                    System.out.println();
                    System.out.format("%20s%20f%25f", res.Name, res.Average/future.size(), res.Probability/future.size());
                }
            }
            catch (InterruptedException | ExecutionException ex)
            {

            }

            executor.shutdown();

            long startTime;
            long stopTime;
            int repeatCount = 5;
            double[] singleThreadTimes = new double[repeatCount];
            double[] parallelThreadTimes = new double[repeatCount];
            double[] speedUp = new double[repeatCount];

        for (int j = 0; j < repeatCount; j++) {
            startTime = System.currentTimeMillis();
            ExecutorService executor1 = Executors.newCachedThreadPool();
            Collection<Callable<ArrayList<Result>>> collection1 = new ArrayList<>();
            for (int i = 0; i < repeatCount; i++) {
                Create c = new Create(2.0, "CREATOR", "Exponential");
                Process p1 = new Process(0.6, "PROCESSOR1", "Exponential", 2, 1);
                Process p2 = new Process(0.3, "PROCESSOR2", "Exponential", 2, 1);
                Process p3 = new Process(0.4, "PROCESSOR3", "Exponential", 2, 3);
                Process p4 = new Process(0.1, "PROCESSOR4", "Exponential", 2, 2);
                Process d = new Process(0, "DISPOSE", "Exponential", 0, 1);

                c.NextElement = p1;
                p1.NextProcesses = Arrays.asList(new Process[]{p2, p3, p4});
                p1.Probabilities = Arrays.asList(new Probability[]{new Probability(0.15), new Probability(0.13), new Probability(0.3)});
                p2.NextProcesses = Arrays.asList(new Process[]{p1});
                p3.NextProcesses = Arrays.asList(new Process[]{p1});
                p4.NextProcesses = Arrays.asList(new Process[]{p1});
                List<Element> elementsList = Arrays.asList(new Element[]{c, p1, p2, p3, p4});

                Callable<ArrayList<Result>> model = new Model(elementsList);
                collection1.add(model);
            }
            try {
                List<Future<ArrayList<Result>>> future = executor1.invokeAll(collection1);
                boolean finish = false;
                for (Future<ArrayList<Result>> r: future) {
                    if (r.isDone()) {
                        stopTime = System.currentTimeMillis();
                        parallelThreadTimes[j] = stopTime - startTime;
                    }
                }

            }
            catch (InterruptedException ex)
            {

            }
            executor1.shutdown();

            startTime = System.currentTimeMillis();
            for (int i = 0; i < repeatCount; i++) {
                Create c = new Create(2.0, "CREATOR", "Exponential");
                Process p1 = new Process(0.6, "PROCESSOR1", "Exponential", 2, 1);
                Process p2 = new Process(0.3, "PROCESSOR2", "Exponential", 2, 1);
                Process p3 = new Process(0.4, "PROCESSOR3", "Exponential", 2, 3);
                Process p4 = new Process(0.1, "PROCESSOR4", "Exponential", 2, 2);
                Process d = new Process(0, "DISPOSE", "Exponential", 0, 1);

                c.NextElement = p1;
                p1.NextProcesses = Arrays.asList(new Process[]{p2, p3, p4});
                p1.Probabilities = Arrays.asList(new Probability[]{new Probability(0.15), new Probability(0.13), new Probability(0.3)});
                p2.NextProcesses = Arrays.asList(new Process[]{p1});
                p3.NextProcesses = Arrays.asList(new Process[]{p1});
                p4.NextProcesses = Arrays.asList(new Process[]{p1});
                List<Element> elementsList = Arrays.asList(new Element[]{c, p1, p2, p3, p4});

                Model model = new Model(elementsList);
                model.call();
            }
            stopTime = System.currentTimeMillis();
            singleThreadTimes[j] = stopTime - startTime;
        }

        System.out.println();System.out.println();
        System.out.println("                  COMPARE TIME");
        System.out.printf("%-15s %-15s %-15s\n", "Sequential", "Parallel", "SpeedUp");

        for (int i = 0; i < repeatCount; i++)
        {
            speedUp[i] = (double)singleThreadTimes[i]/parallelThreadTimes[i];
            System.out.printf("%-15s %-15s %-15s\n", singleThreadTimes[i],parallelThreadTimes[i],speedUp[i]);
        }
        System.out.println();
        System.out.printf("%-15s %-15s %-15s\n", Arrays.stream(singleThreadTimes).sum() / repeatCount,Arrays.stream(parallelThreadTimes).sum()/repeatCount
                ,Arrays.stream(speedUp).sum()/repeatCount);

    }
}
