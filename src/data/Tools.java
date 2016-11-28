package data;

import logist.task.Task;
import logist.task.TaskSet;

import java.util.Iterator;

/**
 * Created by noodle on 28.11.16.
 */
public class Tools {





    public static TaskSet createTaskSet(TaskSet set, Task task){


        Task[] newTask = new Task[set.size() + 1];
        Iterator<Task> taskIt = set.iterator();

        for (int i = 0; i < set.size(); i++) {
            Task tmp = taskIt.next();
            newTask[i] = new Task(i, tmp.pickupCity, tmp.deliveryCity, tmp.reward, tmp.weight);
        }

        newTask[newTask.length-1] = new Task(newTask.length-1, task.pickupCity, task.deliveryCity, task.reward, task.weight);

        return TaskSet.create(newTask);
    }




}
