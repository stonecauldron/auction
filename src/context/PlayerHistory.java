package context;

import data.Buffer;
import logist.task.Task;
import logist.task.TaskSet;
import planning.AgentPlanner;

import java.util.Iterator;

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








    private Buffer<Boolean> isWinnerLs = null;

    private Buffer<Long> bids  = null;

    private Buffer<AgentPlanner> agentPlans = null;

    private TaskSet commitTasks = null;








    public PlayerHistory(int bufferSpace){

        Task[] emptyTask = new Task[0];
        this.commitTasks = TaskSet.create(emptyTask);

        isWinnerLs = new Buffer<>(bufferSpace);
        bids = new Buffer<>(bufferSpace);
        agentPlans = new Buffer<>(bufferSpace);

    }


    private PlayerHistory(Buffer<Long> bids, Buffer<Boolean> isWinnerLs, TaskSet tasks, Buffer<AgentPlanner> agentPlans){

        if(bids.size() != agentPlans.size()){
            throw new IllegalArgumentException();
        }


        this.bids = bids;
        this.isWinnerLs = isWinnerLs;
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

        Buffer<Boolean> newIsWinnerLs = this.isWinnerLs.put(isWinningBidder);
        Buffer<Long> newBids = this.bids.put(lastBid);
        Buffer<AgentPlanner> newPlan = agentPlans.put(plan);


        if(isWinningBidder) {
            Task[] newTask = new Task[this.commitTasks.size() + 1];
            Iterator<Task> taskIt = commitTasks.iterator();

            for (int i = 0; i < this.commitTasks.size(); i++) {
                Task tmp = taskIt.next();
                newTask[i] = new Task(i, tmp.pickupCity, tmp.deliveryCity, tmp.reward, tmp.weight);
            }
            newTask[newTask.length - 1] = new Task(
                    newTask.length - 1,
                    forTask.pickupCity,
                    forTask.deliveryCity,
                    forTask.reward,
                    forTask.weight);

            return new PlayerHistory(newBids,newIsWinnerLs, TaskSet.create(newTask), newPlan);
        }

        return new PlayerHistory(newBids,newIsWinnerLs, commitTasks, newPlan);
    }




    public Buffer<Long> getBids(){
        return this.bids;
    }


    public Buffer<Boolean> isWinners(){
        return this.isWinnerLs;
    }


    public Buffer<AgentPlanner> getAgentPlans(){return this.agentPlans;}

}
