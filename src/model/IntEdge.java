package model;

import org.jgrapht.graph.DefaultEdge;

/**
 * Created by audun on 27.04.17.
 */
public class IntEdge extends DefaultEdge {
    private final int value;

    public IntEdge(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
