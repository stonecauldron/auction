package planning;

import context.GameHistory;
import context.GameParameters;
import exceptions.NoSolutionException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noodle on 20.11.16.
 */
public class AgentPlannerContainer {







    private GameParameters parameters = null;

    private TopologyEvaluation topoEval = null;

    private GameHistory gameHistory = null;

    private List<AgentPlanner> playerToPlanning = new ArrayList<>();







    public AgentPlannerContainer(GameParameters parameters,
                                 GameHistory gameHisto,
                                 TopologyEvaluation topoEval) throws NoSolutionException {



        this.parameters = parameters;

        this.gameHistory = gameHisto;

        this.topoEval = topoEval;



        for(int i = 0; i<parameters.totalPlayer(); i++){

                playerToPlanning.add(
                        new AgentPlanner(
                                gameHisto.getPlayerHistory(i).getCommitedTasks(),
                                parameters.getVehicles(i),
                                topoEval)
                );

        }

    }







    public AgentPlannerContainer updateGameHistory(GameHistory gameHisto) throws NoSolutionException {

        return new AgentPlannerContainer(this.parameters, gameHisto, topoEval);
    }




    public AgentPlanner getAgentPlan(int playerId){

        return this.playerToPlanning.get(playerId);
    }





}
