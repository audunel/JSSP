import model.Individual;
import model.Subtask;
import org.jfree.chart.*;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.IntervalCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.chart.urls.StandardCategoryURLGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.ui.ApplicationFrame;

import java.text.NumberFormat;
import java.util.*;

/**
 * Created by audun on 18.04.17.
 */

public class ScheduleFrame extends ApplicationFrame {

    public ScheduleFrame(String scheduleName, Individual ind, ArrayList<Queue<Subtask>> jobs) {
        super(scheduleName);

        CategoryPlot plot = new CategoryPlot();
        plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        plot.setOrientation(PlotOrientation.HORIZONTAL);

        CategoryDataset dataset = new DefaultCategoryDataset();
    }

    private static class TaskNumeric extends Task {

        public TaskNumeric(String description, long start, long end) {
            super(description, new Date(start), new Date(end));
        }

        public static TaskNumeric duration(String description, long start, long duration) {
            return new TaskNumeric(description, start, start + duration);
        }

    }

    private static class GanttChartFactory extends ChartFactory {

        protected static ChartTheme currentTheme = new StandardChartTheme("JFree");

        public static JFreeChart createGanttChart(String title,
                                                  String categoryAxisLabel, String valueAxisLabel,
                                                  IntervalCategoryDataset dataset, boolean legend, boolean tooltips,
                                                  boolean urls) {

            CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
            ValueAxis valueAxis = new NumberAxis(valueAxisLabel);

            CategoryItemRenderer renderer = new GanttRenderer();
            if (tooltips) {
                renderer.setBaseToolTipGenerator(
                        new IntervalCategoryToolTipGenerator(
                                "{1}: {3} - {4}", NumberFormat.getNumberInstance()));
            }
            if (urls) {
                renderer.setBaseItemURLGenerator(
                        new StandardCategoryURLGenerator());
            }

            CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis,
                    renderer);
            plot.setOrientation(PlotOrientation.HORIZONTAL);
            JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                    plot, legend);
            currentTheme.apply(chart);
            return chart;
        }
    }

}
