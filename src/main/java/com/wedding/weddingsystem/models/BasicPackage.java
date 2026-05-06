package com.wedding.weddingsystem.models;

import java.util.List;

/**
 * BasicPackage — entry-level package with no discount.
 * Demonstrates: Inheritance (extends ServicePackage), Polymorphism (overrides calculateFinalPrice)
 */
public class BasicPackage extends ServicePackage {

    public BasicPackage(String packageId, String vendorId, String name,
                        String description, double price, List<String> inclusions) {
        super(packageId, vendorId, name, description, price, inclusions);
    }

    public BasicPackage() {
        super();
    }

    /**
     * Polymorphism: Basic tier has no discount — price is as-is.
     */
    @Override
    public double calculateFinalPrice() {
        return getPrice(); // No discount for basic tier
    }

    /**
     * Polymorphism: Returns tier label used for display and file storage.
     */
    @Override
    public String getPackageTier() {
        return "BASIC";
    }
}
