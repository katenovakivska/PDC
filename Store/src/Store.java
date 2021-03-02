import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class Store{
    private int product=0;
    ReentrantLock locker;
    Condition condition;

    public Store()
    {
        locker = new ReentrantLock();
        condition = locker.newCondition();
    }

    public void get() {

        locker.lock();
        try{
            while (product<1)
                condition.await();

            product--;
            System.out.println("Покупець купив 1 товар");
            System.out.println("Кількість товарів на складі: " + product);

            condition.signalAll();
        }
        catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
        finally{
            locker.unlock();
        }
    }
    public void put() {

        locker.lock();
        try{
            while (product>=3)
                condition.await();

            product++;
            System.out.println("Виробник виробив 1 товар");
            System.out.println("Кількість товарів на складі: " + product);

            condition.signalAll();
        }
        catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
        finally{
            locker.unlock();
        }
    }
}
