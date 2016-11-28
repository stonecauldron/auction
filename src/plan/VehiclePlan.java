package plan;

import data.Action;
import data.ActionType;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.topology.Topology;

import java.util.*;

/**
 * represents a Plan for a given vehicle (or agent).
 */
public class VehiclePlan implements Iterable<Action> {








    private Vehicle vehicle;

    private ArrayList<Action> actions;











    public VehiclePlan(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.actions = new ArrayList<Action>();
    }


    public VehiclePlan(Vehicle vehicle, ArrayList<Action> actions) {
        this.actions = actions;
        this.vehicle = vehicle;
    }












    /**
     * @return the vehicle
     */
    public Vehicle getVehicle(){
        return vehicle;
    }


    /**
     * @param i
     * @return the action to process at step i
     */
    public Action get(int i){
        return actions.get(i);
    }


    /**
     * @return a new plan with the given action at the given position
     */
    public VehiclePlan add(int id, Action that){

        ArrayList<Action> newActions = (ArrayList<Action>) actions.clone();
        newActions.add(id,that);

        return new VehiclePlan(this.vehicle, newActions);
    }


    /**
     * return a new plan with the given action removed
     */
    public VehiclePlan remove(int id){

        ArrayList<Action> newActions = (ArrayList<Action>) actions.clone();
        newActions.remove(id);

        return new VehiclePlan(this.vehicle, newActions);
    }


    /**
     * @return the total number of action in the plan
     */
    public int size(){
        return actions.size();
    }





    /**
     * @return the cost needed to execute the plan
     */
    public Long cost() {


        List<Topology.City> path = new ArrayList<>();
        path.add(vehicle.homeCity());

        for(Action toDoAction : this.actions){

            path.addAll(   path.get(path.size()-1).pathTo(toDoAction.getCity())   );
        }


        int totalDistance = 0;
        for(int i = 1; i<path.size(); i++){
            totalDistance += path.get(i-1).distanceTo(path.get(i));
        }

        return (long)(totalDistance*vehicle.costPerKm());
    }



    /**
     * @return the Plan converted for the logistic api
     */
    public logist.plan.Plan toLogistPlan() {

        Topology.City current = vehicle.getCurrentCity();
        logist.plan.Plan plan = new logist.plan.Plan(current);

        for(Action a : this){

            if(a.getAction() == ActionType.PICKUP){

                for(Topology.City c : current.pathTo(a.getCity())){
                    plan.appendMove(c);
                }
                plan.appendPickup(a.getTask());

                current = a.getCity();
            }
            else {

                for(Topology.City c : current.pathTo(a.getCity())){
                    plan.appendMove(c);
                }
                plan.appendDelivery(a.getTask());

                current = a.getCity();
            }

        }
        return plan;
    }






    /**
     * @return a new plan with one permutation over the actions
     */
    public VehiclePlan bestModification(){



        // for each task in the current plan : we extract the task and it's position in the plan
        // and classify them into the following map.

        Map<Task,Integer> pickupTaskId = new HashMap<>();
        Map<Task,Integer> deliverTaskId = new HashMap<>();

        int id = 0;
        for(Action a : this){
            if(a.getAction() == ActionType.DELIVERY){
                deliverTaskId.put(a.getTask(),id);
            }
            else {
                pickupTaskId.put(a.getTask(),id);
            }
            id++;
        }

        // we have to choose the best modification with respect to the following constraints :
        // 1. the vehicle can still carry out all tasks without capacity issues
        // 2. the pickup of a particular task should always happen before its associated delivery
        // a modification is just an action we want to execute latter, or, sooner in the plan

        // working plan help to delay/advance an action
        VehiclePlanBuilder workingPlan = new VehiclePlanBuilder(this.vehicle, this.actions);

        int bestIdFrom = -1;
        int bestIdTo = -1;
        Float bestCost = Float.MAX_VALUE;


        // fromId : is the id of action we want to do sooner/latter
        int fromId= 0;

        while(fromId < actions.size()){

            Action pickedAction = actions.get(fromId);

            if(pickedAction.getAction() == ActionType.PICKUP){

                for(int toId = deliverTaskId.get(pickedAction.getTask())-1; toId >= 0; toId--){
                    if(fromId != toId){

                        workingPlan.setActionDisplacement(fromId,toId);

                        // TODO : useless if toId > fromId
                        if(!workingPlan.hasVehicleCapacitySufficient()){
                            break;
                        }

                        Float cost = workingPlan.cost();

                        if(cost < bestCost){
                            bestCost = cost;
                            bestIdFrom = fromId;
                            bestIdTo = toId;
                        }

                    }
                }


            }
            else { // DELIVER (same as before)


                for(int toId = pickupTaskId.get(pickedAction.getTask())+1; toId < actions.size(); toId++){
                    if(fromId != toId){

                        workingPlan.setActionDisplacement(fromId,toId);

                        // TODO : useless if toId > fromId
                        if(!workingPlan.hasVehicleCapacitySufficient()){
                            break;
                        }

                        Float cost = workingPlan.cost();

                        if(cost < bestCost){
                            bestCost = cost;
                            bestIdFrom = fromId;
                            bestIdTo = toId;
                        }

                    }
                }

            }


            fromId++;
        }

        workingPlan.setActionDisplacement(bestIdFrom, bestIdTo);

        return workingPlan.build();
    }






















    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VehiclePlan plan = (VehiclePlan) o;

        return actions.equals(plan.actions);
    }


    @Override
    public int hashCode() {
        return actions.hashCode();
    }



    @Override
    public Iterator<Action> iterator() {
        return actions.iterator();
    }



    @Override
    public String toString(){
        return "cost : "+this.cost() + " for "+ this.actions;
    }


}
