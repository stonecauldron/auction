package data;

import logist.task.Task;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by noodle on 18.11.16.
 *
 *
 *
 *
 *
 * it contains an history of proposed bid (ordered as ACTION HOUSE HISTORY i.e:task supplied),
 * the class also a Set<Task> corresponding of the player's accepted tasks.
 *
 *
 *
 * used in centralized planning interface to compute the optimal planning,
 * and in the opponent package in order to evaluate the best bid to make
 * using the opponent profil.
 *
 *
 */
public class PlayerHistory {







    private Buffer<Long> bids  = null;

    private Set<Task> commitTasks = null;






    public PlayerHistory(int bufferSpace){

        bids = new Buffer<>(bufferSpace);
    }


    private PlayerHistory(Buffer<Long> bids, Set<Task> tasks){

        this.bids = bids;
        this.commitTasks = tasks;

    }








    /**
     * @param lastBid
     * @return a new player history updating the history
     */
    public PlayerHistory addBid(Long lastBid, Task forTask, boolean isWinningBidder){

        Buffer<Long> newBids = this.bids.put(lastBid);
        Set<Task> newCommitedTasks = new HashSet(this.commitTasks);

        if(isWinningBidder){
            newCommitedTasks.add(forTask);
        }

        return new PlayerHistory(newBids, newCommitedTasks);
    }




}
