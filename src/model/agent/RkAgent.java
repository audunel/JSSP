package model.agent;

import model.JSSP;
import model.Subtask;
import utils.ArrayUtils;

import java.util.*;


/**
 * Created by audun on 16.04.17.
 */
public class RkAgent implements Agent, Comparable<RkAgent> {
    static final JSSP jssp = JSSP.getInstance();

    static final Random randGen = new Random();

    protected static final int n = jssp.getNumJobs();
    protected static final int m = jssp.getNumMachines();
    protected static final List<Queue<Subtask>> jobs = jssp.getJobs();
    protected int makespan, minMakespan;
    protected double[] position, bestPosition;

    public RkAgent(double[] position) {
        this.position = position;
        this.bestPosition = position.clone();
        this.minMakespan = Integer.MAX_VALUE;
    }

    public RkAgent() {
        this(ArrayUtils.randomArray(0,n*m,n*m));
    }

    public double[] getPosition() {
        return this.position;
    }

    public void setPosition(double[] position) {
        this.position = position.clone();
    }

    private int[] getSequence() {
        int[] sequence = new int[n*m];
        double[] sorted =  position.clone();
        Arrays.sort(sorted);
        for(int i = 0; i < position.length; ++i) {
            sequence[i] = ArrayUtils.indexOf(position,sorted[i]) % n;
        }
        return sequence;
    }

    public int calculateMakespan() {
        List<Queue<Subtask>> jobsToSchedule = new ArrayList();
        for(Queue<Subtask> job : jobs) {
            jobsToSchedule.add(new LinkedList(job));
        }
        int[] machineAvaiableTime = new int[m];
        int[] currentJobTime = new int[n];
        int[] sequence = this.getSequence();
        for(int i : sequence) {
            Subtask subtask = jobsToSchedule.get(i).remove();
            int machine = subtask.getMachine();
            int processingTime = subtask.getProcessingTime();
            if(currentJobTime[i] <= machineAvaiableTime[machine]) {
                machineAvaiableTime[machine] += processingTime;
                currentJobTime[i] = machineAvaiableTime[machine];
            } else {
                currentJobTime[i] += processingTime;
                machineAvaiableTime[machine] = currentJobTime[i];
            }
        }
        int makespan = 0;
        for(int time : currentJobTime) {
            if(time > makespan) {
                makespan = time;
            }
        }

        this.makespan = makespan;
        if(this.makespan < minMakespan) {
            minMakespan = this.makespan;
            bestPosition = position.clone();
        }
        return makespan;
    }

    public int getMakespan() {
        return makespan;
    }

    public void scheduleJobs() {
        List<Queue<Subtask>> jobsToSchedule = new ArrayList();
        for(Queue<Subtask> job : jobs) {
            jobsToSchedule.add(new LinkedList(job));
        }
        int[] machineAvaiableTime = new int[m];
        int[] currentJobTime = new int[n];
        int[] sequence = this.getSequence();
        for(int i : sequence) {
            Subtask subtask = jobsToSchedule.get(i).remove();
            int machine = subtask.getMachine();
            int processingTime = subtask.getProcessingTime();
            if(currentJobTime[i] <= machineAvaiableTime[machine]) {
                subtask.setStartTime(machineAvaiableTime[machine]);
                machineAvaiableTime[machine] += processingTime;
                currentJobTime[i] = machineAvaiableTime[machine];
            } else {
                subtask.setStartTime(currentJobTime[i]);
                currentJobTime[i] += processingTime;
                machineAvaiableTime[machine] = currentJobTime[i];
            }
        }
    }

    public int compareTo(RkAgent other) {
        if(this.getMakespan() > other.getMakespan()) {
            return 1;
        } else if(this.getMakespan() < other.getMakespan()) {
            return -1;
        } else {
            return 0;
        }
    }
}
