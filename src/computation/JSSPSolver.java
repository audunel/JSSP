package computation;

import entity.Individual;
import entity.Particle;
import entity.Subtask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;

/**
 * Created by audun on 25.04.17.
 */
public class JSSPSolver {

    final int n;
    final int m;
    final ArrayList<Queue<Subtask>> jobs;

    int maxIter;
    double optimalFitness;

    public JSSPSolver(int n, int m, ArrayList<Queue<Subtask>> jobs, double optimalFitness, int maxIter) {
        this.n = n;
        this.m = m;
        this.jobs = jobs;
        this.maxIter = maxIter;
        this.optimalFitness = optimalFitness;
    }

    public void setMaxIter(int maxIter) {
        this.maxIter = maxIter;
    }

    public Individual particleSwarmOptimization(int swarmSize) {
        final double W_MAX = 1.4;
        final double W_MIN = 0.4;
        final double C1 = 2;
        final double C2 = 2;

        ArrayList<Particle> swarm = new ArrayList();
        for(int i = 0; i < swarmSize; ++i) {
            swarm.add(new Particle(n,m,jobs));
        }

        for(Particle p : swarm) {
            p.calculateMakespan();
        }
        Collections.sort(swarm);
        double[] gBestPosition = swarm.get(0).getPosition();
        double gBestMakespan = Integer.MAX_VALUE;

        double w;
        for(int i = 0; i < maxIter; ++i) {
            if(i % 100 == 0) {
                System.out.println("Generation " + i + " (makespan = " + gBestMakespan + ")");
            }

            w = W_MAX - i*((W_MAX-W_MIN)/ maxIter);
            for(Particle p : swarm) {
                p.updatePosition(gBestPosition, w, C1, C2);
                p.calculateMakespan();
            }

            Collections.sort(swarm);
            if(swarm.get(0).getMakespan() < gBestMakespan) {
                gBestPosition = swarm.get(0).getPosition().clone();
                gBestMakespan = swarm.get(0).getMakespan();
                System.out.println("New best Makespan: " + gBestMakespan);
            }

            if(gBestMakespan < 1.10*optimalFitness) {
                System.out.println("Within 10% of optimal value by " + i + " generations");
                break;
            }
        }

        Particle bestParticle = new Particle(n,m,jobs);
        bestParticle.setPosition(gBestPosition);
        bestParticle.calculateMakespan();
        return bestParticle;
    }

    public Individual beesAlgorithm() {
        final int ns; // Number of scout bees
        final int ne; // Number of elite sites
        final int nb; // Number of best sites
        final int nre; // Number of recruited bees for elite sites
        final int nrb; // Number of recruited bees for best sites
        final int ngh; // Initial site of neighbourhood
        final int stlib; // Limit of stagnation cycles for site abandonment


        ArrayList<Individual> scouts = new ArrayList();
        return new Individual(n,m,jobs);
    }
}
