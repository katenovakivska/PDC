package Task3;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Model
{
    public List<Element> list = new ArrayList<Element>();
    public double TNext;
    public double TCurrent;
    public int Event;
    public int prev;
    public List<Element> sortedList = new ArrayList<Element>();

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);


    public Model(List<Element> elements)
    {
        list = elements;
        TNext = 0.00;
        Event = 0;
        TCurrent = TNext;
    }

    public void Simulate(double timeModeling)
    {
        prev = -1;
        System.out.printf("%-20s %-20s %-20s %-20s\n", "Element", "Time", "Average queue", "Failure probability");
        while (TCurrent < timeModeling)
        {
            TNext = Double.MAX_VALUE;

            for (Element e: list)
            {
                if (e.TNext < TNext)
                {
                    TNext = e.TNext;
                    Event = list.indexOf(e);
                }
            }

            executor.schedule(() -> {
                double averageQueue = 0 ; double probabilityFailure = 0; int counter = 0;
                for (var e : list) {
                    if (e.getClass() == Process.class) {
                        averageQueue += ((Process) e).AverageQueue;
                        probabilityFailure += ((Process) e).Failure;
                        counter += ((Process) e).Failure + e.Quantity;
                    }
                }
                System.out.printf("%-20s %-20f %-20f %-20f\n", list.get(Event).Name,TCurrent,(averageQueue/TCurrent),(probabilityFailure/counter));
            }, 15, TimeUnit.NANOSECONDS);

            ManageChannels();

            for (Element e: list)
            {
                e.CountStatistics(TNext - TCurrent);
            }
            TCurrent = TNext;

            for (Element e: list)
            {
                e.TCurrent = TCurrent;
            }
            list.get(Event).OutAct();

            for (Element e: list)
            {
                if (e.TNext == TCurrent)
                {
                    e.OutAct();
                }
            }
            prev = Event;
            if(TCurrent > timeModeling)
            {
                for(int i = 1; i < list.size(); i++)
                {
                    Process process = (Process) list.get(i);
                    process.Executor.shutdown();
                }
            }
        }
    }

    public void ManageChannels()
    {
        List<Channel> channels = new ArrayList<Channel>();
        List<Channel> outChannels = new ArrayList<Channel>();
        if (list.get(Event).getClass() == Process.class && list.get(Event).Name != "DISPOSE")
        {
            Process p = (Process)list.get(Event);
            p.InChannel();
        }
        for (var e : list)
        {
            if (e.getClass() == Process.class && e.Name != "DISPOSE")
            {
                Process p = (Process)e;
                outChannels = p.OutChannel(TNext);
                for (var o: outChannels)
                {
                    channels.add(o);
                }

            }
        }
        channels.sort(Comparator.comparingDouble(object->object.TimeOut));;
        /*for (var c: channels)
        {
            System.out.println(c.Name+" is free now t = "+c.TimeOut);
        }*/
    }
    public void PrintInfo()
    {
        for (Element e: list)
        {
            e.PrintInfo();
        }
    }

    public void PrintResult()
    {
        System.out.println("\n-------------RESULTS-------------");
        for (Element e: list)
        {
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            e.PrintResult();
            if (e.getClass() == Process.class && e.Name != "DISPOSE")
            {
                Process p = (Process)e;
                p.AverageQueueTime = p.AverageQueue / p.Quantity;
                p.AverageQueue /= TCurrent;
                p.AverageProcessingTime /= p.Quantity;
                p.ProbabilityFailure = p.Failure / (double)(p.Quantity + p.Failure);
                p.AverageWorkload /= TCurrent;
                //Console.WriteLine($"Delay = {p.AverageDelay} QLength = {p.MaxQueueObserved} MaxParallel = {p.MaxParallel} AvgQLength = {p.AverageQueue} " +
                //$"MaxQLength = {p.MaxQueueObserved} AvgWorkload = {p.AverageWorkload} MaxWorkload = {p.MaxWorkload} AvgProcTime = {p.AverageProcessingTime}" +
                //$" Failure = {p.Failure} PFailure = {p.ProbabilityFailure}    AvgQTime = {p.AverageQueueTime}");
                                                                                                                                                                          p.AverageWorkload = Math.abs(p.AverageWorkload);
                System.out.println("mean length of queue = "+p.AverageQueue);
                System.out.println("max observed queue length = "+p.MaxQueueObserved);
                System.out.println("failure probability = "+p.ProbabilityFailure);
                System.out.println("average time in queue = "+p.AverageQueueTime);
                System.out.println("average processing time = "+p.AverageProcessingTime);
                System.out.println("max workload = "+p.MaxWorkload);
                System.out.println("average workload = "+p.AverageWorkload);
            }
        }
    }

    public ArrayList<Result> ReturnResult()
    {
        ArrayList<Result> result = new ArrayList<Result>();
        double average, probability;
        for (var e: list)
        {
            e.PrintResult();
            if (e.getClass() == Process.class)
            {
                Process p = (Process)e;
                average = p.AverageQueue / TCurrent;
                probability = p.Failure / (double)(p.Quantity + p.Failure);
                result.add(new Result(p.Name, average, probability));
            }
        }
        return result;
    }
}
