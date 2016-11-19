package bid;

import history.GameHistory;
import logist.simulation.Vehicle;
import logist.task.Task;

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

public class BidAnalysis {






    private GameHistory gameHistory = null;

    private List<List<Vehicle>> playerToAsset = null;

    private ArrayList<ArrayList<Long>> playerToEstimMargCost = null;






    public BidAnalysis(GameHistory gameHisto,
                       List<List<Vehicle>> playerToAsset,
                       ArrayList<ArrayList<Long>> playerToEstimatedCosts){

        this.gameHistory = gameHisto;
        this.playerToAsset = playerToAsset;
        this.playerToEstimMargCost = playerToEstimMargCost;
    }





    public BidAnalysis add(GameHistory newHistory, List<Long> playerToMargCost){


        if(playerToMargCost.size() != this.playerToEstimMargCost.size()){
            throw new IllegalAccessError();
        }


        ArrayList<ArrayList<Long>> newplayerToEstimMargCost
                = new ArrayList<>(this.playerToEstimMargCost.size());

        for(int i = 0; i< playerToMargCost.size(); i++){

            ArrayList<Long> estimMargCost = this.playerToEstimMargCost.get(i);
            Long estimatedMargCost = playerToMargCost.get(i);

            ArrayList<Long> tmp = (ArrayList<Long>)estimMargCost.clone();
            tmp.add(estimatedMargCost);

            newplayerToEstimMargCost.add(tmp);
        }


        return new BidAnalysis(newHistory,this.playerToAsset, newplayerToEstimMargCost);
    }




    public BidDistribution getBidDistrib(int idPlayer){


        return new BidDistribution(
                gameHistory.getPlayerHistory(idPlayer),
                playerToEstimMargCost.get(idPlayer ));

    }





    public Long getBid(Task task){

        // 1. Map<idPlayer, marginalCost>

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
