package agent;

import data.GameHistory;
import logist.task.Task;
import opponent.BidAnalysis;

/**
 * Created by noodle on 18.11.16.
 *
 *
 * process the bid offer using the planning interface and opponent.BidAnalysis :
 * 1. compute marginal cost using PlanningInterface as the threshold : no-gain/no-loss
 * 2. pass this value into opponent.bidAnalysis and get back the optimal bid
 *
 * it also provides function to update the state of the bidder each times the action house
 * is supplying feedback (Bidder.appendResult(ActionHouseResult) => new Bidder).
 */

public class Bidder {






    private GameHistory history;

    private BidAnalysis bidAnalysis;







    public Bidder(int historyBuffer, int totalPlayer){

        this.history = new GameHistory(historyBuffer, totalPlayer);
        this.bidAnalysis = new BidAnalysis(this.history);

    }






    /**
     *
     * @return the bid to offer for the history's pending task.
     */
    public Long getBid(){
        return null;
    }


    /**
     * set the task given as parameter as pending (not yet bidded)
     * @param task
     */
    public Bidder setNewPendingTask(Task task){

        return null;
    }


    /**
     * update player history with their bids and add the task for the
     * commited player.
     * @param bids
     */
    public Bidder setBidFeedback(Long[] bids, int idPlayerCommited, Task task){

        return null;
    }





}
