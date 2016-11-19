package history;

import data.Buffer;
import logist.task.Task;
import logist.task.TaskSet;

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

    private TaskSet commitTasks = null;





    public PlayerHistory(int bufferSpace){

        bids = new Buffer<>(bufferSpace);
    }


    private PlayerHistory(Buffer<Long> bids, TaskSet tasks){

        this.bids = bids;
        this.commitTasks = tasks;

    }








    /**
     * return taskset
     */
    public TaskSet getCommitedTasks(){
        return commitTasks;
    }



    /**
     * @param lastBid
     * @return a new player history updating the history
     */
    public PlayerHistory addBid(Long lastBid, Task forTask, boolean isWinningBidder){

        Buffer<Long> newBids = this.bids.put(lastBid);
        TaskSet newCommitedTasks = this.commitTasks.clone();

        if(isWinningBidder){
            newCommitedTasks.add(forTask);
        }

        return new PlayerHistory(newBids, newCommitedTasks);
    }



    public Buffer<Long> getBids(){
        return bids;
    }


}
