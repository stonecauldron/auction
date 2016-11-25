package opponent_parameters;

import logist.simulation.Vehicle;
import logist.topology.Topology;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by noodle on 22.11.16.
 *
 * This module create possible opponent asset (List<Vehicle>)
 * regardless of historic opponent behaviour.
 * By specifying a range of possible number of vehicle for our opponent
 * (2 to 5 during the tournament), and a cumuledCapacity to spread over the
 * opponent vehicle. This class construct some of the possible opponent asset.
 *
 */
public class AssetPossibilities implements Iterable<List<Vehicle>> {




    private List<List<Vehicle>> assetPossibilities = null;

    private List<Vehicle> medianAsset = null;




    public AssetPossibilities(int cumuledCap,
                              int minVehicle,
                              int maxVehicle,
                              int costPerKm,
                              Topology topo){


        // all type of assets(List<Vehicle>) our opponent could dispose
        assetPossibilities = new ArrayList<>();


        for(int totalOpponentVehicles = minVehicle; totalOpponentVehicles<=maxVehicle; totalOpponentVehicles++){

            //1. we process 2 assets (List<Vehicle> for our opponent)
            //   for the given number of opponent vehicles : totalOpponentVehicles

            // first one has uniform repartition :
            List<Vehicle> opponentUniform = new ArrayList<>();
            // second one has one bigger vehicle and one smaller vehicle
            List<Vehicle> opponentUnbalanced = new ArrayList<>();

            // So, the number of possibilities is not exhaustif,
            // but we only want to get an asset that is close to real opponent asset.


            // 2. process OPPONENT UNIFORM :

            int uniformRep = (int)((double)cumuledCap/totalOpponentVehicles);

            for(int i = 0; i<totalOpponentVehicles; i++) {
                opponentUniform.add(new OpponentVehicle(i, uniformRep, topo.randomCity(new Random()), costPerKm));
            }

            // 3. process OPPONENT UNBALANCED :

            if(totalOpponentVehicles <=1 ){
                continue;
            }

            opponentUnbalanced = new ArrayList<>(opponentUniform);
            opponentUnbalanced.set(0,
                    new OpponentVehicle(0, (int)(1.5*(double)uniformRep) ,topo.randomCity(new Random()),costPerKm)
            );

            opponentUnbalanced.set(1,
                    new OpponentVehicle(1, (int)(0.5*(double)uniformRep) ,topo.randomCity(new Random()),costPerKm)
            );


            //4. ADD the 2 created assets for the current number of opponentVehicles

            this.assetPossibilities.add(opponentUniform);
            this.assetPossibilities.add(opponentUnbalanced);


            int middleTotalVehicles = minVehicle + (maxVehicle - minVehicle)/2;
            if(totalOpponentVehicles == middleTotalVehicles){
                this.medianAsset = opponentUniform;
            }

        }




    }


    /**
     * @return an asset that is the representative of all created possible asset.
     */
    public List<Vehicle> getMedianAsset(){
        return this.medianAsset;
    }



    @Override
    public Iterator<List<Vehicle>> iterator() {
        return assetPossibilities.iterator();
    }



}
