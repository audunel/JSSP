package computation;

import model.Individual;
import model.Particle;
import model.Subtask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.Random;

/**
 * Created by audun on 25.04.17.
 */
public class JSSPSolver {

    static final Random randGen = new Random();

    static final double W_MAX = 1.4;
    static final double W_MIN = 0.4;
    static final double C1 = 2;
    static final double C2 = 2;
    static final double BETA = 0.97;
    static final double T_F = 0.1;

    final int n;
    final int m;
    final ArrayList<Queue<Subtask>> jobs;

    int maxGen;
    int popSize;

    double optimalFitness;

    public JSSPSolver(int n, int m, ArrayList<Queue<Subtask>> jobs, int maxGen, int popSize, double optimalFitness) {
        this.n = n;
        this.m = m;
        this.jobs = jobs;
        this.maxGen = maxGen;
        this.popSize = popSize;
        this.optimalFitness = optimalFitness;
    }

    public void setMaxGen(int maxGen) {
        this.maxGen = maxGen;
    }

    public void setPopSize(int popSize) {
        this.popSize = popSize;
    }

    public Individual particleSwarmOptimization() {
        ArrayList<Particle> swarm = new ArrayList();
        for(int i = 0; i < popSize; ++i) {
            swarm.add(new Particle(n,m,jobs));
        }

        for(Particle p : swarm) {
            p.calculateFitness();
        }
        Collections.sort(swarm);
        ArrayList<Double> gBestPosition = swarm.get(0).getPosition();
        double gBestFitness = swarm.get(0).getFitness();

        double w;
        for(int gen = 0; gen < maxGen; ++gen) {
            if(gen % 100 == 0) {
                System.out.println("Gen " + gen);
                System.out.println("Fitness " + gBestFitness);
            }

            w = W_MAX - gen*((W_MAX-W_MIN)/ maxGen);
            for(Particle p : swarm) {
                p.updatePosition(gBestPosition, w, C1, C2);
                p.calculateFitness();
                /*if(randGen.nextDouble() < 0.01) {
                    p.simmulatedAnnealing(p.getFitness() - gBestFitness, T_F, BETA);
                }*/
            }

            Collections.sort(swarm);
            if(swarm.get(0).getFitness() < gBestFitness) {
                gBestPosition = new ArrayList(swarm.get(0).getPosition());
                gBestFitness = swarm.get(0).getFitness();
            } else {
                // Elitism
                int idx = randGen.nextInt(popSize);
                swarm.get(idx).setPosition(gBestPosition);
                swarm.get(idx).calculateFitness();
            }

            if(gBestFitness < 1.1*optimalFitness) {
                System.out.println("Within 10% of optimal value by " + gen + " generations");
                break;
            }
        }

        Collections.sort(swarm);
        return swarm.get(0);
    }

    public Individual beesAlgorithm() {
        return new Individual(n,m,jobs);
    }
}
