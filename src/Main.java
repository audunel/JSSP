import computation.JSSPSolver;
import model.Individual;
import model.JSSP;
import org.jfree.ui.RefineryUtilities;
import visualization.ScheduleFrame;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        final String FILEPATH = "test_data/5.txt";
        final int OPTIMAL_MAKESPAN = 1451;
        final String ALGORITHM = "PSO";

        JSSP.loadFile(FILEPATH);

        JSSPSolver solver = new JSSPSolver(1.10*OPTIMAL_MAKESPAN);
        solver.setMaxIter(5000);

        long startTime = System.currentTimeMillis();
        Individual best = solver.solve(ALGORITHM, OPTIMAL_MAKESPAN);
        long endTime = System.currentTimeMillis();

        System.out.println("Found solution after " + (endTime - startTime)/1000 + " seconds");
        System.out.println("Final makespan: " + best.getMakespan());

        ScheduleFrame frame = new ScheduleFrame("Schedule",best);
        frame.pack();
        RefineryUtilities.centerFrameOnScreen(frame);
        frame.setVisible(true);
    }
}
