package model;

/**
 * Created by audun on 16.04.17.
 */
public class Subtask {
    private final int job;
    private final int machine;
    private final int processingTime;
    private int startTime;

    public Subtask(int job, int machine, int processingTime) {
        this.job = job;
        this.machine = machine;
        this.processingTime = processingTime;
    }

    public int getJob() {
        return job;
    }

    public int getMachine() {
        return machine;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }
}
