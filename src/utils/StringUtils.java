package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by audun on 16.04.17.
 */
public class StringUtils {
    public static List<Integer> parseLine(String line) {
        return Stream.of(line.trim().split("\\s+"))
                .filter(StringUtils::isNumeric)
                .map(Integer::parseInt)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static boolean isNumeric(String s) {
        return s.matches("-?\\d+(\\.\\d+)?");
    }
}
