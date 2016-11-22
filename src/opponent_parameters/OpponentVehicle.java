package opponent_parameters;

import logist.simulation.Vehicle;
import logist.task.TaskSet;
import logist.topology.Topology;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;

/**
 * Created by noodle on 22.11.16.
 *
 * It represents an opponent Vehicle used to define the assets of our opponent.
 *
 * This class are used by other class trying to guess opponent vehicles
 * in the aim to supply those vehicles to get an estimate marginal cost.
 *
 */
public class OpponentVehicle implements Vehicle{






    private Integer id = null, capacity = null, costPerKm;

    private Topology.City city;





    public OpponentVehicle(int id,
                           int capacity,
                           Topology.City city,
                           int costPerKm){

        this.id = id;
        this.capacity = capacity;
        this.city = city;
        this.costPerKm = costPerKm;
    }






    @Override
    public int id() {
        return this.id;
    }

    @Override
    public String name() {
        return ""+this.id;
    }

    @Override
    public int capacity() {
        return this.capacity;
    }

    @Override
    public int costPerKm() {
        return this.costPerKm;
    }




    ///  THIS FUNCTION DOES NOT HAVE TO BE IMPLEMENTED
    ///  OPPONENT VEHICLE IS IMPLEMENTED TO ESTIMATE
    ///  THE OPPONENT MARGINAL COST



    @Override
    public Topology.City getCurrentCity() {
        throw new NotImplementedException();
    }

    @Override
    public Topology.City homeCity() {
        throw new NotImplementedException();
    }

    @Override
    public double speed() {
        throw new NotImplementedException();
    }

    @Override
    public TaskSet getCurrentTasks() {
        throw new NotImplementedException();
    }

    @Override
    public long getReward() {
        throw new NotImplementedException();
    }

    @Override
    public long getDistanceUnits() {
        throw new NotImplementedException();
    }

    @Override
    public double getDistance() {
        throw new NotImplementedException();
    }

    @Override
    public Color color() {
        throw new NotImplementedException();
    }

}
