package computation;

import model.*;
import model.agent.Agent;
import model.agent.Ant;
import model.agent.Bee;
import model.agent.Particle;
import org.jgrapht.DirectedGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import java.util.*;

/**
 * Created by audun on 25.04.17.
 */
public class JSSPSolver {
    private static final JSSP jssp = JSSP.getInstance();
    private static final Scanner scanner = new Scanner(System.in);

    private static final int n = jssp.getNumJobs();
    private static final int m = jssp.getNumMachines();
    private static final List<Queue<Subtask>> jobs = jssp.getJobs();
    private static final DirectedGraph<Subtask,IntEdge> disjunctiveGraph = jssp.getDisjunctiveGraph();

    private int maxIter;
    private double targetMakespan;

    public JSSPSolver(double targetMakespan) {
        this.targetMakespan = targetMakespan;
        this.maxIter = Integer.MAX_VALUE;
    }

    public void setMaxIter(int maxIter) {
        this.maxIter = maxIter;
    }

    public Agent solve(String algorithm) {
        if(algorithm.equals("PSO")) {
            return particleSwarmOptimization();
        } else if(algorithm.equals("BA")) {
            return beesAlgorithm();
        } else if(algorithm.equals("ACO")) {
            return antColonyOptimization();
        } else {
            throw new IllegalArgumentException("Algorithm not recognized");
        }
    }

    public Particle particleSwarmOptimization() {
        final int swarmSize = 400;
        final double W_MAX = 1.4;
        final double W_MIN = 0.4;
        final double C1 = 2;
        final double C2 = 2;

        List<Particle> swarm = new ArrayList();
        for(int i = 0; i < swarmSize; ++i) {
            swarm.add(new Particle());
        }

        for(Particle p : swarm) {
            p.calculateMakespan();
        }
        Collections.sort(swarm);
        double[] gBestPosition = swarm.get(0).getPosition();

        double makespan = Integer.MAX_VALUE;
        double gBestMakespan = makespan;

        double w;
        System.out.println("Solving with Particle Swarm Optimization");
        for(int i = 0; i < maxIter; ++i) {
            if(i % 100 == 0) {
                System.out.println("Iteration " + i + " (makespan = " + gBestMakespan + ")");
            }

            w = W_MAX - i*((W_MAX-W_MIN)/ maxIter);
            for(Particle p : swarm) {
                p.updatePosition(gBestPosition, w, C1, C2);
                p.calculateMakespan();
            }

            Collections.sort(swarm);
            makespan = swarm.get(0).getMakespan();
            if(makespan < gBestMakespan) {
                gBestMakespan = makespan;
                gBestPosition = swarm.get(0).getPosition().clone();
                System.out.println("New best Makespan: " + gBestMakespan);
            }

            if(gBestMakespan < targetMakespan) {
                System.out.println("Reached target makespan by " + i + " iterations");
                break;
            }

            if(i == maxIter - 1) {
                System.out.println("Reached maximum iteration " + maxIter);
                System.out.println("Additional iterations: ");
                int n = scanner.nextInt();
                maxIter += n;
            }
        }

        Particle bestParticle = new Particle();
        bestParticle.setPosition(gBestPosition);
        bestParticle.calculateMakespan();
        return bestParticle;
    }

    private Bee beesAlgorithm() {
        final int ns = 400; // Number of scout bees
        final int ne = 4; // Number of elite sites
        final int nb = 15; // Number of best sites
        final int nre = 75; // Number of recruited bees for elite sites
        final int nrb = 30; // Number of recruited bees for best sites
        final int ngh = n*m/10; // Initial size of neighbourhood

        List<Bee> scouts = new ArrayList();

        double[] gBestPosition = new double[n*m];

        double makespan = Integer.MAX_VALUE;
        double gBestMakespan = makespan;

        System.out.println("Solving with Bees Algorithm");
        for(int i = 0; i < maxIter; ++i) {
            if(i % 100 == 0) {
                System.out.println("Generation " + i + " (makespan = " + gBestMakespan + ")");
            }

            // Send out scouts
            while(scouts.size() < ns) {
                Bee scout = new Bee();
                scout.calculateMakespan();
                scouts.add(scout);
            }
            Collections.sort(scouts);

            makespan = scouts.get(0).getMakespan();
            if(makespan < gBestMakespan) {
                gBestMakespan = makespan;
                gBestPosition = scouts.get(0).getPosition().clone();
                System.out.println("New best Makespan: " + gBestMakespan);
            }

            if(gBestMakespan < targetMakespan) {
                System.out.println("Reached target makespan by " + i + " iterations");
                break;
            }

            List<Bee> bestBees = new ArrayList();
            for(int j = 0; j < ne; ++j) {
                Bee eliteBee = scouts.get(j).localSearch(nre,ngh);
                bestBees.add(eliteBee);
            }
            for(int j = ne; j < ne+nb; ++j) {
                Bee bestBee = scouts.get(j).localSearch(nrb,ngh);
                bestBees.add(bestBee);
            }

            scouts = new ArrayList(bestBees);

            if(i == maxIter - 1) {
                System.out.println("Reached iteration " + maxIter);
                System.out.println("Additional iterations: ");
                int n = scanner.nextInt();
                maxIter += n;
            }
        }

        Bee best = new Bee();
        best.setPosition(gBestPosition);
        best.calculateMakespan();

        return best;
    }

    private Ant antColonyOptimization() {
        final int numAnts = 100;
        final double Q = targetMakespan;
        final double tauInitial = 1.0;
        final double rho = 0.5;
        final double epsilon = 0.5;
        final double maxTau = 1.999;
        final double minTau = 0.001;

        List<Ant> ants = new ArrayList();
        for(int i = 0; i < numAnts; ++i) {
            ants.add(new Ant());
        }

        Map<IntEdge,Double> pheromones = new HashMap();
        for(IntEdge edge : JSSP.getDisjunctiveGraph().edgeSet()) {
            pheromones.put(edge,tauInitial);
        }

        Ant best;
        int gBestMakespan = Integer.MAX_VALUE;
        List<Integer> gBestSequence = new ArrayList();
        System.out.println("Solving with Ant Colony Optimization");
        for(int i = 0; i < maxIter; ++i) {
            if(i % 10 == 0) {
                System.out.println("Iteration " + i + " (makespan = " + gBestMakespan + ")");
            }

            for(Ant ant : ants) {
                ant.traverseGraph(pheromones);
                ant.calculateMakespan();
                int makespan = ant.getMakespan();
                if(makespan < gBestMakespan) {
                    System.out.println("New best Makespan: " + makespan);
                    gBestMakespan = makespan;
                    gBestSequence = ant.getSequence();
                }
            }

            if(gBestMakespan < targetMakespan) {
                System.out.println("Reached target makespan by " + i + " iterations");
                break;
            }

            /*Map<IntEdge,Double> deltaTau = new HashMap();
            for(Ant ant : ants) {
                for(IntEdge edge : ant.getEdges()) {
                    deltaTau.put(edge,deltaTau.getOrDefault(edge,0.0) + ant.getMakespan());
                }
            }
            for(IntEdge edge : deltaTau.keySet()) {
                deltaTau.put(edge, Q/(deltaTau.get(edge) + 1.0));
            }*/

            Collections.sort(ants);
            best = ants.get(0);
            Map<IntEdge,Double> deltaTauElite = new HashMap();
            for(IntEdge edge : best.getEdges()) {
                deltaTauElite.put(edge,Q/(gBestMakespan + 1.0));
            }

            for(IntEdge edge : pheromones.keySet()) {
                double newTau = (1-rho)*pheromones.get(edge)
                        /*+ rho*deltaTau.getOrDefault(edge,0.0)*/
                        + rho*deltaTauElite.getOrDefault(edge,0.0);
                if(newTau > maxTau) {
                    newTau = maxTau;
                }
                if(newTau < minTau) {
                    newTau = minTau;
                }
                pheromones.put(edge,newTau);
            }

            if(i == maxIter - 1) {
                System.out.println("Reached iteration " + maxIter);
                System.out.println("Additional iterations: ");
                int n = scanner.nextInt();
                maxIter += n;
            }
        }

        best = new Ant();
        best.setSequence(gBestSequence);
        best.calculateMakespan();

        return best;
    }
}
