package utils;

import java.util.ArrayList;

/**
 * Created by audun on 16.04.17.
 */
public class ArrayUtils {

    public static int indexOf(Double[] array, double value) {
        for(int i = 0; i < array.length; ++i) {
            if(array[i] == value) {
                return i;
            }
        }
        return -1;
    }
}
