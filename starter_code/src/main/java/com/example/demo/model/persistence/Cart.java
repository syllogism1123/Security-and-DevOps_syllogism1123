package com.example.demo.model.persistence;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    @Column
    private Long id;

    @ManyToMany
    @JsonProperty
    @Column
    private List<Item> items;

    @OneToOne(mappedBy = "cart")
    @JsonProperty
    private Customer customer;

    @Column
    @JsonProperty
    private BigDecimal total;

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void addItem(Item item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
        if (total == null) {
            total = new BigDecimal(0);
        }
        total = total.add(BigDecimal.valueOf(item.getPrice()));
    }

    public void removeItem(Item item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.remove(item);
        if (total == null) {
            total = new BigDecimal(0);
        }
        total = total.subtract(BigDecimal.valueOf(item.getPrice()));
    }
}
