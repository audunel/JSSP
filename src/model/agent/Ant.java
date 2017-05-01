package model.agent;

import model.IntEdge;
import model.JSSP;
import model.Subtask;
import org.jgrapht.DirectedGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import java.util.*;

/**
 * Created by audun on 27.04.17.
 */
public class Ant implements Agent, Comparable<Ant> {

    private static final JSSP jssp = JSSP.getInstance();

    private static final Random randGen = new Random();

    private int n, m;
    private List<Queue<Subtask>> jobs;
    private Subtask source, sink;
    private DirectedGraph<Subtask,IntEdge> disjunctiveGraph;
    private DirectedAcyclicGraph<Subtask,IntEdge> scheduleGraph;
    private Map<Integer,List<Integer>> machineSequences;
    private List<Integer> sequence;
    private Set<IntEdge> edges;

    int makespan;

    public Ant() {
        n = jssp.getNumJobs();
        m = jssp.getNumMachines();
        jobs = jssp.getJobs();
        source = jssp.getSource();
        sink = jssp.getSink();
        disjunctiveGraph = jssp.getDisjunctiveGraph();

        machineSequences = new HashMap();

        makespan = Integer.MAX_VALUE;
    }

    public void traverseGraph(Map<IntEdge,Double> pheromones) {
        final int alpha = 1;
        final int beta = 4;

        sequence = new ArrayList();
        edges = new HashSet();

        Set<Subtask> unscheduled = new HashSet(disjunctiveGraph.vertexSet());

        Subtask currentVertex = jssp.getSource();
        unscheduled.remove(currentVertex);

        Set<IntEdge> available = new HashSet(disjunctiveGraph.outgoingEdgesOf(currentVertex));
        while(!unscheduled.isEmpty()) {

            // Calculate probabilities of state transition
            Map<IntEdge,Double> transitionProbs = new HashMap();
            double total = 0.0;
            for(IntEdge edge : available) {
                if(!unscheduled.contains(disjunctiveGraph.getEdgeTarget(edge))) {
                    continue;
                }

                double p = Math.pow(1/(edge.getValue() + 1.0),alpha) * Math.pow(pheromones.get(edge),beta);
                //double p = pheromones.get(edge);
                transitionProbs.put(edge,p);
                total += p;
            }

            // Make transition
            IntEdge transition = new IntEdge(-1);
            double rand = randGen.nextDouble();
            double cumulative = 0.0;
            for(IntEdge edge : transitionProbs.keySet()) {
                cumulative += transitionProbs.get(edge) / total;
                if(rand <= cumulative) {
                    transition = edge;
                    break;
                }
            }

            currentVertex = disjunctiveGraph.getEdgeTarget(transition);
            available.remove(transition);
            edges.add(transition);
            unscheduled.remove(currentVertex);
            if(currentVertex == jssp.getSink()) {
                continue;
            }
            for(IntEdge edge : disjunctiveGraph.outgoingEdgesOf(currentVertex)) {
                if(unscheduled.contains(disjunctiveGraph.getEdgeTarget(edge))) {
                    available.add(edge);
                }
            }

            sequence.add(currentVertex.getJob());
        }
    }

    public void setSequence(List<Integer> sequence) {
        this.sequence = sequence;
    }

    public List<Integer> getSequence() {
        return sequence;
    }

    public Set<IntEdge> getEdges() {
        return edges;
    }

    public void calculateMakespan() {
        List<Queue<Subtask>> jobsToSchedule = new ArrayList();
        for(Queue<Subtask> job : jssp.getJobs()) {
            jobsToSchedule.add(new LinkedList(job));
        }
        int[] machineAvailableTime = new int[m];
        int[] currentJobTime = new int[n];
        for(int i : sequence) {
            Subtask subtask = jobsToSchedule.get(i).remove();
            int job = subtask.getJob();
            int machine = subtask.getMachine();
            int processingTime = subtask.getProcessingTime();
            if(currentJobTime[job] <= machineAvailableTime[machine]) {
                machineAvailableTime[machine] += processingTime;
                currentJobTime[job] = machineAvailableTime[machine];
            } else {
                currentJobTime[job] += processingTime;
                machineAvailableTime[machine] = currentJobTime[job];
            }
        }

        int makespan = 0;
        for(int time : currentJobTime) {
            if(makespan < time) {
                makespan = time;
            }
        }

        this.makespan = makespan;
    }

    public int getMakespan() {
        return makespan;
    }

    public void scheduleJobs() {
        List<Queue<Subtask>> jobsToSchedule = new ArrayList();
        for(Queue<Subtask> job : jssp.getJobs()) {
            jobsToSchedule.add(new LinkedList(job));
        }
        int[] machineAvailableTime = new int[m];
        int[] currentJobTime = new int[n];
        for(int i : sequence) {
            Subtask subtask = jobsToSchedule.get(i).remove();
            int job = subtask.getJob();
            int machine = subtask.getMachine();
            int processingTime = subtask.getProcessingTime();
            if(currentJobTime[job] <= machineAvailableTime[machine]) {
                subtask.setStartTime(machineAvailableTime[machine]);
                machineAvailableTime[machine] += processingTime;
                currentJobTime[job] = machineAvailableTime[machine];
            } else {
                subtask.setStartTime(currentJobTime[job]);
                currentJobTime[job] += processingTime;
                machineAvailableTime[machine] = currentJobTime[job];
            }
        }
    }

    public int compareTo(Ant other) {
        if (this.getMakespan() > other.getMakespan()) {
            return 1;
        } else if (this.getMakespan() < other.getMakespan()) {
            return -1;
        } else {
            return 0;
        }
    }
}
