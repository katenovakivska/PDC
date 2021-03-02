package ProducerConsumer;

import ProducerConsumer.Consumer;
import ProducerConsumer.Drop;
import ProducerConsumer.Producer;

public class ProducerConsumerExample
{
    public static void main(String[] args)
    {
        Drop drop = new Drop();
        (new Thread(new Producer(drop))).start();
        (new Thread(new Consumer(drop))).start();
    }
}
