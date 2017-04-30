import computation.JSSPSolver;
import model.IntEdge;
import model.Subtask;
import model.agent.Agent;
import model.JSSP;
import model.agent.Ant;
import org.jfree.ui.RefineryUtilities;
import visualization.ScheduleFrame;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        final String FILEPATH = "test_data/2.txt";
        final int OPTIMAL_MAKESPAN = 930;
        final String ALGORITHM = "BA";

        JSSP.loadFile(FILEPATH);

        JSSPSolver solver = new JSSPSolver(1.10*OPTIMAL_MAKESPAN);
        solver.setMaxIter(5000);

        long startTime = System.currentTimeMillis();
        Agent best = solver.solve(ALGORITHM, 400);
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
