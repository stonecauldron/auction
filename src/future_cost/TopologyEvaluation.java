package future_cost;

import logist.topology.Topology;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by noodle on 18.11.16.
 *
 * This module evaluates how the current city of the vehicle influences the km
 * the cost added in a delivery (relative to cheapest deliver) is the distance :
 * current city => pickup position.
 *
 * This cost is usefull for small capacity vehicle that deliver task by task
 * and does not want to make an immediate rewarded deliver that lead to a dead end street
 * i.e :
 * if the deliver is at the end of the world
 * we will have to wait for a new task : end_of_the_world => centeredCity
 * to get a new rewarded deliver.
 *
 *
 *
 * TODO :
 * - the class is not implemented, and have to follow the MDP fashion
 * - it should take care about
 * Set<ROAD_TAKEN_AT_LEAST_ONCE> for the current TaskSet
 * Set<ROAD_TAKEN_AT_LEAST_ONCE> for the new TaskSet
 * => if new cities are visited between the two set, compute the diff cost :
 *  (newTaskSet : SUM city.cost /totalCities)  -  (currTaskSet : SUM city.cost /totalCities)
 * or something like that.
 *
 *
 */

public class TopologyEvaluation {






    private List<Node> nodes = null;

    private Map<Topology.City,Map<Topology.City,Double>> deliverDistrib = null;

    private Map<Topology.City, Node> cityToNode = null;







    public TopologyEvaluation(Topology topo){


        this.deliverDistrib = new HashMap<>();

        double uniformFreq = 1/(topo.cities().size()*(topo.cities().size()-1));

        for(Topology.City pickupCity : topo.cities()){

            this.deliverDistrib.put(pickupCity, new HashMap<>());

            for(Topology.City deliverCity : topo.cities()){
                if(pickupCity != deliverCity){

                    this.deliverDistrib.get(pickupCity).put(deliverCity,uniformFreq);
                }
            }
        }


        cityToNode = new HashMap<>();

        for(Topology.City city: topo.cities()){
            cityToNode.put(city,new Node(city));
        }


    }


    public TopologyEvaluation(Map<Topology.City,Map<Topology.City,Double>> distrib){

        this.deliverDistrib = distrib;


        cityToNode = new HashMap<>();

        for(Topology.City city: distrib.keySet()){
            cityToNode.put(city,new Node(city));
        }

    }







    private class Node {



        public Topology.City city = null;

        public double immediateCost = 0l;

        public double cost = 0l;


        public Node(Topology.City city){

            this.city = city;

            for(Topology.City pickupCity : deliverDistrib.keySet()){
                for(Topology.City deliverCity : deliverDistrib.get(pickupCity).keySet()){

                    immediateCost += deliverDistrib.get(pickupCity).get(deliverCity)
                                     *  city.distanceTo(pickupCity);

                }
            }

            this.cost = immediateCost;
        }




    }









}
