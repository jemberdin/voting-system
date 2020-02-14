package com.jemberdin.votingsystem;

import com.jemberdin.votingsystem.TestMatchers;
import com.jemberdin.votingsystem.model.Dish;

import static com.jemberdin.votingsystem.model.AbstractBaseEntity.START_SEQ;

public class DishTestData {

    public static final int DISH1_ID = START_SEQ + 15;

    public static final Dish DISH1 = new Dish(DISH1_ID, "House salad", 350);
    public static final Dish DISH2 = new Dish(DISH1_ID + 1, "Steak-Frites", 2000);
    public static final Dish DISH3 = new Dish(DISH1_ID + 6, "Ice cream & sorbet", 450);

    public static Dish getNew() {
        return new Dish(null, "New dish", 500);
    }

    public static Dish getUpdated() {
        return new Dish(DISH1_ID, "Updated dish", 400);
    }

    public static TestMatchers<Dish> DISH_MATCHERS = TestMatchers.useFieldsComparator(Dish.class, "menu");
}
