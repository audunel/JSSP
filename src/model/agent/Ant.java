package model.agent;

import model.IntEdge;
import model.JSSP;
import model.Subtask;
import org.jgrapht.DirectedGraph;

import java.util.*;

/**
 * Created by audun on 27.04.17.
 */
public class Ant implements Agent {

    private static final JSSP jssp = JSSP.getInstance();

    private static final Random randGen = new Random();

    private static final int alpha = 2;
    private static final int beta = 1;
    private static final int Q = 500;
    private static final double rho = 0.2;

    private int n, m;
    private  DirectedGraph<Subtask, IntEdge> disjunctiveGraph;
    private Map<IntEdge,Double> pheromones;
    private List<Subtask> path;

    int makespan;

    public Ant() {
        n = jssp.getNumJobs();
        m = jssp.getNumMachines();
        disjunctiveGraph = jssp.getDisjunctiveGraph();
        pheromones = new HashMap();

        for(IntEdge edge : disjunctiveGraph.edgeSet()) {
            pheromones.put(edge,0.1);
        }

        makespan = Integer.MAX_VALUE;
    }

    public void traverseGraph(int bestMakespan) {
        path = new ArrayList();

        //Set<Subtask> scheduled = new HashSet();
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
                double p = 1.0;
                //double p = Math.pow(edge.getValue(),alpha) * Math.pow(pheromones.get(edge),beta);
                transitionProbs.put(edge,p);
                total += p;
            }

            // Make transition
            IntEdge transition = new IntEdge(-1);
            double rand = randGen.nextDouble();
            double ratio = 1.0 / total;
            double temp = 0.0;
            for(IntEdge edge : transitionProbs.keySet()) {
                temp += transitionProbs.get(edge);
                if(rand / ratio <= temp) {
                    transition = edge;
                    break;
                }
            }

            currentVertex = disjunctiveGraph.getEdgeTarget(transition);
            available.remove(transition);
            unscheduled.remove(currentVertex);
            if(currentVertex == jssp.getSink()) {
                continue;
            }
            for(IntEdge edge : disjunctiveGraph.outgoingEdgesOf(currentVertex)) {
                if(unscheduled.contains(disjunctiveGraph.getEdgeTarget(edge))) {
                    available.add(edge);
                }
            }
            path.add(currentVertex);
        }
    }

    public List<Subtask> getPath() {
        return path;
    }

    public int getMakespan() {
        return makespan;
    }

    public void scheduleJobs() {
        // TODO: This
        int[] machineAvailableTime = new int[m];
        int[] currentJobTime = new int[n];
        for(int i = 0; i < path.size(); ++i) {
            Subtask subtask = path.get(i);
            int machine = subtask.getMachine();
            int processingTime = subtask.getProcessingTime();
            if(currentJobTime[i] <= machineAvailableTime[machine]) {
                subtask.setStartTime(machineAvailableTime[machine]);
                currentJobTime[i] += processingTime;

            }
        }
    }
}
