package com.jemberdin.votingsystem.model;

import com.jemberdin.votingsystem.View;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "dishes", uniqueConstraints = {@UniqueConstraint(columnNames = {"menu_id", "name"}, name = "dishes_unique_menu_name_idx")})
public class Dish extends AbstractNamedEntity {

    @Column(name = "price", nullable = false)
    @NotNull
    @Range(min = 0, max = 1_000_000)
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull(groups = View.Persist.class)
    protected Menu menu;

    public Dish() { }

    public Dish(Integer id, String name, int price) {
        super(id, name);
        this.price = price;
    }

    public Dish(String name, int price) {
        this(null, name, price);
    }

    public Integer getPrice() {
        return price;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "price=" + price +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
