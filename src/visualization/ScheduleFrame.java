package visualization;

import entity.Individual;
import entity.Subtask;
import org.jfree.chart.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Created by audun on 18.04.17.
 */

public class ScheduleFrame extends ApplicationFrame {

    public ScheduleFrame(String scheduleName, Individual ind, ArrayList<Queue<Subtask>> jobs) {
        super(scheduleName);

        TaskSeriesCollection dataset = new TaskSeriesCollection();
        TaskSeries machineSchedule = new TaskSeries("job-schedule");

        for(int i = 0; i < ind.getM(); ++i) {
            Task machine = new TaskNumeric(""+i, 0, ind.getMakespan());
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
