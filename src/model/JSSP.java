package model;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.*;
import utils.ArrayUtils;
import utils.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by audun on 26.04.17.
 */
public class JSSP {


    private static final JSSP jssp = new JSSP();
    public static JSSP getInstance() {
        if(!loaded) {
            throw new RuntimeException("JSSP problem not loaded.");
        }

        return jssp;
    }

    private static int numJobs, numMachines;
    private static List<Queue<Subtask>> jobs;
    private static Map<Integer,Set<Subtask>> machineSets;
    private static List<Integer> machinesByTlm;

    private static DirectedGraph<Subtask,IntEdge> disjunctiveGraph;
    private static final Subtask source = new Subtask(-1,-1,Integer.MAX_VALUE);
    private static final Subtask sink = new Subtask(-1,-1,Integer.MAX_VALUE);

    private static boolean loaded = false;

    private JSSP() {}

    public static void loadFile(String filepath) throws IOException {
        FileReader file = new FileReader(filepath);
        BufferedReader bufReader = new BufferedReader(file);

        /* First line should contain numJobs (number of jobs), and numMachines (number of machines) */
        try {
            List<Integer> firstLine = StringUtils.parseLine(bufReader.readLine());
            numJobs = firstLine.get(0);
            numMachines = firstLine.get(1);
        } catch(Exception e) {
            throw new IOException("Could not parse first line. File may be invalid or corrupt.",e);
        }

        /* Following numJobs lines should contain a description of each job, listing the machine
         * number and processing time for each step of the job */
        jobs = new ArrayList();
        for(int i = 0; i < numJobs; ++i) {
            Queue<Subtask> job = new LinkedList();
            List<Integer> jobDescription;
            try {
                jobDescription = StringUtils.parseLine(bufReader.readLine());
            } catch(Exception e) {
                throw new IOException("Could not parse description of job " + i + ". File may be invalid or corrupt.",e);
            }
            for(int j = 0; j < numMachines; ++j) {
                job.add(new Subtask(i, jobDescription.get(2*j), jobDescription.get(2*j + 1)));
            }
            jobs.add(job);
        }
        bufReader.close();
        file.close();

        generateDisjunctiveGraph();
        generateMachineSets();
        orderMachinesByTLM();

        loaded = true;
    }

    public static int getNumJobs() {
        return numJobs;
    }

    public static int getNumMachines() {
        return numMachines;
    }

    public static List<Queue<Subtask>> getJobs() {
        return jobs;
    }

    public static Set<Subtask> getMachineSet(int machine) {
        return machineSets.get(machine);
    }

    public static List<Integer> getMachinesByTlm() {
        return machinesByTlm;
    }

    public static DirectedGraph<Subtask,IntEdge> getDisjunctiveGraph() {
        return disjunctiveGraph;
    }

    public static Subtask getSource() {
        return source;
    }

    public static Subtask getSink() {
        return sink;
    }


    private static void generateDisjunctiveGraph() {

        disjunctiveGraph = new SimpleDirectedGraph(IntEdge.class);

        disjunctiveGraph.addVertex(source);
        disjunctiveGraph.addVertex(sink);
        for(Queue<Subtask> job : jobs) {
            for(Subtask subtask : job) {
                disjunctiveGraph.addVertex(subtask);
            }
        }

        // Add directed edges
        for(Queue<Subtask> job : jobs) {
            Subtask prevSubtask = source;
            for(Subtask subtask : job) {
                disjunctiveGraph.addEdge(prevSubtask, subtask, new IntEdge(subtask.getProcessingTime()));
                prevSubtask = subtask;
            }
            disjunctiveGraph.addEdge(prevSubtask,sink, new IntEdge(0));
        }

        // Add undirected edges and generate machine graphs
        for(Queue<Subtask> job : jobs) {
            for(Subtask subtask : job) {
                // Iterate over all subtasks and find those on the same machine
                for(Queue<Subtask> subtasks : jobs) {
                    for(Subtask otherSubtask : subtasks) {
                        if(subtask.equals(otherSubtask)) {
                            continue;
                        }
                        if(subtask.getMachine() == otherSubtask.getMachine()) {
                            disjunctiveGraph.addEdge(subtask, otherSubtask, new IntEdge(otherSubtask.getProcessingTime()));
                        }
                    }
                }
            }
        }
    }

    private static void generateMachineSets() {
        machineSets = new HashMap();
        for(int machine = 0; machine < numMachines; ++machine) {
            machineSets.put(machine, new HashSet());
        }
        for(Queue<Subtask> job : jobs) {
            for(Subtask subtask : job) {
                machineSets.get(subtask.getMachine()).add(subtask);
            }
        }
    }

    private static void orderMachinesByTLM() {
        int[] tlm = new int[numMachines];
        for(Queue<Subtask> job : jobs) {
            for(Subtask subtask : job) {
                tlm[subtask.getMachine()] += subtask.getProcessingTime();
            }
        }

        TreeMap<Integer,List<Integer>> tlmMap = new TreeMap();
        for(int machine = 0; machine < numMachines; ++machine) {
            if(!tlmMap.containsKey(tlm[machine])) {
                tlmMap.put(tlm[machine],new ArrayList());
            }
            tlmMap.get(tlm[machine]).add(machine);
        }

        //int[] sorted = tlm.clone();

        machinesByTlm = new ArrayList();
        for(int i = 0; i < numMachines; ++i) {
            machinesByTlm.add(0);
        }

        int i = 0;
        for(int value : tlmMap.descendingKeySet()) {
            for(int machine : tlmMap.get(value)) {
                machinesByTlm.set(i,machine);
                ++i;
            }
        }
    }
}
