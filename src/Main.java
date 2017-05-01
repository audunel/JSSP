import computation.JSSPSolver;
import model.agent.Agent;
import model.JSSP;
import model.agent.Ant;
import org.jfree.ui.RefineryUtilities;
import visualization.ScheduleFrame;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        final String FILEPATH = "test_data/3.txt";
        final int ACCEPTABLE_MAKESPAN = 1276;
        final String ALGORITHM = "BA";

        JSSP.loadFile(FILEPATH);

        JSSPSolver solver = new JSSPSolver(1.10*ACCEPTABLE_MAKESPAN);
        solver.setMaxIter(100);

        long startTime = System.currentTimeMillis();
        Agent best = solver.solve(ALGORITHM);
        long endTime = System.currentTimeMillis();

        System.out.println("Found solution after " + (endTime - startTime)/1000 + " seconds");
        System.out.println("Final makespan: " + best.getMakespan());

        best.scheduleJobs();

        ScheduleFrame frame = new ScheduleFrame("Schedule",best);
        frame.pack();
        RefineryUtilities.centerFrameOnScreen(frame);
        frame.setVisible(true);
    }
}
