package plan;

import data.Action;
import data.ActionType;
import exceptions.NoSolutionException;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A solution is represented by each vehicle having a dedicated plan
 */
public class AgentPlan {





    // one plan by vehicle
    private ArrayList<VehiclePlan> plans;


    private AgentPlan(ArrayList<VehiclePlan> plans){
        this.plans = plans;
    }


    public ArrayList<VehiclePlan> getPlans(){
        return new ArrayList<>(plans);
    }


    private double p = 0.8;




    // TODO : verify VehiclePlan has the same order as the given asset everywhere
    public static AgentPlan randomPlan(TaskSet tasks, List<Vehicle> vehicles) throws NoSolutionException {


        if(tasks.isEmpty()){

            ArrayList<VehiclePlan> plans = new ArrayList<>();

            for(Vehicle v : vehicles){
                plans.add(new VehiclePlan(v));
            }

            return new AgentPlan(plans);
        }


        if(vehicles.isEmpty()){
            throw new NoSolutionException("existing task to deliver without any vehicle");
        }



        int maxCap = 0;

        ArrayList<VehiclePlan> plans = new ArrayList<>();

        for(int i = 0; i< vehicles.size(); i++){

            plans.add(new VehiclePlan(vehicles.get(i)));

            if(vehicles.get(i).capacity() > maxCap){
                maxCap = vehicles.get(i).capacity();
            }

        }


        for(Task t : tasks){

            if(t.weight>maxCap){
                throw new NoSolutionException("a task has a weight too high for any of our vehicles");
            }

            int planId = (int)(Math.random()*plans.size());

            while(vehicles.get(planId).capacity() < t.weight){
                planId = (plans.size()+planId+1)%plans.size();
            }

            VehiclePlan plan = plans.get(planId);
            plan = plan.add(0, new Action(t.deliveryCity,ActionType.DELIVERY,t));
            plan = plan.add(0, new Action(t.pickupCity, ActionType.PICKUP,t));

            plans.set(planId, plan);
        }

        return new AgentPlan(plans);
    }






    private Set<AgentPlan> generateNeighbours(){



        //1. choose random not empty plan
        List<Integer> notEmptyPlanId = new ArrayList<>();
        for(int i = 0; i<plans.size();i++){
            if(plans.get(i).size()>0){
                notEmptyPlanId.add(i);
            }
        }


        if(notEmptyPlanId.size() == 0){
            return new HashSet<>();
        }

        int planId = notEmptyPlanId.get((int)(Math.random()*notEmptyPlanId.size()));


        // 2. create Set<AgentPlan> => neighbors solutions
        Set<AgentPlan> solutions = new HashSet<>();
        VehiclePlan selectedPlan = plans.get(planId);

        //3. all inner modifications over the selected plan
        ArrayList<VehiclePlan> newPlans = (ArrayList<VehiclePlan>) plans.clone();
        newPlans.set(planId, selectedPlan.bestModification());
        solutions.add(new AgentPlan(newPlans));

        //4. remaining modifications <=> seap between vehicle plans
        //a. extract task from selectedPlans

        Action extractedPickup = selectedPlan.get(0);
        Action extractedDeliver = null;
        selectedPlan = selectedPlan.remove(0);

        for(int i = 0; i<selectedPlan.size();i++){

            if(selectedPlan.get(i).getTask() == extractedPickup.getTask()){
                extractedDeliver = selectedPlan.get(i);
                selectedPlan = selectedPlan.remove(i);
                break;
            }
        }


        for(int i = 0; i<plans.size(); i++){
            if(i != planId){


                ArrayList<VehiclePlan > firstTaskSwap = (ArrayList<VehiclePlan>)plans.clone();
                firstTaskSwap.set(planId,selectedPlan);
                VehiclePlan modifPlan = newPlans.get(i);
                modifPlan = modifPlan.add(0,extractedDeliver);
                modifPlan = modifPlan.add(0,extractedPickup);
                firstTaskSwap.set(i, modifPlan);
                solutions.add(new AgentPlan(firstTaskSwap));


            }
        }

        return solutions;
    }




    /**
     * @paramneighbors : the set of neighbours from which we will choose the best solution
     * @return the solution with the best improvement with random choice among the best if there is a tie
     */
    public AgentPlan localChoice() {

        Set<AgentPlan> neighbors = generateNeighbours();

        if(neighbors.size() == 0){
            return this;
        }

        float minCost = Float.MAX_VALUE;

        for(AgentPlan ap : neighbors){
            float cost = ap.cost();
            if(cost<minCost){
                minCost = cost;
            }
        }

        List<AgentPlan> candidates = new ArrayList<>();

        for(AgentPlan ap : neighbors){
            float cost = ap.cost();
            if(cost==minCost){
                candidates.add(ap);
            }
        }

        int randomIndex = (int) Math.floor(Math.random() * candidates.size());

        return candidates.get(randomIndex);


    }



    /**
     * The objective function
     *
     * @return the total cost of a given solution
     */
    public Long cost() {

        Long cost = 0l;

        for(VehiclePlan vp : this.getPlans()){
            cost += vp.cost();
        }

        return cost;
    }






    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AgentPlan solution = (AgentPlan) o;

        return plans.equals(solution.plans);

    }

    @Override
    public int hashCode() {
        return plans.hashCode();
    }


    @Override
    public String toString(){

        StringBuilder sB = new StringBuilder();

        int i = 0;
        for(VehiclePlan vp : this.getPlans()){
            sB.append("vehicle n°"+i+" has " + vp+"\n");
            i++;
        }

        return sB.toString();
    }


}

