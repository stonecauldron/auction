package bid;

import history.PlayerHistory;

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





    private List<Long> diff = null;

    private Long mean;

    private Long offset = 0l;





    public BidDistribution(PlayerHistory h, List<Long> estimation){

        List<Long> exemples = new ArrayList<>();

        for(Long l : h.getBids()){
            exemples.add(l);
        }

        if(exemples.size() != estimation.size()){
            throw new IllegalArgumentException();
        }


        diff = new ArrayList<>();
        mean = 0l;

        for(int i = 0; i<exemples.size(); i++){
            diff.add(estimation.get(i) - exemples.get(i));
            mean += estimation.get(i) - exemples.get(i);
        }

        Collections.sort(diff);
        mean = mean/exemples.size();
    }





    /**
     * @param offset used to pass an estimated cost as offset
     */
    public void setOffset(Long l){
        this.offset = l;
    }


    /**
     * @return the offset (estimated cost) and the mean difference between real life values
     * and our estimation (from exemples).
     */
    public Long mean(){
        return offset+mean;
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
     * @param bidThreshold
     * @return proba opponent bid greater than the bidThreshold
     * the offset has to be parametrized.
     */
    public double probaBidGreaterThan(Long diffFromEstimationThreshold){

        return 1 - probaBidLowerThan(diffFromEstimationThreshold);
    }



}
