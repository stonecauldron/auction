package tests;

import exceptions.NoSolutionException;
import logist.config.ParserException;
import logist.config.Parsers;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskSet;
import logist.topology.Topology;
import opponent_parameters.OpponentVehicle;
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

        List<List<TaskSet>> xToTaskSet = new ArrayList<>();

        for(int x = 1; x<=xMax; x++){
            xToTaskSet.add(genereTaskSets(sampleByX,x,topo));
        }

        System.out.println(xToTaskSet.get(2));





        List<List<Long>> xToPlanCost = new ArrayList<>();

        for(int x = 0; x<xMax; x++){

            System.out.println(""+x);
            List<Long> planCost = new ArrayList<>();

            for(TaskSet taskSet: xToTaskSet.get(x)){

                planCost.add(new AgentPlanner(taskSet, vehicles).getOptimalCost());
            }

            xToPlanCost.add(planCost);
        }

        System.out.println("one :::: ");
        System.out.println(xToPlanCost.get(2));
        System.out.println("one :::: ");


        List<Long> xToAverageCost = new ArrayList<>();

        xToAverageCost.add(0l);

        for(int x = 0; x<xMax; x++){

            Long avgCost = 0l;
            for(int sampleId = 0; sampleId<sampleByX; sampleId++){
                avgCost += xToPlanCost.get(x).get(sampleId);
            }

            avgCost = avgCost/sampleByX;
            xToAverageCost.add(avgCost);
        }

        System.out.println(xToAverageCost.get(2));

        Map<Integer, Long> xToAverageMarginalCost = new HashMap<>();

        for(int x = 0; x<xMax; x++){
            xToAverageMarginalCost.put(x, xToAverageCost.get(x+1) - xToAverageCost.get(x));
        }


        return xToAverageMarginalCost;
    }







    public static void main(String[] argv) throws NoSolutionException {

        int xMax = 5;
        int sampleByX = 100;

        Topology topo = null;
        List<Vehicle> vehicles = new ArrayList<>();

        try {
            topo = Parsers.parseTopology("config/topology/france.xml");
        } catch (ParserException e) {
            e.printStackTrace();
        }

        vehicles.add(new OpponentVehicle(0,60,topo.randomCity(new Random()),5));

        Map<Integer,Long> xToAverageMarginalCost = histogram(xMax,sampleByX,vehicles, topo);

        for(int i = 0; i<xMax; i++){
            System.out.println("average marginal cost to buy the "
                    +i+"th task equals :\t"
                    +xToAverageMarginalCost.get(i));
        }

    }




}
