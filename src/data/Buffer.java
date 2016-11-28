package data;

import java.util.Iterator;

/**
 * Created by noodle on 18.11.16.
 *
 * Creates a List of fixed size erasing oldest
 * values when the size has reached the maximal capacity.
 * It supply access method to retrieve newest value first in a stack fashion way.
 *
 */
public class Buffer<T> implements Iterable<T> {






    private T[] array = null;

    private int relativePosition = 0;

    private int size = 0;






    public Buffer(int capacity){


        this.array = (T[]) new Object[capacity];

    }



    private Buffer(T[] array, int size, int relativePosition){


        this.array = array;
        this.size = size;
        this.relativePosition = relativePosition;

    }






    /**
     * @param v
     * add the value v to the buffer overwriting the oldest added value if the buffer array
     * has already reach is maximal capacity.
     */
    public Buffer put(T v){

        T[] newArray = array.clone();
        newArray[relativePosition] = v;

        int newRelativePosition = (relativePosition + 1)%array.length;

        int newSize = Math.min(size+1, array.length);

        return new Buffer(newArray,newRelativePosition,newSize);
    }


    /**
     *
      * @return the size of the buffer
     */
    public int size(){
        return size;
    }


    /**
     *
     * @param k
     * @return the kth newest added value
     */
    public T get(int k){

        if(k>=size()){
            throw new IndexOutOfBoundsException();
        }

        return array[(array.length+relativePosition-1-k)%array.length];
    }



    /**
     *
     * @return an iterator newest to oldest added values
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            int i = 0;

            @Override
            public boolean hasNext() {
                return i<Buffer.this.size();
            }

            @Override
            public T next() {
                T t = Buffer.this.get(i);
                i++;
                return t;
            }

        };
    }



    @Override
    public String toString(){

        StringBuilder sB = new StringBuilder();

        for(T t : this){
            sB.append(t+",");
        }

        return "[" + sB.substring(0,sB.length()-1)+"]";
    }



}
