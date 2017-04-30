package model.agent;

/**
 * Created by audun on 24.04.17.
 */
public class Particle extends RkAgent {

    final double maxVelocity;
    double[] velocity;

    public Particle() {
        super();
        this.maxVelocity = 0.1*n*m;
        velocity = randGen.doubles(0,this.maxVelocity).limit(n*m).toArray();
    }

    public void updatePosition(double[] gBestPosition, double w, double c1, double c2) {
        for(int i = 0; i < n*m; ++i) {
            double newVelocity = w*velocity[i]
                    + c1*randGen.nextDouble()*(gBestPosition[i] - position[i])
                    + c2*randGen.nextDouble()*(bestPosition[i] - position[i]);

            if(newVelocity > maxVelocity) {
                newVelocity = maxVelocity;
            } else if(newVelocity < -maxVelocity) {
                newVelocity = -maxVelocity;
            }

            velocity[i] = newVelocity;
            position[i] += newVelocity;
        }
    }

}
