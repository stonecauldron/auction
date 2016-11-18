package opponent;

import history.GameHistory;

/**
 * Created by noodle on 18.11.16.
 *
 *
 * It's instanciated with PlayerHistory, UnknownParameter,
 *
 * Decorates opponent.UnkownParameters to provide functions:
 * - for a given task it return a map bid => probability
 *
 * bid => opponentGain (using our model)
 *
 *
 * The centralized planning singleton informs about the the marginal cost of the task for the opponent.
 * Using the relative difference from history to our model we can establish  a distribution function that
 * bind a bid to a probability.
 *
 *
 * So this module constructs 2 data :

 *
 *
 * With this 2 predictions, plus a threshold 0-gain bid given as parameter (it’s our marginal cost),
 * the class is able to supply a bid that maximize the the average difference between our gain and the opponent gain.
 * i.e : if we loose 3 by taking a task and our opponent win 5 by taking the same task, we do the job (blocking bid).

 */

public class BidAnalysis {






    private GameHistory gameHistory = null;





    public BidAnalysis(GameHistory gameHisto){
        this.gameHistory = gameHisto;
    }




    public Long getBid(int marginalCost){

        return 0l;
    }



}
