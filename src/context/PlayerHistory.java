package context;

import data.Buffer;
import data.Tools;
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








    private Buffer<Boolean> isWinnerLs = null;

    private Buffer<Long> bids  = null;

    private Buffer<Long> marginalCosts = null;

    private TaskSet commitTasks = null;








    public PlayerHistory(int bufferSpace){

        Task[] emptyTask = new Task[0];
        this.commitTasks = TaskSet.create(emptyTask);

        isWinnerLs = new Buffer<>(bufferSpace);
        bids = new Buffer<>(bufferSpace);
        marginalCosts = new Buffer<>(bufferSpace);

    }


    private PlayerHistory(Buffer<Long> bids, Buffer<Boolean> isWinnerLs, TaskSet tasks, Buffer<Long> marginalCost){

        if(bids.size() != marginalCost.size()){
            throw new IllegalArgumentException();
        }


        this.bids = bids;
        this.isWinnerLs = isWinnerLs;
        this.commitTasks = tasks;
        this.marginalCosts = marginalCost;

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
    public PlayerHistory addBid(Long lastBid, Task forTask, boolean isWinningBidder, Long estimateMargCost){

        Buffer<Boolean> newIsWinnerLs = this.isWinnerLs.put(isWinningBidder);
        Buffer<Long> newBids = this.bids.put(lastBid);
        Buffer<Long> newCosts = marginalCosts.put(estimateMargCost);


        if(isWinningBidder) {

            TaskSet newTaskset = Tools.createTaskSet(this.getCommitedTasks(),forTask);

            return new PlayerHistory(newBids,newIsWinnerLs, newTaskset, newCosts);
        }

        return new PlayerHistory(newBids,newIsWinnerLs, commitTasks, newCosts);
    }




    public Buffer<Long> getBids(){
        return this.bids;
    }


    public Buffer<Boolean> isWinners(){
        return this.isWinnerLs;
    }


    public Buffer<Long> getEstimateMargCost(){return this.marginalCosts;}



    public String toString(){

        int totalWin = 0;
        for(Boolean b : isWinnerLs){
            if(b){
                totalWin++;
            }
        }


        StringBuilder sB = new StringBuilder("win : "+ totalWin + "  =========================\n");

        for(int i = 0; i< isWinnerLs.size(); i++){
            sB.append("isWin : "+this.isWinnerLs.get(i)+"\t");
            sB.append("bid : "+this.getBids().get(i)+"\t");
            sB.append("estimMarg : "+this.getEstimateMargCost().get(i)+"\t");

            if(this.getBids().get(i) != null) {
                sB.append("diff : " + (this.getBids().get(i) - this.getEstimateMargCost().get(i)) + "\t\n");
            }
        }

        sB.append("\n");

        return sB.toString();
    }


}
