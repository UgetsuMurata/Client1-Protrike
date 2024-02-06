package com.research.protrike.HelperFunctions;

import com.research.protrike.Application.Protrike;
import com.research.protrike.CustomObjects.TricycleFareObject;
import com.research.protrike.CustomObjects.TricycleFareObject.TF;

public class PaymentProcessing {
    public enum Discount {
        NONE, STUDENT, SENIOR_CITIZEN, PWD, CHILD
    }

    public static Float distanceToCosting(Float distance, Discount discount) {
        Protrike protrike = Protrike.getInstance();
        TricycleFareObject tricycleFareObject = protrike.getTricycleFare();

        Float startingFare = tricycleFareObject.get(TF.NORMAL_STARTING_FARE);
        Float succeedingFare = tricycleFareObject.get(TF.NORMAL_SUCCEEDING_FARE);

        if (discount == Discount.STUDENT || discount == Discount.SENIOR_CITIZEN || discount == Discount.PWD) {
            startingFare = tricycleFareObject.get(TF.DISCOUNTED_STARTING_FARE);
            succeedingFare = tricycleFareObject.get(TF.DISCOUNTED_SUCCEEDING_FARE);
        } else if (discount == Discount.CHILD) {
            startingFare = tricycleFareObject.get(TF.THREE_TO_FIVE_STARTING_FARE);
            succeedingFare = tricycleFareObject.get(TF.THREE_TO_FIVE_SUCCEEDING_FARE);
        }

        if (distance <= tricycleFareObject.get(TF.MIN_FARE_DIST) * 1000) {
            return startingFare;
        } else {
            int succeededKM = (int) Math.ceil(distance / (tricycleFareObject.get(TF.NORMAL_STARTING_FARE) * 1000)) - 1;
            return startingFare + (succeedingFare * succeededKM);
        }
    }
}
