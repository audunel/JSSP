package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * Created by audun on 26.04.17.
 */
public class Bee extends Individual {

    public Bee(double[] position) {
        super(position);
    }

    public Bee() {
        super();
    }

    public Bee localSearch(int numForagers, double size) {
        List<Bee> foragers = new ArrayList();
        for(int i = 0; i < numForagers; ++i) {
            double[] location = new double[n*m];
            for(int j = 0; j < location.length; ++j) {
                location[j] = position[j] + (randGen.nextDouble() - 0.5)*size;
            }
            Bee forager = new Bee(location);
            int makespan = forager.calculateMakespan();
            if(makespan < this.makespan) {
                foragers.add(forager);
            }
        }
        if(foragers.isEmpty()) {
            return this;
        } else {
            Collections.sort(foragers);
            return foragers.get(0);
        }
    }
}
