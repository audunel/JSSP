package visualization;

import model.agent.Agent;
import model.JSSP;
import model.Subtask;
import org.jfree.chart.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by audun on 18.04.17.
 */

public class ScheduleFrame extends ApplicationFrame {

    JSSP jssp = JSSP.getInstance();

    public ScheduleFrame(String scheduleName, Agent agent) {
        super(scheduleName);

        List<Queue<Subtask>> jobs = JSSP.getInstance().getJobs();

        TaskSeriesCollection dataset = new TaskSeriesCollection();
        TaskSeries machineSchedule = new TaskSeries("job-schedule");

        for(int i = 0; i < jssp.getNumMachines(); ++i) {
            Task machine = new TaskNumeric(""+i, 0, agent.getMakespan());
            machineSchedule.add(machine);

            for(Queue<Subtask> job : jobs) {
                for(Subtask subtask : job) {
                    if(subtask.getMachine() == i) {
                        Task task = new TaskNumeric("" + jobs.indexOf(job),
                                subtask.getStartTime(),
                                subtask.getStartTime() + subtask.getProcessingTime());
                        task.setPercentComplete(jobs.indexOf(job));
                        machine.addSubtask(task);
                    }
                }
            }
        }
        dataset.add(machineSchedule);

        JFreeChart chart = GanttChartFactory.createGanttChart("", "Machine", "Time", dataset, false, true, false);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        MyGanttRenderer renderer = new MyGanttRenderer();
        renderer.setDrawBarOutline(true);
        renderer.setShadowVisible(false);
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        getContentPane().add(chartPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(50, 50, 1300, 500);
    }

}
