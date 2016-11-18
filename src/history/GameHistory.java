package history;

import data.Buffer;
import exceptions.NoTaskException;
import logist.task.Task;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by noodle on 18.11.16.
 *
 * it contains a simple list of tasks supplied by the action house until now.
 * And a List of PlayerHistory
 */
public class GameHistory {





    private Task pendingTask = null;

    private Buffer<Task> tasks = null;

    private Map<Integer, PlayerHistory> playerToHisto = null;









    public GameHistory(int bufferSpace, int totalPlayer){


        this.tasks = new Buffer<>(bufferSpace);

        this.playerToHisto = new HashMap<>();

        for(int i = 0; i<totalPlayer; i++){
            this.playerToHisto.put(i, new PlayerHistory(bufferSpace));
        }

    }



    private GameHistory(Buffer<Task> tasks,
                       Map<Integer,PlayerHistory> playerToHisto,
                       Task pendingTask){

        this.tasks = tasks;
        this.playerToHisto = playerToHisto;
        this.pendingTask = pendingTask;

        Collections.unmodifiableMap(this.playerToHisto);

    }






    /**
     *
     * @return true if action house is providing a pending task to evaluate
     */
    public boolean hasPendingTask(){


        return this.pendingTask != null;
    }


    /**
     *
     * @return the newer task still not bidded by the player or
     */
    public Task pending() throws NoTaskException{

        if(this.pendingTask == null){
            throw new NoTaskException();
        }

        return this.pendingTask;
    }

    /**
     *
     * @param i
     * @return the task last bidded task i (newest->oldest) in the capacity limit
     * defined in the constructor.
     */
    public Task get(int i){
        return tasks.get(i);
    }


    /**
     * set the task given as parameter as pending (not yet bidded)
     * @param task
     */
    public GameHistory setNewPendingTask(Task task){

        if(this.hasPendingTask()){
            throw new IllegalAccessError("error pending task discarded");
        }
        return new GameHistory(tasks, playerToHisto, task);
    }


    /**
     * update player history with their bids and add the task for the
     * commited player.
     * @param bids
     */
    public GameHistory setBidFeedback(Long[] bids, int idPlayerCommited, Task task){


        if(bids.length != playerToHisto.size()){
            throw new IllegalAccessError("bids size and histo player size is not the same");
        }

        Map<Integer, PlayerHistory> newPlayerToHisto = new HashMap<>();

        for(int i = 0; i<bids.length; i++){
            PlayerHistory histo = playerToHisto.get(i);
            PlayerHistory newHisto = histo.addBid( bids[i],task, i==idPlayerCommited);
            newPlayerToHisto.put(i,newHisto);
        }

        try {
            Buffer<Task> newTasks = tasks.put(this.pending());
            return new GameHistory(newTasks,newPlayerToHisto,null);
        } catch (NoTaskException e) {
            throw new IllegalAccessError("no pending task during feedback");
        }

    }












}