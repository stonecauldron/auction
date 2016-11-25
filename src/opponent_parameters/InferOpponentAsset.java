package opponent_parameters;

import context.PlayerHistory;
import exceptions.NoSolutionException;
import logist.simulation.Vehicle;
import logist.topology.Topology;
import planning.AgentPlanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noodle on 22.11.16.
 *
 *
 */
public class InferOpponentAsset {




    private AssetPossibilities assetPossibilities = null;





    public InferOpponentAsset(int cumuledCap,
                              int minVehicle,
                              int maxVehicle,
                              int costPerKm,
                              Topology topo){


        this.assetPossibilities = new AssetPossibilities(
                cumuledCap,
                minVehicle,
                maxVehicle,
                costPerKm,
                topo);

    }


    /**
     * TODO :
     * in gameHistory : bids / AgentPlan(estimation : asset dependant)
     * each time we choose the asset that fit the best and perform our analysis on it.
     * So in our history : AgentPlan has different asset.
     *
     * ie : oldest analysis does not use the same asset
     *
     * Then : it's a little bit screwed, but the computation is less intensive,
     * plus, we don't care too much as long we get an estimation, with, opponent bid distribution
     * surrounding our prediction. (feature : marginal_cost_on_best_asset_by_exemple looking
     * for the distrib arround the feature in the history, we loose precision in the sens that
     * we prefer to get the marginal_cost_on_curr_best_asset_for_all_exemples)
     *
     *
     *
     */
    public List<Vehicle> inferAsset(PlayerHistory history) throws NoSolutionException {


        Long minError = Long.MAX_VALUE;
        List<Vehicle> bestAsset = null;

        Long error = 0l;

        // TODO : think about : newest errors are more important (initial city)
        // (buffer.get(i) : ith newest)

        for(List<Vehicle> asset : this.assetPossibilities) {

            List<AgentPlanner> planners = new ArrayList<>();

            for (int i = 0; i < history.getBids().size(); i++) {

                planners.add(new AgentPlanner(history.getAgentPlans().get(i).getCommitedTask(), asset));

            }

            for(int i = 1; i<history.getBids().size(); i++){
                AgentPlanner prevPlanner = planners.get(i-1);
                Long margCostEstim = planners.get(i).getMarginalCost(prevPlanner);

                error += margCostEstim - history.getBids().get(i);

            }

            if(error < minError){
                minError = error;
                bestAsset = asset;
            }

        }



        return bestAsset;
    }





}
