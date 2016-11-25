package bid;

import context.PlayerHistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * Created by noodle on 19.11.16.
 *
 * ourModel => k
 * trainingSet => a
 *
 */
public class BidDistribution {






    private PlayerHistory history = null;

    private List<Long> diff = null;

    private Long offset = 0l;







    public BidDistribution(PlayerHistory h){

        this.history = h;


        diff = new ArrayList<>(h.getBids().size());

        if(h.getBids().size() == 0){
            return;
        }

        diff.add( h.getBids().get(0) - h.getAgentPlans().get(0).getOptimalCost());

        for(int i = 1; i<h.getBids().size(); i++){

            diff.add( h.getBids().get(i) - h.getAgentPlans().get(i).getMarginalCost(h.getAgentPlans().get(i-1)));

        }


        Collections.sort(diff);

    }





    /**
     * @param l used to pass an estimated cost as offset
     */
    public void setOffset(Long l){
        this.offset = l;
    }



    /**
     * @return the min bid that can be done : offset + minDiff
     */
    public Long minBid(){
        return offset+diff.get(0);
    }

    /**
     * @return the given offset(ie: estimatedCost) + exemple_n_i (ie:recorded diff estimation vs opponent bid)
     */
    public Long bid(int i){
        return offset+diff.get(i);
    }


    /**
     * @param bidThreshold
     * @return proba opponent bid lower than the bidThreshold
     * the offset has to be parametrized.
     */
    public double probaBidLowerThan(Long bidThreshold){

        int i = 0;
        while(i<diff.size() && this.bid(i)<bidThreshold);

        return (double)i/diff.size();
    }



    /**
     * @param diffFromEstimationThreshold
     * @return proba opponent bid greater than the bidThreshold
     * the offset has to be parametrized.
     */
    public double probaBidGreaterThan(Long diffFromEstimationThreshold){

        return 1 - probaBidLowerThan(diffFromEstimationThreshold);
    }



}
