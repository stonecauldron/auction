package opponent;

import history.GameHistory;
import history.PlayerHistory;

import java.util.List;

/**
 * Created by noodle on 18.11.16.
 *
 *
 *
 * Create k models with 3 to 5 vehicles without taking care too much about first place vehicles and other
 * parameters as long they did not affect the average on a long time game.
 *
 *
 * Using the opponent's player history the class has to provide a guess over the unknown parameters
 * defined for the opponent, ie:
 *  - total numbers of vehicles (3 to 5)
 *  - related capacities
 *
 *
 * This class use history and considers that a player never bid lower than
 * his marginal cost. Then it run the k models defined in the class and run
 * a centralized planning to estimate :
 * 1. the total optimal cost of the opponent
 * 2. the total reward win by the opponent
 * It chooses the model which has the more consistency, ie :
 * punish model using on each bid a punishment heuristic by evaluating the difference
 * between the optimal opponent marginal cost (using centralized planning) and real
 * bid asked by the opponent.
 *
 *
 *
 *
 *
 * // TODO : move / remove this paragraph
 * For the model selected in the class,
 * the class maintains a list of difference between the proposed bid of the opponent and the cost
 * we have performed in the opponent situation assuming the selected model as true.
 * i.e : if the difference equals 5, so, either the opponent has realized 5 gain on the task
 * or the opponent has less accuracy of 5. This list will help to predict the bid of the opponent.
 *
 * (i.e : we just run centralized planning on opponent history and look if bid are justified)
 */
public class UnkownParameters {







    private ParameterPossibilities parameterPossibilities = null;

    private PlayerHistory playerHistory = null;

    private GameHistory gameHistory = null;






    public UnkownParameters(GameHistory gameHistory,
                            PlayerHistory history,
                            ParameterPossibilities parameterPossibilities){


        this.parameterPossibilities = parameterPossibilities;
        this.playerHistory = history;
        this.gameHistory = gameHistory;
    }







    /**
     * @return the most consistency list of capacities for our opponent according to it's history
     */
    public List<Integer> getEstimatedCapacity(){

        if(gameHistory.size() == 0){
            return parameterPossibilities.get((int)(Math.random()*parameterPossibilities.size());
        }


        return null;
    }










}
