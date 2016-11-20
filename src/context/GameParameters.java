package context;

import logist.simulation.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by noodle on 20.11.16.
 */
public class GameParameters {






    private List<List<Vehicle>> playerToAsset = null;

    private Integer totalPlayer = null;






    public GameParameters(int totalPlayer){


        playerToAsset = new ArrayList<>();

        for(int i = 0; i<totalPlayer; i++){
            playerToAsset.add(new ArrayList<>());
            Collections.unmodifiableCollection(playerToAsset.get(i));
        }

        Collections.unmodifiableCollection(playerToAsset);

        this.totalPlayer = totalPlayer;
    }



    public GameParameters(List<List<Vehicle>> playerToAsset){

        this.playerToAsset = playerToAsset;
    }








    public GameParameters updateAsset(int playerId, List<Vehicle> vehicles){

        List<List<Vehicle>> newPlayerToAsset = new ArrayList<>(playerToAsset);

        Collections.unmodifiableCollection(vehicles);

        newPlayerToAsset.set(playerId, vehicles);

        return new GameParameters(newPlayerToAsset);
    }





    public List<Vehicle> getVehicles(int playerId){
        return this.playerToAsset.get(playerId);
    }





    public int totalPlayer(){

        return totalPlayer;
    }




}
