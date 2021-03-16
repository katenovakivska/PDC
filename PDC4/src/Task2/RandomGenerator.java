package Task2;
import java.util.List;
import java.util.Random;

public class RandomGenerator
{
    public static Random random = new Random();
    public double Exponential(double timeMean)
    {
        double a = 0;
        while (a == 0)
        {
            a = random.nextDouble();
        }
        a = -timeMean * Math.log(a);

        return a;
    }

    public double Uniform(double timeMin, double timeMax)
    {
        double a = 0;
        while (a == 0)
        {
            a = random.nextDouble();
        }
        a = timeMin + a * (timeMax - timeMin);

        return a;
    }

    public double Normal(double timeMean, double timeDeviation)
    {
        double a;
        Random random = new Random();
        double u1 = 1.0 - random.nextDouble();
        double u2 = 1.0 - random.nextDouble();
        double randStdNormal = Math.sqrt(-2.0 * Math.log(u1)) * Math.sin(2.0 * Math.PI * u2);
        a = timeMean + timeDeviation * randStdNormal;

        return a;
    }

    public double Erlang(double timeMean, double timeDeviation)
    {
        double a = -1 / timeDeviation;
        double[] R = new double[] { 0.43, 0.80, 0.29, 0.67, 0.19, 0.96, 0.02, 0.73, 0.50, 0.33, 0.14, 0.71 };
        double r = 1;
        for (int i = 0; i < (int)timeMean; i++)
        {
            r *= R[i];
        }
        a *= Math.log(r);
        return a;
    }

    public int RandomProbability(List<Probability> probabilities)
    {
        Random random = new Random();
        //int x = 0;
        //int a = random.Next(0, 100);
        //for (int i = 0; i < probabilities.Count; i++)
        //{
        //    if (a >= probabilities[i].MinProbability && a <= probabilities[i].MaxProbability)
        //    {
        //        x = i;
        //    }
        //}
        var vers = new double[probabilities.size()];
        double sum = probabilities.stream().mapToDouble(o->o.MaxProbability).sum();
        vers[0] = probabilities.get(0).MaxProbability / sum;
        for (int i = 1; i < probabilities.size() - 1; i++)
        {
            vers[i] = probabilities.get(i).MaxProbability / sum + vers[i - 1];
        }
        vers[vers.length - 1] = 1.0;
        double rnd = random.nextDouble();
        for (int i = 0; i < probabilities.size(); i++)
        {
            if (vers[i] > rnd)
                return i;
        }
        return probabilities.size() - 1;
    }
}
