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

    private Long estimatedMargCost = null;







    public BidDistribution(PlayerHistory h, Long estimatedMargCost){

        this.history = h;
        this.estimatedMargCost = estimatedMargCost;


        diff = new ArrayList<>(h.getBids().size());

        if(h.getBids().size() == 0){
            return;
        }


        System.out.println(h.getBids().get(0));

        diff.add( h.getBids().get(0) - h.getAgentPlans().get(0).getOptimalCost());

        for(int i = 1; i<h.getBids().size(); i++){



            Long tmp =  h.getAgentPlans().get(i).getMarginalCost(h.getAgentPlans().get(i-1));

            diff.add( h.getBids().get(i) - tmp);

        }


        Collections.sort(diff);
            System.out.println("############################################"+diff);

    }







    /**
     * @return the min bid that can be done : offset + minDiff
     */
    public Long minBid(){
        return estimatedMargCost+diff.get(0);
    }



    /**
     * @return the given offset(ie: estimatedCost) + exemple_n_i (ie:recorded diff estimation vs opponent bid)
     */
    public Long bid(int i){
        return estimatedMargCost+diff.get(i);
    }




    /**
     * @param bidThreshold
     * @return proba opponent bid lower than the bidThreshold
     * the offset has to be parametrized.
     */
    public double probaBidLowerThan(Long bidThreshold){

        int total = 0;
        for (int i = 0; i < diff.size(); i++) {
            if (bid(i) < bidThreshold) {
                total++;
            }
        }

        return (double)total/diff.size();
    }



    /**
     * @param diffFromEstimationThreshold
     * @return proba opponent bid greater than the bidThreshold
     * the offset has to be parametrized.
     */
    public double probaBidGreaterThan(Long diffFromEstimationThreshold){

        return 1 - probaBidLowerThan(diffFromEstimationThreshold);
    }


    // TODO : review variance
    /**
     * @param heroMarginalCost
     * @return the bid that maximize our reward
     */
    public Long bestBid(Long heroMarginalCost){

        double bestHeroGain = Double.MIN_VALUE;
        Long bestBid = heroMarginalCost;

        for(int i = 0; i<diff.size(); i++){

            // we bid just below to win against curr exemple
            Long heroBid = this.bid(i) -1;

            double heroWinProba = 1 - (double)i/(double)diff.size();

            double heroLooseProba = 1-heroWinProba;

            double gain = heroWinProba*(heroBid-heroMarginalCost)
                          - heroLooseProba*(this.bid(i)-this.estimatedMargCost);

            if(gain > bestHeroGain){
                bestHeroGain = gain;
                bestBid = heroBid;
            }
        }



        return bestBid;
    }


    @Override
    public String toString(){

        return "diff size : " +diff.size() +" / estimatedMargCost : "+this.estimatedMargCost;
    }


}
