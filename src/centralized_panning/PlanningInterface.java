package centralized_panning;

/**
 * Created by noodle on 17.11.16.
 *
 *
 *
 * It provides a plan for input : <vehicles, tasks>,
 * and the coresponding using centralized planning.
 * It’s implemented as a singleton.
 *
 *
 *
 * TODO :
 *
 * add a function shuffle to move to a distant solution for the current one.
 * Make this operation after reached a local minimum to retry to another place in
 * solutions space. Keep in memory the minimum cost encountered.
 *
 */

public class PlanningInterface {





    private static PlanningInterface instance = new PlanningInterface();





    private PlanningInterface(){

        if(instance != null){
            throw new IllegalAccessError();
        }

    }




    public static PlanningInterface getInstance(){
        return instance;
    }


    // TODO : adapt solution to provide the optimal planning for a PlayerHistory


}
