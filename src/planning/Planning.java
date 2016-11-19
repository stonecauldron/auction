package planning;

import exceptions.NoSolutionException;
import history.PlayerHistory;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;
import plan.Solution;
import plan.VehiclePlan;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

public class Planning {





    private Solution initialSolution, optimalSolution = null;

    private TopologyEvaluation topoEval;

    private double p = 0.8;






    public Planning(TaskSet tasks,
                    List<Vehicle> vehicles,
                    TopologyEvaluation topoEval) throws NoSolutionException {
        this.initialSolution = Solution.dummySolution(tasks, vehicles);
        this.topoEval = topoEval;
    }



    /**
     * TODO review : it seems that lab description cannot avoid local minimum => try simulated annealing
     * Choose the solution that gives the best improvement in the objective function
     *
     * @param neighbors the set of neighbours from which we will choose the best solution
     * @return the solution with the best improvement with random choice among the best if there is a tie
     */
    private Solution localChoice(Solution solution) {

        Set<Solution> neighbors = solution.generateNeighbours();

        if(Math.random() < this.p){

            float minCost = neighbors.stream()
                    .min((x,y) -> (int) (Math.ceil(x.cost() - y.cost())))
                    .get()
                    .cost();

            List<Solution> minCostCandidates = neighbors.stream()
                    .filter(x -> x.cost() == minCost)
                    .collect(Collectors.toList());

            int randomIndex = (int) Math.floor(Math.random() * minCostCandidates.size());

            return minCostCandidates.get(randomIndex);
        } else {
            int rndId = (int)(Math.random()*neighbors.size());
            int i = 0;
            Solution tmp = null;
            for(Solution s : neighbors){
                if(i == rndId){
                    tmp = s;
                    break;
                }
                i++;
            }

            return tmp;
        }
    }





    // TODO : adapt solution to provide the optimal planning for a PlayerHistory


    /**
     * @return a new planning with
     */
    public Solution getOptimalSolution(){

        if(optimalSolution != null){
            return optimalSolution;
        }

        // compute optimal values using local choice
        // store it

        return this.optimalSolution;
    }



    public Long marginalCost(Planning that){

        return this.optimalCost() - that.optimalCost();
    }


    /**
     * @return optimal cost, adding Topology evaluation
     */
    public Long optimalCost(){

        return this.getOptimalSolution().cost(); // TODO : to Long
    }



}
