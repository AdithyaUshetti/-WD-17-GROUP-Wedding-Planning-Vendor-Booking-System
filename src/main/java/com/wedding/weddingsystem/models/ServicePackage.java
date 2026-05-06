package com.wedding.weddingsystem.models;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract base class for all service packages.
 * Demonstrates: Encapsulation (private fields + getters/setters), Abstraction (abstract method)
 */
public abstract class ServicePackage {

    // Encapsulation: all fields are private
    private String packageId;
    private String vendorId;
    private String name;
    private String description;
    private double price;
    private List<String> inclusions;

    // Constructor
    public ServicePackage(String packageId, String vendorId, String name,
                          String description, double price, List<String> inclusions) {
        this.packageId = packageId;
        this.vendorId = vendorId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.inclusions = inclusions;
    }

    // Default constructor
    public ServicePackage() {}

    // Abstract method — Abstraction + Polymorphism
    // Each subclass MUST implement this differently
    public abstract double calculateFinalPrice();

    // Abstract method to get the tier/type label
    public abstract String getPackageTier();

    // Getters and Setters — Encapsulation
    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }

    public String getVendorId() { return vendorId; }
    public void setVendorId(String vendorId) { this.vendorId = vendorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public List<String> getInclusions() { return inclusions; }
    public void setInclusions(List<String> inclusions) { this.inclusions = inclusions; }

    // Helper: Convert inclusions list to pipe-separated string for file storage
    public String getInclusionsAsString() {
        if (inclusions == null) return "";
        return String.join("|", inclusions);
    }

    // Helper: Convert pipe-separated string back to list
    public static List<String> parseInclusions(String raw) {
        if (raw == null || raw.trim().isEmpty()) return List.of();
        return Arrays.asList(raw.split("\\|"));
    }

    // Convert object to CSV line for .txt file storage
    public String toFileString() {
        return packageId + "," + vendorId + "," + name + "," +
                description + "," + price + "," + getInclusionsAsString() +
                "," + getPackageTier();
    }

    @Override
    public String toString() {
        return "ServicePackage{" +
                "packageId='" + packageId + '\'' +
                ", name='" + name + '\'' +
                ", tier='" + getPackageTier() + '\'' +
                ", finalPrice=" + calculateFinalPrice() +
                '}';
    }
}