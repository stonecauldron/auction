package bid;

import context.GameHistory;
import context.GameParameters;
import exceptions.NoSolutionException;
import logist.task.Task;
import planning.AgentPlannerContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noodle on 18.11.16.
 *
 *
 * It's instanciated with PlayerHistory, UnknownParameter,
 *
 * Decorates opponent.UnkownParameters to provide functions:
 * - for a given task it return a map bid => probability
 *
 * bid => opponentGain (using our model)
 *
 *
 * The centralized planning singleton informs about the the marginal cost of the task for the opponent.
 * Using the relative difference from history to our model we can establish  a distribution function that
 * bind a bid to a probability.
 *
 *
 * So this module constructs 2 data :
 *
 *
 * With this 2 predictions, plus a threshold 0-gain bid given as parameter (it’s our marginal cost),
 * the class is able to supply a bid that maximize the the average difference between our gain and the opponent gain.
 * i.e : if we loose 3 by taking a task and our opponent win 5 by taking the same task, we do the job (blocking bid).

 */

public class Bidder {







    private GameHistory gameHistory = null;

    private GameParameters gameParameters = null;

    private AgentPlannerContainer aPlanner = null;






    public Bidder(GameParameters gameParameters,
                  int bufferSpace,
                  int primePlayerId) throws NoSolutionException {

        this.gameParameters = gameParameters;
        this.gameHistory = new GameHistory(bufferSpace, gameParameters.totalPlayer(),primePlayerId);
        this.aPlanner = new AgentPlannerContainer(gameParameters,this.gameHistory);
    }




    public Bidder(GameHistory gameHisto,
                  GameParameters gameParameters,
                  AgentPlannerContainer aPlanner){

        this.gameHistory = gameHisto;
        this.gameParameters = gameParameters;
        this.aPlanner = aPlanner;
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

        return new Bidder(newHisto, gameParameters, newAplanner);
    }






    private BidDistribution getBidDistrib(int idPlayer){

        return new BidDistribution(gameHistory.getPlayerHistory(idPlayer));
    }





    public Long getBid(GameParameters parameters, Task task){

        // 1.
        // computes the marginal cost for the new task for each player
        // according to our model

        List<Long> newMargCost = new ArrayList<>();


        for(int player = 0; player<parameters.totalPlayer(); player++){

            //Long previousCost = this.gameHistory.get(i);

        }



        // 2. Map<idPlayer, BidDistribution>

        // 3. take min bidDistrib without us

        // 4. from min until our processed value isn't decreased anymore

        //    processedValue(forBid) =
        //    (forBid-margCost)[ourGain]*PI[opponent]{probaBetLower(forBod)}

        //    if only one opponent  :
        //    processedValue(forBid) -=
        //    probaBetGreater(forBid)*(forBid-opponentCost)

        //    we consider that a gain for the opponent is equivalent to loss for us
        //    in a one opponent game


        return 0l;
    }






}
