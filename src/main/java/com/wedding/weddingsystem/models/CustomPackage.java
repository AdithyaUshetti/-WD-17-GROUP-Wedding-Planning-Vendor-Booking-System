package com.wedding.weddingsystem.models;

import java.util.List;

/**
 * CustomPackage — fully tailored top-tier package with 20% discount.
 * Demonstrates: Inheritance (extends ServicePackage), Polymorphism (overrides calculateFinalPrice)
 */
public class CustomPackage extends ServicePackage {

    private static final double DISCOUNT_RATE = 0.20; // 20% discount for custom/luxury tier
    private String customNote; // Extra field only CustomPackage has

    public CustomPackage(String packageId, String vendorId, String name,
                         String description, double price, List<String> inclusions,
                         String customNote) {
        super(packageId, vendorId, name, description, price, inclusions);
        this.customNote = customNote;
    }

    public CustomPackage() {
        super();
    }

    /**
     * Polymorphism: Custom tier gets the highest discount — 20% off base price.
     */
    @Override
    public double calculateFinalPrice() {
        return getPrice() * (1 - DISCOUNT_RATE); // 20% off
    }

    /**
     * Polymorphism: Returns tier label.
     */
    @Override
    public String getPackageTier() {
        return "CUSTOM";
    }

    public String getCustomNote() { return customNote; }
    public void setCustomNote(String customNote) { this.customNote = customNote; }

    public double getDiscountRate() {
        return DISCOUNT_RATE;
    }
}

