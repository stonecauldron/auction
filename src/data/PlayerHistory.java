package data;

import logist.task.Task;

import java.util.List;
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






    private Buffer<Integer> bids  = null;

    private Set<Task> commitTasks = null;





    public PlayerHistory(int bufferSpace){

    }


    /**
     *
     * @param lastBid
     * @return a new player history updating the history
     */
    public PlayerHistory addBid(int lastBid, Task forTask, boolean isWinningBidder){
        return null;
    }




}
