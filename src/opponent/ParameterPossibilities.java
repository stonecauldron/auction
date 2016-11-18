package opponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by noodle on 18.11.16.
 *
 * Creates possibles opponent parameters (total vehicles and related capacities)
 *
 */

public class ParameterPossibilities {




    private List<List<Integer>> possibilities = null;




    /**
     * @param cumulatedCapacity (we assume opponent has the same vehicle capacity as us)
     */
    public ParameterPossibilities(int cumulatedCapacity){


        possibilities = new ArrayList<>();

        for(int totalVehicles=2; totalVehicles<=5; totalVehicles++){


            int uniformCapacity = (int)((double)cumulatedCapacity/totalVehicles);


            // ADD to possibilities : opponent has totalVehicles of same size
            List<Integer> uniform = new ArrayList<>(totalVehicles);

            for(int i = 0; i< totalVehicles; i++){
                uniform.add(uniformCapacity);
            }

            possibilities.add(uniform);


            // ADD to possibilities : opponent has total vehicles of unbalanced size
            List<Integer> unbalancedVehicles = new ArrayList<>();

            for(int i =0; i < totalVehicles/2; i++){
                unbalancedVehicles.add(uniformCapacity/2);
            }

            for(int i =totalVehicles/2; i < totalVehicles; i++){
                unbalancedVehicles.add(uniformCapacity*3/2);
            }

        }


    }




    /**
     * @param i
     * @return a capacities parameters possibility
     */
    public List<Integer> get(int i){
        return possibilities.get(i);
    }


    /**
     * @return the size of opponent parameters possibilities
     */
    public int size(){
        return possibilities.size();
    }


}
