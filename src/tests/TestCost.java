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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by noodle on 27.11.16.
 */
public class TestCost {






    private static TaskSet createRandomTaskSet(int size, Topology topo){

        //Task(int id, City source, Topology.City destination, long reward, int weight)

        Task[] tasks = new Task[size];

        for(int i = 0; i<size; i++){

            Topology.City fromCity = topo.randomCity(new Random());
            Topology.City toCity = topo.randomCity(new Random());

            Task randomTask = new Task(i, fromCity, toCity, 0, 30);
            tasks[i] = randomTask;

        }

        return TaskSet.create(tasks);
    }





    public static void main(String[] argv) throws NoSolutionException {

        int taskSetSize = 15;
        int totalSample = 5;
        int computedByValue = 1;

        Topology topo = null;
        List<Vehicle> vehicles = new ArrayList<>();

        try {
            topo = Parsers.parseTopology("config/topology/france.xml");
        } catch (ParserException e) {
            e.printStackTrace();
        }

        vehicles.add(new OpponentVehicle(0,60,topo.randomCity(new Random()),5));


        Task pendingTask = new Task(taskSetSize,topo.randomCity(new Random()), topo.randomCity(new Random()), 0,30);


        List<TaskSet> taskSets = new ArrayList<>();

        for(int i = 0; i<taskSetSize; i++){
            taskSets.add(createRandomTaskSet(taskSetSize,topo));
        }



        for(int i =0; i<totalSample; i++){

            String tmp = "sample n°"+i+" get : [";
            for(int k = 0; k<computedByValue; k++) {
                AgentPlanner agentPlanner = new AgentPlanner(taskSets.get(i), vehicles);
                Long currCost = agentPlanner.getOptimalCost();


                Task[] newTasks = new Task[taskSetSize+1];

                Iterator<Task> taskIt = taskSets.get(i).iterator();
                for(int j = 0;j<taskSetSize; j++){
                    Task t = taskIt.next();
                    newTasks[j] = new Task(j,t.pickupCity,t.deliveryCity,t.reward,t.weight);
                }
                newTasks[taskSetSize] = pendingTask;

                TaskSet newTaskSet = TaskSet.create(newTasks);

                AgentPlanner newPlanner = new AgentPlanner(newTaskSet, vehicles);
                Long newCost = newPlanner.getOptimalCost();

                tmp += (newCost-currCost)+",";
            }

            tmp = tmp.substring(0, tmp.length()-1) + "]";
            System.out.println(tmp);

        }


    }




}
