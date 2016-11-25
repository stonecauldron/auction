package future_cost;

import exceptions.NoSolutionException;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology;
import planning.AgentPlanner;

import java.util.*;

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





    private List<Long> totalCitiesToAvgMargCost = null;

    private Integer maxAskedBid = null;




    public CommitmentEvaluation(
            Topology topo,
            int maxAskedBid,
            int sampleSizeByValue,
            List<Vehicle> asset) throws NoSolutionException {

        this.totalCitiesToAvgMargCost = totalCitiesToAverageMargCost(maxAskedBid,sampleSizeByValue, asset, topo);

        this.maxAskedBid = maxAskedBid;

    }







    private TaskSet createRandomTaskSet(int size, Topology topo){

        //Task(int id, City source, Topology.City destination, long reward, int weight)

        Task[] tasks = new Task[size];

        for(int i = 0; i<size; i++){

            Topology.City fromCity = topo.randomCity(new Random());
            Topology.City toCity = topo.randomCity(new Random());

            Task randomTask = new Task(i, fromCity, toCity, 0, 0);
            tasks[i] = randomTask;

        }

        return TaskSet.create(tasks);
    }





    private List<TaskSet> genereTaskSets(int total, int taskSetSize, Topology topo){

        List<TaskSet> taskSets = new ArrayList<>();

        for(int i = 0; i<total; i++){
            taskSets.add(createRandomTaskSet(taskSetSize,topo));
        }


        return taskSets;
    }



    private Set<Topology.City> citiesVisited(TaskSet tasks){

        Set<Topology.City> roads = new HashSet<>();

        for(Task task : tasks){
            roads.addAll(task.path());
        }

        return roads;
    }



    public List<Long> totalCitiesToAverageMargCost(
            int maxTaskSetSize,
            int sampleByTaskSetSize,
            List<Vehicle> vehicles,
            Topology topo) throws NoSolutionException {


        // x represents : the total visited cities in taskSet during the travel


        // 1.- x => List<TaskSet> (having x visited cities)

        Map<Integer, List<TaskSet>> xToTaskSet = new HashMap<>();

        for(int taskSetSize = 1; taskSetSize<=maxTaskSetSize; taskSetSize++){

            List<TaskSet> tasks = genereTaskSets(sampleByTaskSetSize,taskSetSize,topo);

            for(TaskSet t : tasks){
                int x = citiesVisited(t).size();
                if(!xToTaskSet.containsKey(x)){
                    xToTaskSet.put(x, new ArrayList<>());
                }
                xToTaskSet.get(x).add(t);
            }
        }


        Map<Integer, List<Long>> xToPlanCost = new HashMap<>();

        for(int x : xToTaskSet.keySet()){

            xToPlanCost.put(x, new ArrayList<>());

            for(TaskSet taskSet: xToTaskSet.get(x)){

                xToPlanCost.get(x).add(new AgentPlanner(taskSet, vehicles).getOptimalCost());
            }
        }


        Map<Integer, Long> xToAverageCost = new HashMap<>();

        // put taskSetSize = 0 at 0 cost
        xToAverageCost.put(0,0l);

        // and remaining
        for(int x : xToTaskSet.keySet()){

            xToAverageCost.put(x,0l);

            for(int sampleId = 0; sampleId<sampleByTaskSetSize; sampleId++){

                xToAverageCost.put(x, xToAverageCost.get(x)+xToPlanCost.get(x).get(sampleId));
            }

            xToAverageCost.put(x, xToAverageCost.get(x)/sampleByTaskSetSize);
        }


        // extrapolate result from 0 until maxVisitedCities (xLs.get(xLs.size-1))

        List<Integer> xLs = new ArrayList<>(xToAverageCost.keySet());
        Collections.sort(xLs);

        List<Long> xToExtrapolatedAverageCost = new ArrayList<>();

        int lowerCurrId = 0;

        for(int x = 0; x<xLs.get(xLs.size()-1); x++){

            if(xLs.get(lowerCurrId+1) <= x){
                lowerCurrId++;
            }

            int xLower = xLs.get(lowerCurrId);
            int xUpper = xLs.get(Math.min(lowerCurrId+1,xLs.size()-1));

            double progressToUpperX = (double)(x-xLower)/(xUpper-xLower);

            Long result = (long)((1.0 - progressToUpperX)*xToAverageCost.get(xLower))
                    + (long)(progressToUpperX*xToAverageCost.get(xUpper));

            xToExtrapolatedAverageCost.add(result);

        }



        List<Long> xToAverageMarginalCost = new ArrayList<>();

        for(int x = 0; x<xToExtrapolatedAverageCost.size()-1; x++){
            xToAverageMarginalCost.add(x, xToAverageCost.get(x) - xToAverageCost.get(x+1));
        }


        return xToAverageMarginalCost;
    }




    /**
     *
     * @param evRemainingBid ~ how many times we keep our progress vs std_strategy
     *                       this value is an estimation :
     *                       if opponent does not implement futureGain : we could keep our
     *                       progress until the end (avg marg cost will make our algorithm to win
     *                       more and more house_auction's pending task)
     *                       if opponent implement futureGain : we enjoy improvement in the margin
     *                       cost during a shorter duration (geometric law using recursive win_pending_task_probability).
     *
     *
     * @param tasks player actual taskSet
     * @param pendingTask new task to bid
     * @return influence having one more task in the pipeline :
     *
     *  If our marginal cost decrease by diff during the next remainded bid :
        and assuming we always pick the avg task making the player having the best avgReward to win

        If opponent does not care about future avg marginal cost improvement and hero does :
        Then, hero has remindingBid with diff_avg_marg_cost as benefit so it has
        just enough time to repay the remainingBid*diffAvgCost (return of the function).

        If opponent care about improvement and hero does :
        Suppose opponent has currently a better marginal cost, it ensure free progress
        for hero or cost for the opponent. In this case both hero and opponent
        will make losses.

        In fact, the avg task is not drawn each time and distribution allow
        opponent to progress when his state, give to him sufficient advantage
        to balance our avg_marg_cost progress.
     *
     */
    public Long futureGainInfluence(int evRemainingBid, TaskSet tasks, Task pendingTask){

        TaskSet tasksWithPending = tasks.clone();
        tasksWithPending.add(pendingTask);


        int currTotalCities = citiesVisited(tasks).size();

        int nextTotalCities = citiesVisited(tasksWithPending).size();

        if(nextTotalCities >= totalCitiesToAvgMargCost.size()){
            return 0l;
        }

        // avg marg cost decrease : so next time we increase curr avg marg cost by : diffAvgCost
        Long diffAvgCost = totalCitiesToAvgMargCost.get(currTotalCities)
                - totalCitiesToAvgMargCost.get(nextTotalCities);


        // return equilibrium :
        /*
           If our marginal cost decrease by diff during the next remainded bid :
           and assuming we always pick the avg task making the player having the best avgReward to win

           If opponent does not care about future avg marginal cost improvement and hero does :
           Then, hero has remindingBid with diff_avg_marg_cost as benefit so it has
           just enough time to repay the remainingBid*diffAvgCost (return of the function).

           If opponent care about improvement and hero does :
           Suppose opponent has currently a better marginal cost, it ensure free progress
           for hero or cost for the opponent. In this case both hero and opponent
           will make losses.

           In fact, the avg task is not drawn each time and distribution allow
           opponent to progress when his state, give to him sufficient advantage
           to balance our avg_marg_cost progress.
         */
        return evRemainingBid*diffAvgCost;
    }




}
