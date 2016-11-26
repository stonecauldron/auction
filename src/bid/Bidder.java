package bid;

import context.GameHistory;
import context.GameParameters;
import exceptions.NoSolutionException;
import exceptions.NoTaskException;
import future_cost.CommitmentEvaluation;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology;
import opponent_parameters.AssetPossibilities;
import opponent_parameters.InferOpponentAsset;
import planning.AgentPlanner;
import planning.AgentPlannerContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noodle on 18.11.16.
 *
 * Return a bid for a given pending task (not yet bidded).
 * Return new bidder when auction house provide feedback (game history).
 *
 */

public class Bidder {







    private GameHistory gameHistory = null;

    private GameParameters gameParameters = null;

    private AgentPlannerContainer aPlanner = null;

    private List<CommitmentEvaluation> playerToCommitEval = null;




    /**
     * max asked bid : total bid in the game (used to take care about future)
     * buffer space : remember last buffer_space game history of all players
     * commit eval sample by value : Commitement Eval generes COMMIT_EVAL_SAMPLE_BY_VALUE taskset to evaluate
     * the average marginal cost for different plan size.
     */
    private static int MAX_ASKED_BID = 30, BUFFER_SPACE = 10000, COMMIT_EVAL_SAMPLE_BY_VALUE = 10;

    private static int MIN_OPPONENT_VEHICLES = 2, MAX_OPPONENT_VEHICLES = 5;






    public Bidder(List<Vehicle> primePlayerAsset,
                  Topology topo,
                  int totalPlayer,
                  int primePlayerId,
                  int costPerKm) throws NoSolutionException {


        int primePlayerCumuledCap = 0;

        for(Vehicle v : primePlayerAsset){
            primePlayerCumuledCap += v.capacity();
        }


        AssetPossibilities opponentAssetPossibilities = new AssetPossibilities(
                        primePlayerCumuledCap, // suppose opponent has same cumuled capacity
                        MIN_OPPONENT_VEHICLES,
                        MAX_OPPONENT_VEHICLES,
                        costPerKm,
                        topo);

        List<List<Vehicle>> playerToAsset = new ArrayList<>();

        for(int i = 0; i<totalPlayer; i++){
            if(i == primePlayerId){
                playerToAsset.add(primePlayerAsset);
            }
            else {
                playerToAsset.add(opponentAssetPossibilities.getMedianAsset());
            }
        }


        this.gameParameters = new GameParameters(playerToAsset, primePlayerId, primePlayerCumuledCap, costPerKm, topo);


        this.gameHistory = new GameHistory(BUFFER_SPACE, gameParameters.totalPlayer());


        this.aPlanner = new AgentPlannerContainer(gameParameters,this.gameHistory);


        List<CommitmentEvaluation> playerToCommitEval = new ArrayList<>();


        for(int i = 0; i<gameParameters.totalPlayer(); i++){

            CommitmentEvaluation tmp = new CommitmentEvaluation(
                    topo,
                    MAX_ASKED_BID,
                    COMMIT_EVAL_SAMPLE_BY_VALUE,
                    gameParameters.getVehicles(i));

            playerToCommitEval.add(tmp);
        }

    }




    public Bidder(GameHistory gameHisto,
                  GameParameters gameParameters,
                  AgentPlannerContainer aPlanner) throws NoSolutionException {

        this.gameHistory = gameHisto;
        this.gameParameters = gameParameters;
        this.aPlanner = aPlanner;

        this.playerToCommitEval = new ArrayList<>();
        for(int i = 0; i<gameParameters.totalPlayer(); i++){

            CommitmentEvaluation tmp = new CommitmentEvaluation(
                    gameParameters.topology(),
                    this.MAX_ASKED_BID,
                    this.COMMIT_EVAL_SAMPLE_BY_VALUE,
                    gameParameters.getVehicles(i));

            this.playerToCommitEval.add(tmp);
        }

    }








    /**
     * set the task given as parameter as pending (not yet bidded)
     * @param task
     */
    public Bidder setNewPendingTask(Task task) throws NoSolutionException {


        GameHistory newHistory = this.gameHistory.setNewPendingTask(task);
        AgentPlannerContainer newAplanner = this.aPlanner.updateGameHistory(newHistory);


        return new Bidder(newHistory, gameParameters, newAplanner);
    }






    /**
     * update player history with their bids and add the task for the
     * commited player.
     * @param bids
     */
    public Bidder setBidFeedback(
            Long[] bids,
            int idPlayerCommited,
            Task task) throws NoSolutionException {


        AgentPlannerContainer snapshot = this.aPlanner;

        GameHistory newHisto = this.gameHistory.setBidFeedback(bids,idPlayerCommited,task, snapshot);

        AgentPlannerContainer newAplanner = this.aPlanner.updateGameHistory(newHisto);


        InferOpponentAsset oppoAsset = new InferOpponentAsset(
                gameParameters.cumuledCapacity(),
                this.MIN_OPPONENT_VEHICLES,
                this.MAX_OPPONENT_VEHICLES,
                gameParameters.costPerKm(),
                gameParameters.topology());


        GameParameters newGamePara = gameParameters;

        if(idPlayerCommited != gameParameters.primePlayerId()){

            List<Vehicle> vehicles = oppoAsset.inferAsset(newHisto.getPlayerHistory(idPlayerCommited));

            newGamePara = gameParameters.updateAsset(idPlayerCommited, vehicles);
        }



        return new Bidder(newHisto, newGamePara, newAplanner);
    }







    public Long getBid() throws NoTaskException {




        // 1. COMPUTE MARGINAL COST TAKING CARE ABOUT FUTURE

        // a. compute immediate cost for every player

        List<Long> newMargCost = new ArrayList<>();

        for(int playerId = 0; playerId<this.gameParameters.totalPlayer(); playerId++){

            AgentPlanner lastPlan = this.gameHistory.getPlayerHistory(playerId).getAgentPlans().get(0);

            Long margCost = this.aPlanner.getAgentPlan(playerId).getMarginalCost(lastPlan);

            newMargCost.add(margCost);
        }

        // b. take care about change in the future in the cost
        /*
         Agent with high loaded plan will get better cost in the future.
         Those gain need to be considered for our cost.
         */
        List<Long> expectedMargCost = new ArrayList<>();
        int expectedAdvantageTime = 5; // TODO

        for(int playerId = 0; playerId<this.gameParameters.totalPlayer(); playerId++){

            Long currEvMargCost = newMargCost.get(playerId);
            TaskSet currTaskSet = this.gameHistory.getPlayerHistory(playerId).getCommitedTasks();
            Task pendingTask = this.gameHistory.pending();

            currEvMargCost -= playerToCommitEval
                    .get(playerId)
                    .futureGainInfluence(expectedAdvantageTime,currTaskSet, pendingTask);

            expectedMargCost.add(currEvMargCost);
        }



        // 2. CREATE BID DISTRIB FOR EACH PLAYER
        /*
          how opponent bids differ from our estimation using history.
         */

        List<BidDistribution> playerToBidDistrib = new ArrayList<>();

        Long minProposedBid = Long.MAX_VALUE;

        Long heroMarginalCost = expectedMargCost.get(this.gameParameters.primePlayerId());
        for(int playerId = 0; playerId<gameParameters.totalPlayer(); playerId++){
            if(playerId != gameParameters.primePlayerId()){

                Long currBid = playerToBidDistrib.get(playerId).bestBid(heroMarginalCost);

                if(currBid < minProposedBid){
                    minProposedBid = currBid;
                }
            }
        }

        /*
         previous code try to optimize benefit with the best opponent state
         and will be sufficient for a 2-players game.
         */


        return minProposedBid;
    }






}
