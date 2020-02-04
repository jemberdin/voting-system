package com.jemberdin.votingsystem.util;

import com.jemberdin.votingsystem.model.Dish;
import com.jemberdin.votingsystem.to.DishTo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DishUtil {

    public static List<DishTo> getTos(Collection<Dish> dishes) {
        return dishes.stream().map(DishUtil::createTo).collect(Collectors.toList());
    }

    public static DishTo createTo(Dish dish) {
        return new DishTo(dish.getName(), dish.getPrice());
    }
}
