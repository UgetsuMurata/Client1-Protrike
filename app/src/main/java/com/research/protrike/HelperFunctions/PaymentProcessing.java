package com.research.protrike.HelperFunctions;

import android.util.Log;

import com.research.protrike.Application.Protrike;
import com.research.protrike.CustomObjects.TricycleFareObject;
import com.research.protrike.CustomObjects.TricycleFareObject.TF;

public class PaymentProcessing {
    public enum Discount {
        NONE, DISCOUNTED
    }

    public static Float distanceToCosting(Float distance, Discount discount) {
        Protrike protrike = Protrike.getInstance();
        TricycleFareObject tricycleFareObject = protrike.getTricycleFare();

        Float startingFare = tricycleFareObject.get(TF.NORMAL_STARTING_FARE);
        Float succeedingFare = tricycleFareObject.get(TF.NORMAL_SUCCEEDING_FARE);

        if (discount == Discount.DISCOUNTED) {
            startingFare = tricycleFareObject.get(TF.DISCOUNTED_STARTING_FARE);
            succeedingFare = tricycleFareObject.get(TF.DISCOUNTED_SUCCEEDING_FARE);
        }

        Log.d("Distance", distance + "m");
        if (distance <= tricycleFareObject.get(TF.MIN_FARE_DIST) * 1000) {
            Log.d(String.format("Before %skm", tricycleFareObject.get(TF.MIN_FARE_DIST) * 1000), "just showing starting fare.");
            return startingFare;
        } else {
            int succeededKM = (int) Math.ceil((distance - (tricycleFareObject.get(TF.MIN_FARE_DIST)*1000)) / 1000);
            Log.d("After " + (tricycleFareObject.get(TF.MIN_FARE_DIST) * 1000) + "km", succeededKM + "km succeeded!");
            return startingFare + (succeedingFare * succeededKM);
        }
    }
}
