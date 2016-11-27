package context;

import logist.simulation.Vehicle;
import logist.topology.Topology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by noodle on 20.11.16.
 */
public class GameParameters {






    private List<List<Vehicle>> playerToAsset = null;

    private Integer cumuledCapacity = null;

    private Integer costPerKm = null;

    private Integer primePlayerId = null;

    private Topology topology = null;





    public GameParameters(
            List<List<Vehicle>> playerToAsset,
            int primePlayer,
            int cumuledCapacity,
            int costPerKm,
            Topology topology){

        this.playerToAsset = playerToAsset;
        this.primePlayerId = primePlayer;
        this.cumuledCapacity = cumuledCapacity;
        this.costPerKm = costPerKm;
        this.topology = topology;

        // TODO : delete
        for(Vehicle v : playerToAsset.get(0)){
            System.out.println(v.homeCity());
        }

    }







    public GameParameters updateAsset(int playerId, List<Vehicle> vehicles){

        List<List<Vehicle>> newPlayerToAsset = new ArrayList<>(playerToAsset);

        Collections.unmodifiableCollection(vehicles);

        newPlayerToAsset.set(playerId, vehicles);

        return new GameParameters(
                newPlayerToAsset,
                this.primePlayerId,
                this.cumuledCapacity,
                this.costPerKm,
                this.topology);
    }





    public List<Vehicle> getVehicles(int playerId){
        return this.playerToAsset.get(playerId);
    }





    public int totalPlayer(){

        return playerToAsset.size();
    }


    public int primePlayerId(){
        return primePlayerId;
    }


    public int costPerKm(){
        return costPerKm;
    }

    public int cumuledCapacity(){
        return cumuledCapacity;
    }

    public Topology topology(){
        return topology;
    }

}
