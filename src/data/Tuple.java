package data;

/**
 * Created by noodle on 25.11.16.
 */
public class Tuple<T,V> {


    private T t;
    private V v;

    public Tuple(T t, V v){
        this.t = t;
        this.v = v;
    }

    public T get_1(){
        return t;
    }

    public V get_2(){
        return v;
    }



    @Override
    public int hashCode(){
        return t.hashCode() + 31*v.hashCode();
    }

    @Override
    public boolean equals(Object that){

        return (that instanceof Tuple) ?
                ((Tuple)that).get_1().equals(this.get_1()) && ((Tuple)that).get_2().equals(this.get_2())
                : false;
    }


}

