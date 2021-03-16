package Task2;

import java.util.ArrayList;
import java.util.List;

public class Element
{
    public String Name;
    public double TNext;
    public double AverageDelay;
    protected double DevDelay;
    public String Distribution;
    public int Quantity;
    public double TCurrent;
    public int State;
    public Element NextElement;
    public List<Element> NextElements;
    public RandomGenerator RandomGenerator = new RandomGenerator();
    public boolean IsNext;
    private static int NextId;
    public int Id;
    public Element()
    {
        TNext = 0.0;
        AverageDelay = 1.0;
        Distribution = "Exponential";
        TCurrent = TNext;
        State = 0;
        Id = NextId;
        NextId++;
        Name = "element " + Name;
    }
    public Element(double delay, String name, String distribution, double devDelay)
    {
        Name = name;
        TNext = 0.0;
        AverageDelay = delay;
        DevDelay = devDelay;
        Distribution = distribution;
        TCurrent = TNext;
        State = 0;
        Id = NextId;
        NextId++;
        IsNext = true;
        NextElements = new ArrayList<Element>();
        Name = "element " + Name;
    }

    public double GetDelay()
    {
        double delay = 0.00;
        switch (Distribution)
        {
            case "Exponential":
                delay = RandomGenerator.Exponential(AverageDelay);
                break;
            case "Uniform":
                delay = RandomGenerator.Uniform(AverageDelay, DevDelay);
                break;
            case "Normal":
                delay = RandomGenerator.Normal(AverageDelay, DevDelay);
                break;
            case "Erlang":
                delay = RandomGenerator.Erlang(AverageDelay, DevDelay);
                break;
            case "":
                delay = AverageDelay;
                break;
        }

        return delay;
    }
    public void InAct(int i)
    {

    }
    public void OutAct()
    {
        Quantity++;
    }
    public void PrintResult()
    {
        //System.out.println(Name+": quantity = "+Quantity);
    }
    public void PrintInfo()
    {
        //System.out.println(Name+": state = "+State+" quantity = "+Quantity+" tnext = "+TNext);
    }
    public void CountStatistics(double delta)
    {

    }
}