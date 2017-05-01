package utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by audun on 16.04.17.
 */
public class ArrayUtils {
    private final static Random randGen = new Random();

    public static int indexOf(double[] array, double value) {
        for(int i = 0; i < array.length; ++i) {
            if(array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(int[] array, int value) {
        for(int i = 0; i < array.length; ++i) {
            if(array[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public static double[] randomArray(double v, double v1, int l) {
        return randGen.doubles(v,v1).limit(l).toArray();
    }
}
