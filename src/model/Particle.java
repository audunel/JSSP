package model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by audun on 24.04.17.
 */
public class Particle extends Individual {

    final double maxVelocity;
    ArrayList<Double> velocity;

    public Particle(int n, int m, ArrayList<Queue<Subtask>> jobs) {
        super(n, m, jobs);
        this.maxVelocity = 0.1*n*m;
        velocity = randGen.doubles(0,this.maxVelocity)
                .limit(n*m)
                .mapToObj(Double::valueOf)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void updatePosition(ArrayList<Double> gBestPosition, double w, double c1, double c2) {
        for(int i = 0; i < n*m; ++i) {
            double newVelocity = w*velocity.get(i)
                    + c1*randGen.nextDouble()*(gBestPosition.get(i) - position.get(i))
                    + c2*randGen.nextDouble()*(bestPosition.get(i) - position.get(i));

            if(newVelocity > maxVelocity) {
                newVelocity = maxVelocity;
            } else if(newVelocity < -maxVelocity) {
                newVelocity = -maxVelocity;
            }

            velocity.set(i, newVelocity);
            position.set(i, position.get(i) + newVelocity);
        }
    }

    /*public void simmulatedAnnealing(double T0, double Tf, double beta) {
        double T = T0;
        while(T > Tf) {
            Particle newParticle = individualEnhancement();
            newParticle.calculateFitness();

            double delta = newParticle.getFitness() - this.getFitness();
            if(delta > 0) {
                double r = randGen.nextDouble();
                if(r < Math.min(1,Math.exp(delta / T))) {
                    this.setPosition(newParticle.getPosition());
                    this.calculateFitness();
                }
            } else {
                this.setPosition(newParticle.getPosition());
                this.calculateFitness();
            }
            T = beta*T;
        }
    }*/

    public Particle individualEnhancement() {
        Particle newParticle = new Particle(n,m,jobs);

        double q = randGen.nextDouble();
        if(0 <= q && q < 0.4) {;
            // Pick two dimensions at random
            int i = randGen.nextInt(position.size());
            int j = i;
            while(i == j) {
                j = randGen.nextInt(position.size());
            }
            // Swap the values of the two dimensions
            newParticle.getPosition().set(i, position.get(j));
            newParticle.getPosition().set(j, position.get(i));
        }
        else if(0.4 <= q && q < 0.8) {
            // Pick two dimensions at random
            int i = randGen.nextInt(position.size());
            int j = i;
            while(i == j) {
                j = randGen.nextInt(position.size());
            }
            // Remove the value at i and insert it at j
            double value = newParticle.getPosition().remove(i);
            newParticle.getPosition().add(j, value);
        }
        else {
            // Pick two dimensions at random
            int i = randGen.nextInt(position.size());
            int j = i;
            while(i == j) {
                j = randGen.nextInt(position.size());
            }
            int min = Math.min(i,j);
            int max = Math.max(i,j);
            // Reverse the values between i and j
            ArrayList<Double> newPosition = new ArrayList(position.subList(0,min));
            List<Double> reversed = position.subList(min,max);
            Collections.reverse(reversed);
            newPosition.addAll(reversed);
            newPosition.addAll(position.subList(max,position.size()));

            newParticle.setPosition(newPosition);
        }
        // TODO: Implement long-distance movement
        return newParticle;
    }

}
