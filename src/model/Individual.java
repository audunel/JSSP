package model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by audun on 16.04.17.
 */
public class Individual implements Comparable<Individual> {
    static final Random randGen = new Random();

    protected final int n, m;
    protected final ArrayList<Queue<Subtask>> jobs;
    protected int fitness, bestFitness;
    protected ArrayList<Double> position, bestPosition;

    public Individual(int n, int m, ArrayList<Queue<Subtask>> jobs) {
        this.n = n;
        this.m = m;
        this.jobs = jobs;
        this.position = randGen.doubles(0, n*m)
                .limit(n*m)
                .mapToObj(Double::valueOf)
                .collect(Collectors.toCollection(ArrayList::new));
        this.bestPosition = new ArrayList(position);
    }

    public ArrayList<Double> getPosition() {
        return this.position;
    }

    public void setPosition(ArrayList<Double> position) {
        this.position = new ArrayList(position);
    }

    private int[] getSequence() {
        int[] sequence = new int[n*m];
        ArrayList<Double> sorted =  new ArrayList(position);
        Collections.sort(sorted);
        for(int i = 0; i < position.size(); ++i) {
            sequence[i] = position.indexOf(sorted.get(i)) % n;
        }
        return sequence;
    }

    public void calculateFitness() {
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

        fitness = makespan;
        if(fitness < bestFitness) {
            bestFitness = fitness;
            bestPosition = new ArrayList(position);
        }
    }

    public int getFitness() {
        return fitness;
    }

    public int compareTo(Individual other) {
        if(this.getFitness() > other.getFitness()) {
            return 1;
        } else if(this.getFitness() < other.getFitness()) {
            return -1;
        } else {
            return 0;
        }
    }
}
