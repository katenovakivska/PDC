package FirstTask;

import java.util.ArrayList;

public class Statistics {
    public long countOfWords;
    public long lengthes;
    public int maxLength;
    public int minLength;
    public int[] counter;

    public Statistics()
    {
        countOfWords = 0;
        lengthes = 0;
        minLength = Integer.MAX_VALUE;
        maxLength = 0;
        counter = new int[30];
    }
}
