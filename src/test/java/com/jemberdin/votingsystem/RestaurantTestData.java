package com.jemberdin.votingsystem;

import com.jemberdin.votingsystem.model.Restaurant;
import com.jemberdin.votingsystem.to.RestaurantTo;

import static com.jemberdin.votingsystem.model.AbstractBaseEntity.START_SEQ;

public class RestaurantTestData {

    public static final int RESTAURANT1_ID = START_SEQ + 3;
    public static final int RESTAURANT2_ID = RESTAURANT1_ID + 1;
    public static final int RESTAURANT3_ID = RESTAURANT1_ID + 2;
    public static final int RESTAURANT4_ID = RESTAURANT1_ID + 43;

    public static final Restaurant RESTAURANT1 = new Restaurant(RESTAURANT1_ID, "French Restaurant");
    public static final Restaurant RESTAURANT2 = new Restaurant(RESTAURANT2_ID, "Chinese Restaurant");
    public static final Restaurant RESTAURANT3 = new Restaurant(RESTAURANT3_ID, "Italian Restaurant");
    public static final Restaurant RESTAURANT4 = new Restaurant(RESTAURANT4_ID, "Estonian Restaurant");

    public static Restaurant getNew() {
        return new Restaurant(null, "New Restaurant");
    }

    public static Restaurant getUpdated() {
        return new Restaurant(RESTAURANT1_ID, "Updated Restaurant");
    }

    public static TestMatchers<Restaurant> RESTAURANT_MATCHERS = TestMatchers.useFieldsComparator(Restaurant.class, "votes");
    public static TestMatchers<RestaurantTo> RESTAURANT_TO_MATCHERS = TestMatchers.useEquals(RestaurantTo.class);
}
