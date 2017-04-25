package visualization;

import org.jfree.data.gantt.Task;

import java.util.Date;

/**
 * Created by audun on 25.04.17.
 */
public class TaskNumeric extends Task {

    public TaskNumeric(String description, long start, long end) {
        super(description, new Date(start), new Date(end));
    }

    public static TaskNumeric duration(String description, long start, long duration) {
        return new TaskNumeric(description, start, start + duration);
    }

}
