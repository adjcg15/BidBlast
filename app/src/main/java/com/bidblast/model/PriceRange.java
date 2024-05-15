package com.bidblast.model;

import java.util.Objects;

public class PriceRange {
    private String label;
    private float minimumAmount;
    private float maximumAmount;

    public PriceRange() { }

    public PriceRange(String label, float minimumAmount, float maximumAmount) {
        this.label = label;
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(float minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public float getMaximumAmount() {
        return maximumAmount;
    }

    public void setMaximumAmount(float maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceRange that = (PriceRange) o;
        return Float.compare(that.minimumAmount, minimumAmount) == 0 && Float.compare(that.maximumAmount, maximumAmount) == 0 && Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, minimumAmount, maximumAmount);
    }
}
