package tests;

import exceptions.NoSolutionException;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology;
import planning.AgentPlanner;

import java.util.*;

/**
 * Created by noodle on 22.11.16.
 */
public class PlanSizeVsMargCost {





    private static TaskSet createRandomTaskSet(int size, Topology topo){

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





    private static List<TaskSet> genereTaskSets(int total, int taskSetSize, Topology topo){

        List<TaskSet> taskSets = new ArrayList<>();

        for(int i = 0; i<total; i++){
            taskSets.add(createRandomTaskSet(taskSetSize,topo));
        }


        return taskSets;
    }




    public static Map<Integer, Long> histogram(
            int xMax,
            int sampleByX,
            List<Vehicle> vehicles,
            Topology topo) throws NoSolutionException {

        // CREATE HISTOGRAM <NUMBER OF TASK => AVERAGE MARGINAL COST>

        Map<Integer, List<TaskSet>> xToTaskSet = new HashMap<>();

        for(int x = 1; x<=xMax; x++){
            xToTaskSet.put(x,genereTaskSets(sampleByX,x,topo));
        }


        Map<Integer, List<Long>> xToPlanCost = new HashMap<>();

        for(int x = 1; x<=xMax; x++){

            xToPlanCost.put(x, new ArrayList<>());

            for(TaskSet taskSet: xToTaskSet.get(x)){

                xToPlanCost.get(x).add(new AgentPlanner(taskSet, vehicles).getOptimalCost());
            }
        }


        Map<Integer, Long> xToAverageCost = new HashMap<>();

        xToAverageCost.put(0,0l);

        for(int x = 1; x<=xMax; x++){

            xToAverageCost.put(x,0l);

            for(int sampleId = 0; sampleId<sampleByX; sampleId++){

                xToAverageCost.put(x, xToAverageCost.get(x)+xToPlanCost.get(x).get(sampleId));
            }
        }


        Map<Integer, Long> xToAverageMarginalCost = new HashMap<>();

        for(int x = 1; x<xMax; x++){
            xToAverageMarginalCost.put(x, xToAverageCost.get(x) - xToAverageCost.get(x-1));
        }


        return xToAverageMarginalCost;
    }







    public static void main(String[] argv) throws NoSolutionException {

        int xMax = 20;
        int sampleByX = 5;
        Topology topo = null;
        List<Vehicle> vehicles = null;

        Map<Integer,Long> xToAverageMarginalCost = histogram(xMax,sampleByX,vehicles, topo);

        for(int i = 0; i<xMax; i++){
            System.out.println("average marginal cost to buy the "
                    +i+"th task equals :\t"
                    +xToAverageMarginalCost.get(i));
        }

    }




}
