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
 */
public class TrunkEvaluation {
}
