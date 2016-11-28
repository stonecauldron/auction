package planning;

import data.Action;
import data.ActionType;
import data.Tools;
import exceptions.NoSolutionException;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;
import plan.AgentPlan;
import plan.VehiclePlan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noodle on 17.11.16.
 *
 *
 *
 * It provides a plan for input : <vehicles, tasks>,
 * and the coresponding using centralized planning.
 *
 *
 *
 * TODO :
 *
 * add a function shuffle to move to a distant solution for the current one (in solution).
 * Make this operation after reached a local minimum to retry to another place in
 * solutions space. Keep in memory the minimum cost encountered.
 * Use the topoEval to take care about future influence in cost inside localChoice
 *
 */

public class AgentPlanner {








    private AgentPlan initialSolution, optimalSolution = null;

    private TaskSet commitedTask = null;

    private List<Vehicle> vehicles = new ArrayList<>();







    public AgentPlanner(TaskSet tasks,
                        List<Vehicle> vehicles) throws NoSolutionException {


        this.initialSolution = AgentPlan.randomPlan(tasks, vehicles);
        this.commitedTask = tasks;
        this.vehicles = vehicles;
    }





    private AgentPlanner(TaskSet tasks,
                        List<Vehicle> vehicles,
                         AgentPlan initialSolution) throws NoSolutionException {


        this.initialSolution = initialSolution;
        this.commitedTask = tasks;
        this.vehicles = vehicles;

    }




    /**
     * @return a new planning with
     */
    public AgentPlan getOptimalSolution(){

        if(optimalSolution != null){
            return optimalSolution;
        }

        optimalSolution = this.initialSolution;
        Long optimalCost = this.initialSolution.cost();

        AgentPlan currAgent = optimalSolution;
        Long currAgentCost = optimalCost;


        int attempt = 0;

        for(int i = 0; i<30000; i++){

            AgentPlan neighbor = currAgent.localChoice();
            Long neighborCost = neighbor.cost();

            if(neighborCost < currAgentCost){

                currAgent = neighbor;
                currAgentCost = neighborCost;
                attempt = 0;

            }else{

                attempt++;

                if(attempt >= 4) {
                    try {
                        currAgent = AgentPlan.randomPlan(this.getCommitedTask(), this.vehicles);
                        currAgentCost = currAgent.cost();
                    } catch (NoSolutionException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(currAgentCost < optimalCost){
                optimalCost = currAgentCost;
                optimalSolution = currAgent;
            }

        }


        return this.optimalSolution;
    }





    public Long getMarginalCost(Task task) throws NoSolutionException {

        Long l = this.updateCommitedtask(task).getOptimalCost()-this.getOptimalCost();

        return Math.max(l,0l);
    }



    /**
     * @return optimal cost, adding Topology evaluation
     */
    public Long getOptimalCost(){

        return this.getOptimalSolution().cost();
    }



    /**
     *
     * @return the set of task that have to be optimized by our planning algorithm
     */
    public TaskSet getCommitedTask(){
        return commitedTask;
    }





    public AgentPlanner updateCommitedtask(Task t) throws NoSolutionException {

        List<VehiclePlan> vpLs = this.getOptimalSolution().getPlans();

        for(int i = 0; i< vpLs.size(); i++){

            if(vpLs.get(i).getVehicle().capacity() >= t.weight){
                VehiclePlan newVp;
                newVp = vpLs.get(i).add(0, new Action(t.deliveryCity, ActionType.DELIVERY,t));
                newVp = newVp.add(0, new Action(t.pickupCity, ActionType.PICKUP,t));
                vpLs.set(i,newVp);
                break;
            }
        }

        TaskSet newTaskSet = Tools.createTaskSet(this.commitedTask,t);

        return new AgentPlanner(newTaskSet,this.vehicles);
    }




    @Override
    public String toString(){

        StringBuilder sB = new StringBuilder();

        sB.append("commited task : "+ this.commitedTask+"\n");

        for(int i = 0; i<this.vehicles.size(); i++){
            sB.append("vehicle n°"+i+": \n");
            sB.append(this.getOptimalSolution().getPlans().get(i));
        }

        return sB.toString();
    }


}
