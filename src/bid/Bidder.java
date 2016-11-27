package bid;

import context.GameHistory;
import context.GameParameters;
import data.Tuple;
import exceptions.NoSolutionException;
import exceptions.NoTaskException;
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




    private final int PLAN_SIZE_COMMITMENT = 15, TOTAL_SAMPLE_COMMITMENT = 10, DEFAULT_WEIGHT_COMMITMENT = 10;

    private static int MIN_OPPONENT_VEHICLES = 2, MAX_OPPONENT_VEHICLES = 5;

    private int BUFFER_SPACE = 1000;




    private GameHistory gameHistory = null;

    private GameParameters gameParameters = null;

    private AgentPlannerContainer aPlanner = null;

    private List<CommitmentEvaluation> playerToCommitEval = null;








    public Bidder(List<Vehicle> primePlayerAsset,
                  Topology topo,
                  int totalPlayer) throws NoSolutionException {


        int primePlayerCumuledCap = 0;

        for(Vehicle v : primePlayerAsset){
            primePlayerCumuledCap += v.capacity();
        }

        int costPerKm = 0;
        for(Vehicle v : primePlayerAsset){
            costPerKm += v.costPerKm();
        }
        costPerKm = costPerKm/primePlayerAsset.size();


        AssetPossibilities opponentAssetPossibilities = new AssetPossibilities(
                        primePlayerCumuledCap, // suppose opponent has same cumuled capacity
                        MIN_OPPONENT_VEHICLES,
                        MAX_OPPONENT_VEHICLES,
                        costPerKm,
                        topo);

        int primePlayerId = 0;

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


        this.playerToCommitEval = new ArrayList<>();


        for(int i = 0; i<gameParameters.totalPlayer(); i++){

            CommitmentEvaluation tmp = new CommitmentEvaluation(
                    gameParameters.topology(),
                    this.PLAN_SIZE_COMMITMENT,
                    this.TOTAL_SAMPLE_COMMITMENT,
                    this.DEFAULT_WEIGHT_COMMITMENT);

            this.playerToCommitEval.add(tmp);
        }

    }




    public Bidder(GameHistory gameHisto,
                  GameParameters gameParameters,
                  AgentPlannerContainer aPlanner,
                  List<CommitmentEvaluation> commitEval) throws NoSolutionException {

        this.gameHistory = gameHisto;
        this.gameParameters = gameParameters;
        this.aPlanner = aPlanner;
        this.playerToCommitEval = commitEval;

    }




    /**
     * @return the game history of the game
     */
    public GameHistory getGameHistory(){

        return this.gameHistory;
    }






    /**
     * set the task given as parameter as pending (not yet bidded)
     * @param task
     */
    public Bidder setNewPendingTask(Task task) throws NoSolutionException {


        GameHistory newHistory = this.gameHistory.setNewPendingTask(task);
        AgentPlannerContainer newAplanner = this.aPlanner.updateGameHistory(newHistory);


        return new Bidder(newHistory, gameParameters, newAplanner, playerToCommitEval);
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

            // TODO : infere with commitment
            //List<Vehicle> vehicles = oppoAsset.inferAsset(newHisto.getPlayerHistory(idPlayerCommited));

            //newGamePara = gameParameters.updateAsset(idPlayerCommited, vehicles);
            newGamePara = gameParameters;
        }


        List<CommitmentEvaluation> newCommitEvals = new ArrayList<>();

        for(int playerId = 0; playerId<gameParameters.totalPlayer(); playerId++){

            TaskSet commitTasks = newHisto.getPlayerHistory(playerId).getCommitedTasks();
            CommitmentEvaluation tmp = this.playerToCommitEval.get(playerId).update(newHisto,commitTasks);
            newCommitEvals.add(tmp);
        }


        return new Bidder(newHisto, newGamePara, newAplanner, newCommitEvals);
    }







    public Tuple<Long,Long> getBid() throws NoTaskException, NoSolutionException {




        // 1. COMPUTE MARGINAL COST TAKING CARE ABOUT FUTURE

        /*
         Agent with high loaded plan will get better cost in the future.
         Those gain need to be considered for our cost.
         Then, we compute our marginal cost in a taskSet of 15 tasks
         */
        List<Long> newMargCost = new ArrayList<>();

        for(int playerId = 0; playerId<this.gameParameters.totalPlayer(); playerId++){

            TaskSet currTaskSet = this.gameHistory.getPlayerHistory(playerId).getCommitedTasks();
            Task pendingTask = this.gameHistory.pending();

            Long currMargCost = 0l;

            if(currTaskSet.size() < this.PLAN_SIZE_COMMITMENT){

                currMargCost = playerToCommitEval
                        .get(playerId)
                        .getMarginalCost(pendingTask, this.gameParameters.getVehicles(playerId));
            }
            else {

                AgentPlanner lastPlan = this.gameHistory.getPlayerHistory(playerId).getAgentPlans().get(0);

                currMargCost = this.aPlanner.getAgentPlan(playerId).getMarginalCost(lastPlan);

            }

            newMargCost.add(currMargCost);
        }



        // 2. CREATE BID DISTRIB FOR EACH PLAYER
        /*
          how opponent bids differ from our estimation using history.
         */


        Long minProposedBid = Long.MAX_VALUE;

        Long heroMarginalCost = newMargCost.get(this.gameParameters.primePlayerId());

        for(int playerId = 0; playerId<gameParameters.totalPlayer(); playerId++){
            if(playerId != gameParameters.primePlayerId()){

                BidDistribution bidDistribution = new BidDistribution(
                        gameHistory.getPlayerHistory(playerId),
                        newMargCost.get(playerId));

                Long currBid = bidDistribution.bestBid(heroMarginalCost);

                if(currBid < minProposedBid){
                    minProposedBid = currBid;
                }
            }
        }

        /*
         previous code try to optimize benefit with the best opponent state
         and will be sufficient for a 2-players game.
         */


        return new Tuple(minProposedBid, heroMarginalCost);
    }






}
