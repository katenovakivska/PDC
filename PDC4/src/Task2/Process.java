package Task2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Process extends Element
{
        private int QueueLength;
        public int MaxQueueLength;
        public int Failure;
        public double ProbabilityFailure;
        public double AverageQueue;
        public int MaxQueueObserved;
        public double AverageQueueTime;
        public double AverageProcessingTime;
        public double AverageWorkload;
        public double MaxWorkload;
        public int MaxParallel;
        public List<Probability> Probabilities;
        public List<Process> NextProcesses;
        public Random random = new Random();
        public List<Channel> Channels ;
        public int NumberOfTasks;
        public ThreadPoolExecutor Executor;

        public Process()
        {
            NextProcesses = new ArrayList<Process>();
            QueueLength = 0;
            AverageQueue = 0.0;
            MaxQueueObserved = 0;
            AverageWorkload = 0;
        }
        public Process(double delay, String name, String distribution, int maxQueueLength, int maxParallel, double devDelay)
        {
            super(delay, name, distribution, devDelay);
            NextProcesses = new ArrayList<Process>();
            QueueLength = 0;
            MaxQueueLength = maxQueueLength;
            AverageQueue = 0.0;
            MaxQueueObserved = 0;
            AverageWorkload = 0;
            MaxParallel = maxParallel;
            Probabilities = new ArrayList<Probability>();
            Channels = new ArrayList<Channel>();
            Executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MaxParallel);

            for (int i = 0; i < MaxParallel; i++)
            {
                String str = Name+" -> Task1.Channel"+(i + 1);
                Channels.add(new Channel(str, 0.0, true, this));
            }
        }
        public Process(double delay, String name, String distribution, int maxQueueLength, int maxParallel)
        {
            super(delay, name, distribution, 0.0);
            NextProcesses = new ArrayList<Process>();
            QueueLength = 0;
            MaxQueueLength = maxQueueLength;
            AverageQueue = 0.0;
            MaxQueueObserved = 0;
            AverageWorkload = 0;
            MaxParallel = maxParallel;
            Probabilities = new ArrayList<Probability>();
            Channels = new ArrayList<Channel>();
            Executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MaxParallel);

            for (int i = 0; i < MaxParallel; i++)
            {
                String str = Name+" -> Task1.Channel"+(i + 1);
                Channels.add(new Channel(str, 0.0, true, this));
            }
        }
        @Override
        public void InAct(int numberOfTasks)
        {
            int numberOfFreeDevices = MaxParallel - State;
            NumberOfTasks = numberOfTasks;
            if (numberOfTasks <= numberOfFreeDevices && numberOfTasks > 0)
            {
                State += numberOfTasks;
                numberOfTasks = 0;
            }
            else if (numberOfTasks > numberOfFreeDevices)
            {
                numberOfTasks -= numberOfFreeDevices;
                State = MaxParallel;
            }

            TNext = TCurrent + GetDelay();
            if (numberOfTasks > 0)
            {
                int numberOfFreePlaces = MaxQueueLength - QueueLength;
                if (numberOfTasks < numberOfFreePlaces)
                {
                    QueueLength += numberOfTasks;
                    numberOfTasks = 0;
                }
                else
                {
                    numberOfTasks -= numberOfFreePlaces;
                    QueueLength = MaxQueueLength;
                }
            }

            if (numberOfTasks > 0)
            {
                Failure += numberOfTasks;
            }
        }
        public List<Channel> OutChannel(double t)
        {
            List<Channel> outChannels = new ArrayList<Channel>();
            Channels.sort(Comparator.comparingDouble(object->object.TimeOut));
            for (Channel c: Channels)
            {
                if (Channels.size() != 0)
                {
                    if (c.TimeOut < t && c.IsFree == false)
                    {
                        Executor.execute(c.Release);
                        //c.IsFree = true;
                        //outChannels.add(c);
                    }
                }
            }
            return outChannels;
        }
        public void InChannel()
        {
            int count = 0;
            int numberOfFreeDevices = MaxParallel - State;
            //ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MaxParallel);
            if (NumberOfTasks <= numberOfFreeDevices && NumberOfTasks > 0)
            {
                for (int i = 0; i < Channels.size(); i++)
                {
                    if (Channels.get(i).IsFree == true)
                    {
                        Executor.execute(Channels.get(i).Seize);
                        //Channels.get(i).TimeOut = TCurrent + GetDelay();
                        //Channels.get(i).IsFree = false;
                        //System.out.println(Channels.get(i).Name+" is busy and will be free in t = "+Channels.get(i).TimeOut);
                        count++;
                    }
                    if (count == NumberOfTasks)
                    {
                        break;
                    }
                }
                NumberOfTasks = 0;
            }
            else if (NumberOfTasks > numberOfFreeDevices)
            {
                for (int i = 0; i < Channels.size(); i++)
                {
                    if (Channels.get(i).IsFree == true)
                    {
                        Channels.get(i).TimeOut = TCurrent + GetDelay();
                        Channels.get(i).IsFree = false;
                        count++;
                    }
                }
                NumberOfTasks -= numberOfFreeDevices;
            }
        }
        @Override
        public void OutAct()
        {
            super.OutAct();
            TNext = Double.MAX_VALUE;
            State -= 1;
            if (QueueLength > 0)
            {
                QueueLength--;
                State += 1;
                TNext = TCurrent + GetDelay();
            }
            if (NextProcesses.size() != 0 && State != -1)
            {
                if (Probabilities.size() > 1)
                {
                    int index = RandomGenerator.RandomProbability(Probabilities);
                    Process nextProcess = NextProcesses.get(index);
                    if(nextProcess != null)
                    nextProcess.InAct(1);
                    //System.out.println("IN FUTURE goes from "+Name+" to "+nextProcess.Name+" t = "+nextProcess.TNext);
                }
            }
        }
        @Override
        public void PrintInfo()
        {
            super.PrintInfo();
            System.out.println("failure = "+Failure);
        }
        @Override
        public void CountStatistics(double delta)
        {
            AverageQueue += QueueLength * delta;
            AverageProcessingTime += delta;
            AverageWorkload += delta * State;
            if (QueueLength > MaxQueueObserved)
            {
                MaxQueueObserved = QueueLength;
            }
            if (MaxWorkload < State)
            {
                MaxWorkload = State;
            }
        }
}
