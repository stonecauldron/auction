package centralized_panning;

/**
 * Created by noodle on 18.11.16.
 *
 * It has to solve the following :
 * for a remaining capacity and a set of already accepted states
 * => additional future gain/loss of adding task path to current plan (regarding to future task proposed with the distrib)
 *    additional future gain/loss due to capacity evolution
 *
 *
 * It should be inspired from below (explanation below need more reflexion), and used to choose the best neighbor
 * during 'simulated annealing' :
 *
 *
 * Run markov over the task distribution, and evaluates city effect on reward
 * This module is an additional feature to centralized planning, it help to add
 * or remove gain accordingly to the last city on a solution with the new pending
 * task (not yet bidded task) with the previous one, ie :
 *
 * in centralized convergence take care about :
 * diff_gain = last_city_with_the_new_pending_task - curr_ending_city_average
 *
 */

public class TopologyEvaluation {






    private static TopologyEvaluation instance = new TopologyEvaluation();





    private TopologyEvaluation(){

        if(instance != null){
            throw new IllegalAccessError();
        }

    }





    public static TopologyEvaluation getInstance(){
        return instance;
    }







    /**
     * @param fromSol
     * @param toSol
     * @return the future gain affect between the 2 solutions given as parameter.
     */
    public Integer futureGainAffect(Solution fromSol, Solution toSol){
        return null;
    }





}
