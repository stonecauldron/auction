package bid;

import history.GameHistory;
import logist.task.Task;

/**
 * Created by noodle on 18.11.16.
 *
 *
 * process the bid offer using the planning interface and bid.BidAnalysis :
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


    private Bidder(GameHistory history, BidAnalysis bidAnalysis){

        this.history = history;
        this.bidAnalysis = bidAnalysis;
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

        GameHistory newHistory = this.history.setNewPendingTask(task);

        return new Bidder(newHistory, new BidAnalysis(newHistory));
    }



    /**
     * update player history with their bids and add the task for the
     * commited player.
     * @param bids
     */
    public Bidder setBidFeedback(Long[] bids, int idPlayerCommited, Task task){

        GameHistory newHistory = this.history.setBidFeedback(bids,idPlayerCommited,task);

        return new Bidder(newHistory, new BidAnalysis(newHistory));
    }






}
