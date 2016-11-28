package planning;

import context.GameParameters;
import exceptions.NoSolutionException;
import logist.task.Task;
import logist.task.TaskSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noodle on 20.11.16.
 */
public class AgentPlannerContainer {



    private List<AgentPlanner> playerToPlanning = new ArrayList<>();



    public AgentPlannerContainer(GameParameters parameters) throws NoSolutionException {

        for(int i = 0; i<parameters.totalPlayer(); i++){

            Task[] emptyTask = new Task[0];

                playerToPlanning.add(
                        new AgentPlanner(
                                TaskSet.create(emptyTask),
                                parameters.getVehicles(i))
                );

        }

    }


    private AgentPlannerContainer(List<AgentPlanner> playerToPlan) {

        this.playerToPlanning = playerToPlan;

    }




    public AgentPlannerContainer update(int winnerId, Task t) throws NoSolutionException {

        List<AgentPlanner> newPlanner = new ArrayList<>();

        for(int i = 0; i<this.playerToPlanning.size(); i++){
            if(i == winnerId){
                newPlanner.add(this.getAgentPlan(i).updateCommitedtask(t));
            }
            else {
                newPlanner.add(this.getAgentPlan(i));
            }
        }

        return new AgentPlannerContainer(newPlanner);
    }




    public AgentPlanner getAgentPlan(int playerId){

        return this.playerToPlanning.get(playerId);
    }




    @Override
    public String toString(){

        StringBuilder sB = new StringBuilder();

        sB.append("AGENT PLANNER CONTAINER ================================\n\n");
        for(int i = 0;i<this.playerToPlanning.size();i++){
            sB.append("PLAYER N°"+i+" ################\n");
            sB.append(this.getAgentPlan(i).toString());
        }
        sB.append("========================================================\n\n");

        return sB.toString();
    }

}
