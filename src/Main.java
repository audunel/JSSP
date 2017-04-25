import computation.JSSPSolver;
import entity.Individual;
import entity.Subtask;
import org.jfree.ui.RefineryUtilities;
import utils.StringUtils;
import visualization.ScheduleFrame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {

        FileReader file = new FileReader("test_data/1.txt");
        BufferedReader bufReader = new BufferedReader(file);

        /* First line should contain n (number of jobs), and m (number of machines) */
        ArrayList<Integer> firstLine = StringUtils.parseLine(bufReader.readLine());
        int n = firstLine.get(0);
        System.out.println("n: " + n);
        int m = firstLine.get(1);
        System.out.println("m: " + m);

        /* Following n lines should contain a description of each job, listing the machine
         * number and processing time for each step of the job */
        ArrayList<Queue<Subtask>> jobs = new ArrayList();
        for(int i = 0; i < n; ++i) {
            Queue<Subtask> job = new LinkedList();
            ArrayList<Integer> jobDescription = StringUtils.parseLine(bufReader.readLine());
            for(int j = 0; j < m; ++j) {
                job.add(new Subtask(jobDescription.get(2*j), jobDescription.get(2*j + 1)));
            }
            jobs.add(job);
        }
        bufReader.close();
        file.close();

        JSSPSolver solver = new JSSPSolver(n,m,jobs,56,5000);
        Individual best = solver.particleSwarmOptimization(300);
        System.out.println(best.getMakespan());

        ScheduleFrame frame = new ScheduleFrame("",best,jobs);
        frame.pack();
        RefineryUtilities.centerFrameOnScreen(frame);
        frame.setVisible(true);
    }
}
