package com.research.protrike.CustomObjects;

import java.util.HashMap;

public class TricycleFareObject {
    HashMap<TF, Float> tricycleFare;

    public TricycleFareObject() {
        tricycleFare = new HashMap<>();
    }

    public enum TF {
        MIN_FARE_DIST,
        SUCCEEDING_DIST,
        DISCOUNTED_STARTING_FARE,
        DISCOUNTED_SUCCEEDING_FARE,
        NORMAL_STARTING_FARE,
        NORMAL_SUCCEEDING_FARE,
        THREE_TO_FIVE_STARTING_FARE,
        THREE_TO_FIVE_SUCCEEDING_FARE
    }

    public Float get(TF key) {
        return tricycleFare.get(key);
    }

    public void put(TF key, Float value){
        tricycleFare.put(key, value);
    }

    public void clear(){
        tricycleFare.clear();
    }
}
