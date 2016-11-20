package planning;

import exceptions.NoSolutionException;
import logist.simulation.Vehicle;
import logist.task.TaskSet;
import plan.AgentPlan;

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

    private TopologyEvaluation topoEval;






    public AgentPlanner(TaskSet tasks,
                        List<Vehicle> vehicles,
                        TopologyEvaluation topoEval) throws NoSolutionException {


        this.initialSolution = AgentPlan.dummySolution(tasks, vehicles);
        this.topoEval = topoEval;
    }






    // TODO : adapt solution to provide the optimal planning for a PlayerHistory


    /**
     * @return a new planning with
     */
    public AgentPlan getOptimalSolution(){

        if(optimalSolution != null){
            return optimalSolution;
        }

        // compute optimal values using local choice
        // store it

        return this.optimalSolution;
    }



    public Long marginalCost(AgentPlanner that){

        return this.optimalCost() - that.optimalCost();
    }


    /**
     * @return optimal cost, adding Topology evaluation
     */
    public Long optimalCost(){

        //return this.getOptimalSolution().cost(); // TODO : to Long
        return null;
    }



}