package com.sougata.train_booking.global;

public class Variables {
    private static int noOfCoaches;

    public static int getNoOfCoaches() {
        return noOfCoaches;
    }

    public static int incrementCoaches() {
        noOfCoaches += 1;
        return noOfCoaches;
    }

    public static int decrementCoaches() {
        noOfCoaches -= 1;
        return noOfCoaches;
    }
}
