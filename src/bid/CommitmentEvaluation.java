package bid;

import context.GameHistory;
import exceptions.NoSolutionException;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology;
import planning.AgentPlanner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by noodle on 20.11.16.
 *
 *
 * We claim that when many tasks is assigned to an agent,
 * the vehicle can take fully advantage of their trunk's capacity,
 * and then, agent can have better marginal costs.
 *
 * It comes from the fact that if a vehicle will already have to travel through many cities,
 * a new drawn task will often be in our road, and also because, our planning algo will be able
 * to process a cheaper plan for a big taskSet.
 *
 * Commitment evaluation look at the current task set of a player and evaluates
 * how his marginal cost will decrease in the future if it choose to deliver a task.
 *
 *
 * To do so, it generates at first place random taskset of different size,
 * group them by (distinct cities in all deliverRoad).size(),
 * And allows to compute the AVERAGE_marginal_cost difference between 2 TaskSet.
 *
 *
 *
 */
public class CommitmentEvaluation {





    private List<TaskSet> taskSets = new ArrayList<>();

    private Integer planSize = null;

    private Integer defaultWeight = null;

    private Topology topo = null;





    public CommitmentEvaluation(
            Topology topo,
            int planSize,
            int totalSample,
            int initialTaskWeight) throws NoSolutionException {


        this.topo = topo;

        this.planSize = planSize;

        this.defaultWeight = initialTaskWeight;


        for(int i = 0; i<totalSample; i++){
            taskSets.add(createRandomTaskSet(planSize,topo, initialTaskWeight));
        }

    }


    public CommitmentEvaluation(Topology topo,
                                int planSize,
                                int initialWeight,
                                List<TaskSet> taskSets){

        this.topo = topo;
        this.planSize = planSize;
        this.defaultWeight = initialWeight;
        this.taskSets = taskSets;
    }




    private TaskSet createRandomTaskSet(int size, Topology topo, int weight){

        //Task(int id, City source, Topology.City destination, long reward, int weight)

        Task[] tasks = new Task[size];

        for(int i = 0; i<size; i++){

            Topology.City fromCity = topo.randomCity(new Random());
            Topology.City toCity = topo.randomCity(new Random());

            Task randomTask = new Task(i, fromCity, toCity, 0, weight);
            tasks[i] = randomTask;

        }

        return TaskSet.create(tasks);
    }


    private CommitmentEvaluation addInTaskSet(Task task){

        List<TaskSet> newTaskSets = new ArrayList<>();

        for(int i = 0; i<this.taskSets.size(); i++){

            Task[] newTasks = new Task[planSize+1];
            Iterator<Task> taskIt = this.taskSets.get(i).iterator();

            int id = 0;
            while(taskIt.hasNext()){

                Task t = taskIt.next();

                newTasks[id] = new Task(id,t.pickupCity,t.deliveryCity,t.reward,t.weight);
                id++;
            }
            newTasks[id] = new Task(id,task.pickupCity,task.deliveryCity,task.reward,task.weight);

            newTaskSets.add(TaskSet.create(newTasks));
        }



        return new CommitmentEvaluation(this.topo,this.planSize,this.defaultWeight,newTaskSets);
    }



    private int randomWeight(GameHistory histo){

        int id = (int)(Math.random()*histo.size());

        return histo.get(id).weight;
    }


    public CommitmentEvaluation update(GameHistory history, TaskSet currPlan){


        List<TaskSet> newTaskSet = new ArrayList<>();

        Task[] tasksTemplate = new Task[planSize];
        Iterator<Task> taskIt = currPlan.iterator();

        int id = 0;
        while(taskIt.hasNext()){

            Task t = taskIt.next();

            tasksTemplate[id] = new Task(id,t.pickupCity,t.deliveryCity,t.reward,t.weight);
            id++;
        }


        for(int k = 0; k<this.taskSets.size(); k++) {

            Task[] newTasks = tasksTemplate.clone();

            int j = 0;
            while (id +j < planSize) {
                newTasks[id+j] = new Task(
                        id+j,
                        this.topo.randomCity(new Random()),
                        this.topo.randomCity(new Random()),
                        0,
                        this.randomWeight(history));
                j++;
            }

            newTaskSet.add(TaskSet.create(newTasks));
        }

        return new CommitmentEvaluation(this.topo,this.planSize,this.defaultWeight,this.taskSets);
    }



    private Long cumulatedCost(List<Vehicle> asset) throws NoSolutionException {

        Long cumulatedCost = 0l;

        for(TaskSet tasks : this.taskSets){

            cumulatedCost += new AgentPlanner(tasks, asset).getOptimalCost();
        }

        return cumulatedCost;
    }



    public Long getMarginalCost(Task pendingTask, List<Vehicle> asset) throws NoSolutionException {

        CommitmentEvaluation newEval = this.addInTaskSet(pendingTask);
        Long diffCumulCost = this.cumulatedCost(asset) - newEval.cumulatedCost(asset);
        Long avgDiffCost = diffCumulCost/(this.taskSets.size());

        return avgDiffCost;
    }




}
