package computation;

import model.*;
import model.agent.Agent;
import model.agent.Bee;
import model.agent.Particle;

import java.util.*;

/**
 * Created by audun on 25.04.17.
 */
public class JSSPSolver {
    private static final JSSP jssp = JSSP.getInstance();

    private static final int n = jssp.getNumJobs();
    private static final int m = jssp.getNumMachines();
    private static final List<Queue<Subtask>> jobs = jssp.getJobs();

    private int maxIter;
    private double targetMakespan;

    public JSSPSolver(double targetMakespan) {
        this.targetMakespan = targetMakespan;
        this.maxIter = Integer.MAX_VALUE;
    }

    public void setMaxIter(int maxIter) {
        this.maxIter = maxIter;
    }

    public Agent solve(String algorithm, int popSize) {
        if(algorithm.equals("PSO")) {
            return particleSwarmOptimization(popSize);
        } else if(algorithm.equals("BA")) {
            return beesAlgorithm(popSize);
        } else {
            throw new IllegalArgumentException("Algorithm not recognized");
        }
    }

    public Particle particleSwarmOptimization(int swarmSize) {
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
        }

        Particle bestParticle = new Particle();
        bestParticle.setPosition(gBestPosition);
        bestParticle.calculateMakespan();
        return bestParticle;
    }

    private Bee beesAlgorithm(int numBees) {
        final int ns = numBees/2; // Number of scout bees
        final int ne = numBees/100; // Number of elite sites
        final int nb = numBees/20; // Number of best sites
        final int nre = numBees/4; // Number of recruited bees for elite sites
        final int nrb = numBees/10; // Number of recruited bees for best sites
        final int ngh = n*m/10; // Initial size of neighbourhood

        List<Bee> scouts = new ArrayList();

        double[] gBestPosition = new double[n*m];

        double makespan = Integer.MAX_VALUE;
        double gBestMakespan = makespan;

        System.out.println("Solving with Bees Algorithm");
        for(int i = 0; i < maxIter; ++i) {
            if(i % 100 == 0) {
                System.out.println("Generation " + i + " (makespan = " + makespan + ", global best = " + gBestMakespan + ")");
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
        }

        Bee best = new Bee();
        best.setPosition(gBestPosition);
        best.calculateMakespan();

        return best;
    }
}
