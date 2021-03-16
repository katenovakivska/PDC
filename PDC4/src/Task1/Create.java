package Task1;

public class Create extends Element
{
    public Create(double delay, String name, String distribution, double devDelay)
    {
        super(delay, name, distribution, devDelay);
    }
    public Create(double delay, String name, String distribution)
    {
        super(delay, name, distribution, 0);
    }
    @Override
    public void OutAct()
    {
        super.OutAct();
        TNext = TCurrent + GetDelay();
        NextElement.InAct(1);
    }
}