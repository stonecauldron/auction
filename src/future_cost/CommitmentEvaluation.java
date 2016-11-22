package future_cost;

/**
 * Created by noodle on 20.11.16.
 *
 *
 * if a vehicle is not doing anything, and we add a task to deliver :
 * so in the future : we could carry out 2 or more tasks
 * this is cheaper than carry out only one task
 * because we can perfom planning to optimize empty trunk move.
 *
 * The more tasks we plan to deliver : the more cheap will be the deliver,
 * typically if distribution give us one time Paris => Marseille
 * until this task is processed we got no additional km if Paris=>Marseille is drawn again.
 *
 *
 *
 * for a new Task:
 * withoutNewTaskSet, withNewTaskSet => benefit
 *
 * next_average_marginal_cost(_taskSet : TaskSet) =
 * for _t in {all possible task(contract)}:
 * - 1./ evaluate the benefit adding the task _t in our current _taskSet => marginal cost
 * - 2./ weight the above value with the probability to get the contract
 * - 3./ sum all
 *
 * compute : next_average_marginal_cost(withNewTaskSet) - next_average_marginal_cost(withoutNewTaskSet)
 *
 *
 * note : here the algorithm the algorithm see one step before, which is, in a single player game,
 * true, because we want to perform a task if it makes profit.
 * But, in the second game player, it could be interesting to know the upline, when trunk becomes
 * full. In this way we could bid first with immediate loss in the aim to get low cost deliver in the future
 * blocking the adversary that can't bid lower than it's immediate cost.
 *
 *
 * We evaluate this value in this way :
 * evaluate(_sampleSize, _planSize) : (here sampleSize need to be sufficient to has converged value, _plansize to have many task (over solicited))
 * 1./ create _sampleSize list of taskSet containing _planSize task
 * 2./ for each sample, run : next_average_marginal_cost
 * 3./ average
 *
 *
 * say following the value :
 * difference : empty_taskSet - overSollicited, called into function : next_average_marginal_cost;
 * is called commitmentGap
 *
 *
 * we can always bet considering to improve directly from our taskSet to the overSollicitedTaskSet :
 *
 * first case : if our opponent is already in overSolicitedTaskSet and we are starting the game:
 * betting this difference insure progress to overSolicitedTaskSet state or loss to our opponent.
 *
 * second case : we are both in initial state :
 * if the opponent has the same strategy we pay both to reach the finalState
 * if the opponent play without : we reach finalState and we enjoy the gap (having better marginal cost for almost all task)
 *
 *
 */
public class CommitmentEvaluation {
















}
