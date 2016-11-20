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
import java.util.stream.Collectors;

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
        return plans;
    }


    private double p = 0.8;





    public static AgentPlan dummySolution(TaskSet tasks, List<Vehicle> vehicles) throws NoSolutionException {

        if(tasks.isEmpty()){
            return new AgentPlan(new ArrayList<>());
        }

        if(vehicles.isEmpty()){
            throw new NoSolutionException("existing task to deliver without any vehicle");
        }

        // otherwise we create our first dummy plan (the best vehicle do all the job)

        Vehicle maxCapacityVehicle = vehicles.stream().min((v1, v2)->v1.capacity()-v2.capacity()).get();
        VehiclePlan plan = new VehiclePlan(maxCapacityVehicle);

        for(Task t : tasks){
            if(t.weight>maxCapacityVehicle.capacity()){
                throw new NoSolutionException("a task has a weight too high for any of our vehicles");
            }
            plan = plan.add(plan.size(), new Action(t.pickupCity, ActionType.PICKUP,t));
            plan = plan.add(plan.size(), new Action(t.deliveryCity,ActionType.DELIVERY,t));
        }

        ArrayList<VehiclePlan> plans = new ArrayList<>();
        for(Vehicle v : vehicles){
            plans.add( v != maxCapacityVehicle ? new VehiclePlan(v) : plan);
        }

        return new AgentPlan(plans);
    }




    /**
     * Generate neighbours of a given solution
     *
     * @return a set of new solutions
     */
    public Set<AgentPlan> generateNeighbours() {



        List<Integer> notEmptyPlanId = new ArrayList<>();

        for(int i = 0; i<plans.size(); i++){
            if(plans.get(i).size()>0){
                notEmptyPlanId.add(i);
            }
        }

        int planId = notEmptyPlanId.get( (int)( Math.random()*notEmptyPlanId.size() ) );



        Set<AgentPlan> solutions = new HashSet<>();
        VehiclePlan selectedPlan = plans.get(planId);

        ArrayList<VehiclePlan> unselectedPlans = (ArrayList<VehiclePlan>) plans.clone();
        unselectedPlans.remove(planId);


        // 1. all inner modifications over the selected plan

        ArrayList<VehiclePlan> newPlans = (ArrayList<VehiclePlan>) unselectedPlans.clone();
        newPlans.add(selectedPlan.bestModification());
        solutions.add(new AgentPlan(newPlans));



        // 2. remaining modification

        Action extractedPickup = selectedPlan.get(0);
        Action extractedDeliver = null;
        selectedPlan = selectedPlan.remove(0);

        for(int i = 0; i< selectedPlan.size(); i++){
            if(selectedPlan.get(i).getTask() == extractedPickup.getTask()){
                extractedDeliver = selectedPlan.get(i);
                selectedPlan = selectedPlan.remove(i);
            }
        }


        int i = 0;

        for(VehiclePlan plan: unselectedPlans){

            newPlans = (ArrayList<VehiclePlan>) unselectedPlans.clone();
            VehiclePlan modifPlan = newPlans.get(i);
            newPlans.remove(i);

            modifPlan = modifPlan.add(0, extractedDeliver);
            modifPlan = modifPlan.add(0, extractedPickup);

            newPlans.add(modifPlan);
            newPlans.add(selectedPlan);

            solutions.add(new AgentPlan(newPlans));

            i++;
        }


        return solutions;
    }




    /**
     * TODO review : it seems that lab description cannot avoid local minimum => try simulated annealing
     * Choose the solution that gives the best improvement in the objective function
     *
     * @paramneighbors : the set of neighbours from which we will choose the best solution
     * @return the solution with the best improvement with random choice among the best if there is a tie
     */
    private AgentPlan localChoice(AgentPlan solution) {

        Set<AgentPlan> neighbors = solution.generateNeighbours();

        if(Math.random() < this.p){

            float minCost = neighbors.stream()
                    .min((x,y) -> (int) (Math.ceil(x.cost() - y.cost())))
                    .get()
                    .cost();

            List<AgentPlan> minCostCandidates = neighbors.stream()
                    .filter(x -> x.cost() == minCost)
                    .collect(Collectors.toList());

            int randomIndex = (int) Math.floor(Math.random() * minCostCandidates.size());

            return minCostCandidates.get(randomIndex);
        } else {
            int rndId = (int)(Math.random()*neighbors.size());
            int i = 0;
            AgentPlan tmp = null;
            for(AgentPlan s : neighbors){
                if(i == rndId){
                    tmp = s;
                    break;
                }
                i++;
            }

            return tmp;
        }
    }



    /**
     * The objective function
     *
     * @return the total cost of a given solution
     */
    public float cost() {
        return plans.stream().reduce(0f,(accCost,plan)->accCost+plan.cost(),Float::sum);
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




}

