package context;

import data.Buffer;
import logist.task.Task;
import logist.task.TaskSet;
import planning.AgentPlanner;

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

    private Buffer<AgentPlanner> agentPlans = null;

    private TaskSet commitTasks = null;








    public PlayerHistory(int bufferSpace){


        bids = new Buffer<>(bufferSpace);
        agentPlans = new Buffer<>(bufferSpace);

    }


    private PlayerHistory(Buffer<Long> bids, TaskSet tasks, Buffer<AgentPlanner> agentPlans){

        if(bids.size() != agentPlans.size()){
            throw new IllegalArgumentException();
        }


        this.bids = bids;
        this.commitTasks = tasks;
        this.agentPlans = agentPlans;

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
    public PlayerHistory addBid(Long lastBid, Task forTask, boolean isWinningBidder, AgentPlanner plan){

        Buffer<Long> newBids = this.bids.put(lastBid);
        Buffer<AgentPlanner> newPlan = agentPlans.put(plan);

        TaskSet newCommitedTasks = this.commitTasks.clone();

        if(isWinningBidder){
            newCommitedTasks.add(forTask);
        }

        return new PlayerHistory(newBids, newCommitedTasks, newPlan);
    }




    public Buffer<Long> getBids(){
        return this.bids;
    }


    public Buffer<AgentPlanner> getAgentPlans(){return this.agentPlans;}

}
