package entity;

import utils.ArrayUtils;

import java.util.*;

/**
 * Created by audun on 16.04.17.
 */
public class Individual implements Comparable<Individual> {
    static final Random randGen = new Random();

    protected final int n, m;
    protected final ArrayList<Queue<Subtask>> jobs;
    protected int makespan, minMakespan;
    protected double[] position, bestPosition;

    public Individual(int n, int m, ArrayList<Queue<Subtask>> jobs) {
        this.n = n;
        this.m = m;
        this.jobs = jobs;
        this.position = randGen.doubles(0, 10).limit(n*m).toArray();
        this.bestPosition = position.clone();
        this.minMakespan = Integer.MAX_VALUE;
    }

    public int getM() {
        return m;
    }

    public int getN() {
        return n;
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

    public void calculateMakespan() {
        ArrayList<Queue<Subtask>> jobsToSchedule = new ArrayList();
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
    }

    public int getMakespan() {
        return makespan;
    }

    public int compareTo(Individual other) {
        if(this.getMakespan() > other.getMakespan()) {
            return 1;
        } else if(this.getMakespan() < other.getMakespan()) {
            return -1;
        } else {
            return 0;
        }
    }
}
