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







    private final int PLAN_SIZE_COMMITMENT = 11, TOTAL_SAMPLE_COMMITMENT = 5 , DEFAULT_WEIGHT_COMMITMENT = 10;

    private static int MIN_OPPONENT_VEHICLES = 2, MAX_OPPONENT_VEHICLES = 5;

    private int BUFFER_SPACE = 1000;





    private GameHistory gameHistory = null;

    private GameParameters gameParameters = null;

    private AgentPlannerContainer aPlanner = null;

    private List<CommitmentEvaluation> playerToCommitEval = null;



    private Long[] playerToEstimateMargeCost = null;





    public Bidder(List<Vehicle> primePlayerAsset,
                  Topology topo,
                  int totalPlayer,
                  int primePlayerId) throws NoSolutionException {


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


        this.aPlanner = new AgentPlannerContainer(gameParameters);


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
     * @return the game parameters of the game
     */
    public GameParameters getParameters(){

        return this.gameParameters;
    }




    /**
     * set the task given as parameter as pending (not yet bidded)
     * @param task
     */
    public Bidder setNewPendingTask(Task task) throws NoSolutionException {


        GameHistory newHistory = this.gameHistory.setNewPendingTask(task);

        return new Bidder(newHistory, gameParameters, aPlanner, playerToCommitEval);
    }






    /**
     * update player history with their bids and add the task for the
     * commited player.
     * @param bids
     */
    public Bidder setBidFeedback(
            Long[] bids,
            int winnerId,
            Task task) throws NoSolutionException {


        GameHistory newHisto = this.gameHistory.setBidFeedback(bids,winnerId,task, playerToEstimateMargeCost);

        AgentPlannerContainer newAplanner = this.aPlanner.update(winnerId,task);

        List<CommitmentEvaluation> newCommitEvals = new ArrayList<>();


        for(int playerId = 0; playerId<gameParameters.totalPlayer(); playerId++){

            TaskSet commitTasks = newHisto.getPlayerHistory(playerId).getCommitedTasks();

            CommitmentEvaluation tmp = null;

            if(playerId == winnerId){
                tmp = this.playerToCommitEval.get(playerId).update(newHisto,commitTasks);
            }
            else  {
                tmp = this.playerToCommitEval.get(playerId);
            }

            newCommitEvals.add(tmp);
        }


        return new Bidder(newHisto, this.gameParameters, newAplanner, newCommitEvals);
    }






    public Tuple<Long,Long> getBid() throws NoTaskException, NoSolutionException {





        // 1. COMPUTE MARGINAL COST TAKING CARE ABOUT FUTURE

        /*
         Agent with high loaded plan will get better cost in the future.
         Those gain need to be considered for our cost.
         Then, we compute our marginal cost in a taskSet of 15 tasks
         */
        Long[] newMargCost = new Long[this.gameParameters.totalPlayer()];
        Task pendingTask = this.gameHistory.pending();

        for(int playerId = 0; playerId<this.gameParameters.totalPlayer(); playerId++){


            TaskSet currTaskSet = this.aPlanner.getAgentPlan(playerId).getCommitedTask();

            Long currMargCost = 0l;

            if(currTaskSet.size() < this.PLAN_SIZE_COMMITMENT){


                currMargCost = playerToCommitEval
                        .get(playerId)
                        .avgTaskCost(this.gameParameters.getVehicles(playerId));

                if(playerId == gameParameters.primePlayerId()){
                    System.out.println(currMargCost);
                }

            }

            // take min between avg_task_cost (in plan of size k) and marginal cost
            currMargCost = Math.min(currMargCost,this.aPlanner.getAgentPlan(playerId).getMarginalCost(pendingTask));


            newMargCost[playerId] = currMargCost;
        }




        playerToEstimateMargeCost = newMargCost;



        // 2. CREATE BID DISTRIB FOR EACH PLAYER
        /*
          how opponent bids differ from our estimation using history.
         */

        Long minProposedBid = Long.MAX_VALUE;

        Long heroMarginalCost = newMargCost[this.gameParameters.primePlayerId()];

        for(int playerId = 0; playerId<gameParameters.totalPlayer(); playerId++){
            if(playerId != gameParameters.primePlayerId()){

                BidDistribution bidDistribution = new BidDistribution(
                        gameHistory.getPlayerHistory(playerId),
                        newMargCost[playerId]);

                // best bid to make
                Long currBid = bidDistribution.bestBid(heroMarginalCost);

                // previous currBid will prefer that hero loose money than opponent win more than our loose
                // here we don't apply this strategy because we play on estimations
                currBid = Math.max(currBid, heroMarginalCost);

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





    @Override
    public String toString(){

        StringBuilder sB = new StringBuilder();

        sB.append(gameHistory.toString()+"\n\n");
        sB.append(aPlanner.toString()+"\n\n");

        return sB.toString();
    }



}
