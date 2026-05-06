package com.wedding.weddingsystem.models;

import java.util.List;

/**
 * PremiumPackage — mid-tier package with 10% discount.
 * Demonstrates: Inheritance (extends ServicePackage), Polymorphism (overrides calculateFinalPrice)
 */
public class PremiumPackage extends ServicePackage {

    private static final double DISCOUNT_RATE = 0.10; // 10% discount

    public PremiumPackage(String packageId, String vendorId, String name,
                          String description, double price, List<String> inclusions) {
        super(packageId, vendorId, name, description, price, inclusions);
    }

    public PremiumPackage() {
        super();
    }

    /**
     * Polymorphism: Premium tier gets a 10% discount applied to base price.
     */
    @Override
    public double calculateFinalPrice() {
        return getPrice() * (1 - DISCOUNT_RATE); // 10% off
    }

    /**
     * Polymorphism: Returns tier label.
     */
    @Override
    public String getPackageTier() {
        return "PREMIUM";
    }

    public double getDiscountRate() {
        return DISCOUNT_RATE;
    }
}

