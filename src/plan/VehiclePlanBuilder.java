package plan;

import data.Action;
import logist.simulation.Vehicle;
import logist.topology.Topology;
import data.ActionType;

import java.util.ArrayList;




public class VehiclePlanBuilder {






    private Vehicle vehicle;

    private ArrayList<Action> actions;

    private Integer fromId=null, toId = null;







    public VehiclePlanBuilder(Vehicle v, ArrayList<Action> actions){
        this.actions = actions;
        this.vehicle = v;
    }








    public void setActionDisplacement(int fromId, int toId){

        this.fromId = fromId;
        this.toId = toId;

    }


    private int getId(int i){

        if(i == toId){
            return fromId;
        }

        if(i>toId){
            i--;
        }

        if(i>=fromId){
            i++;
        }

        return i;
    }


    public Action get(int i){
        return actions.get(this.getId(i));
    }


    public int size(){
        return actions.size();
    }



    /**
     * @return the cost needed to execute the plan
     */
    public float cost() {

        int totalDistance = 0;

        Topology.City currCity = vehicle.homeCity();

        for(int i = 0; i<this.size(); i++){
            Topology.City nextCity = this.get(i).getCity();
            totalDistance += currCity.distanceTo(nextCity);
            currCity = nextCity;
        }

        return totalDistance*vehicle.costPerKm();
    }



    /**
     * @return true if the vehicle capacity support the max weight during the plan or false otherwise
     */
    public boolean hasVehicleCapacitySufficient(){

        boolean isValid = true;

        int accWeight = 0;


        for(int i = 0; i<this.size(); i++){

            Action currAction = this.get(i);

            if(currAction.getAction() == ActionType.PICKUP){
                accWeight += currAction.getTask().weight;
            }
            else {
                accWeight -= currAction.getTask().weight;
            }

            isValid = isValid && accWeight <= vehicle.capacity();
        }


        return isValid;
    }





    /**
     * @return build the vehicle plan using the permutation
     * parametrized.
     */

    public VehiclePlan build(){

        ArrayList<Action> actions = new ArrayList<>();
        for(int i = 0; i<this.size(); i++){
            actions.add(this.get(i));
        }

        return new VehiclePlan(vehicle,actions);
    }


}
