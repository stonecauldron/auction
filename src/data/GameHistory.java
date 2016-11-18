package data;

import exceptions.NoTaskException;
import logist.task.Task;

import java.util.List;
import java.util.Map;

import static sun.audio.AudioPlayer.player;

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

    }



    public GameHistory(int bufferSpace,
                       int totalPlayer,
                       Buffer<Task> tasks,
                       Map<Integer,PlayerHistory> playerToHisto,
                       Task pendingTask){

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
     * @return the task bidded as step number i
     */
    public Task get(int i){
        return null;
    }


    /**
     * set the task given as parameter as pending (not yet bidded)
     * @param task
     */
    public GameHistory setNewPendingTask(Task task){
        return null;
    }


    /**
     * update player history with their bids and add the task for the
     * commited player.
     * @param bids
     */
    public GameHistory setBidFeedback(Long[] bids, int idPlayerCommited, Task task){
        return null;
    }












}
