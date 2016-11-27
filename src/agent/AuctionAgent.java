package agent;

import bid.Bidder;
import data.Tuple;
import exceptions.NoSolutionException;
import exceptions.NoTaskException;
import logist.agent.Agent;
import logist.behavior.AuctionBehavior;
import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import plan.VehiclePlan;
import planning.AgentPlanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noodle on 27.11.16.
 */
public class AuctionAgent implements AuctionBehavior {





        private Topology topology;

        private TaskDistribution distribution;

        private Agent agent;

        private List<Vehicle> vehicle;

        private Bidder bidder = null;

        private List<Long> estimateBenefit = new ArrayList<>();





        @Override
        public void setup(Topology topology,
                          TaskDistribution distribution,
                          Agent agent) {



            long startMs = System.currentTimeMillis();


            this.topology = topology;

            this.distribution = distribution;

            this.agent = agent;

            this.vehicle = agent.vehicles();


            try {
                this.bidder = new Bidder(this.vehicle, topology, 2, this.agent.id());
            } catch (NoSolutionException e) {
                e.printStackTrace();
            }


            long endMs = System.currentTimeMillis();

            System.out.println("setup duration : " + (endMs-startMs));

        }




        @Override
        public void auctionResult(Task previous, int winner, Long[] bids) {

            long startMs = System.currentTimeMillis();

            try {
                this.bidder = this.bidder.setBidFeedback(bids,winner,previous);
            } catch (NoSolutionException e) {
                e.printStackTrace();
            }

            long endMs = System.currentTimeMillis();

            System.out.println("auction result duration : " + (endMs-startMs));
        }



        @Override
        public Long askPrice(Task task) {

            long startMs = System.currentTimeMillis();

            Long bid = null;

            try {

                this.bidder = this.bidder.setNewPendingTask(task);

                Tuple<Long,Long> bidInfo = this.bidder.getBid();
                bid = bidInfo.get_1();

                // bid - margCost
                estimateBenefit.add(bidInfo.get_1() - bidInfo.get_2());

            } catch (NoSolutionException | NoTaskException e) {
                e.printStackTrace();
            }

            long endMs = System.currentTimeMillis();

            System.out.println("ask bid duration : " + (endMs-startMs));

            return bid;
        }





        @Override
        public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {

            List<Plan> logistPlans = new ArrayList<>();

            Long totalCost = 0l;

            try {

                List<VehiclePlan> plans = new AgentPlanner(tasks, vehicles).getOptimalSolution().getPlans();

                for(VehiclePlan p : plans){
                    logistPlans.add(p.toLogistPlan());
                    totalCost += (long)(p.cost());
                }

            } catch (NoSolutionException e) {
                e.printStackTrace();
            }


            System.out.println(bidder.getGameHistory().getPlayerHistory(bidder.getParameters().primePlayerId()).getCommitedTasks());
            System.out.println(tasks);



            int totalReward = tasks.rewardSum();
            System.out.println("totalTask" + bidder.getGameHistory().getPlayerHistory(bidder.getParameters().primePlayerId()).getCommitedTasks().size());
            System.out.print("bids = "+totalReward);
            System.out.println( " / cost : " + totalCost);

            return logistPlans;
        }



    public static void main(String[] argv){

    }

}
