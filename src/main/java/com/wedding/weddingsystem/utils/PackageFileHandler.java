package com.wedding.weddingsystem.utils;

import com.wedding.weddingsystem.models.BasicPackage;
import com.wedding.weddingsystem.models.CustomPackage;
import com.wedding.weddingsystem.models.PremiumPackage;
import com.wedding.weddingsystem.models.ServicePackage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Utility class for all file handling operations on packages.txt
 * Demonstrates: File Handling with BufferedReader/BufferedWriter + proper error handling
 */
public class PackageFileHandler {

    // Path to the data file — adjust this to match your project structure
    private static final String FILE_PATH = "src/main/webapp/data/packages.txt";

    /**
     * CREATE — Save a new package to packages.txt
     */
    public static boolean savePackage(ServicePackage pkg) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(pkg.toFileString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("Error saving package: " + e.getMessage());
            return false;
        }
    }

    /**
     * READ — Read all packages from packages.txt and return as a list
     */
    public static List<ServicePackage> readAllPackages() {
        List<ServicePackage> packages = new ArrayList<>();
        File file = new File(FILE_PATH);

        // If file doesn't exist yet, return empty list (no error)
        if (!file.exists()) return packages;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // skip blank lines
                ServicePackage pkg = parseLine(line);
                if (pkg != null) packages.add(pkg);
            }
        } catch (IOException e) {
            System.err.println("Error reading packages: " + e.getMessage());
        }

        return packages;
    }

    /**
     * READ — Find a single package by its ID
     */
    public static ServicePackage findById(String packageId) {
        List<ServicePackage> all = readAllPackages();
        for (ServicePackage pkg : all) {
            if (pkg.getPackageId().equals(packageId)) {
                return pkg;
            }
        }
        return null; // Not found
    }

    /**
     * READ — Find all packages belonging to a specific vendor
     */
    public static List<ServicePackage> findByVendorId(String vendorId) {
        List<ServicePackage> result = new ArrayList<>();
        for (ServicePackage pkg : readAllPackages()) {
            if (pkg.getVendorId().equals(vendorId)) {
                result.add(pkg);
            }
        }
        return result;
    }

    /**
     * UPDATE — Update an existing package by rewriting the entire file
     */
    public static boolean updatePackage(ServicePackage updatedPkg) {
        List<ServicePackage> all = readAllPackages();
        boolean found = false;

        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getPackageId().equals(updatedPkg.getPackageId())) {
                all.set(i, updatedPkg); // Replace the old one
                found = true;
                break;
            }
        }

        if (!found) return false;

        // Rewrite the entire file with updated data
        return rewriteFile(all);
    }

    /**
     * DELETE — Remove a package by ID, rewrite file without it
     */
    public static boolean deletePackage(String packageId) {
        List<ServicePackage> all = readAllPackages();
        boolean removed = all.removeIf(pkg -> pkg.getPackageId().equals(packageId));

        if (!removed) return false;

        return rewriteFile(all);
    }

    /**
     * Helper: Rewrite the entire packages.txt file from a list
     */
    private static boolean rewriteFile(List<ServicePackage> packages) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (ServicePackage pkg : packages) {
                writer.write(pkg.toFileString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error rewriting packages file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper: Parse one CSV line from packages.txt into the correct subclass
     *
     * File format:
     * packageId,vendorId,name,description,price,inclusions(pipe-separated),tier
     * Example:
     * PKG001,V001,Gold Wedding,Full day coverage,50000,Photos|Videos|Album,PREMIUM
     */
    private static ServicePackage parseLine(String line) {
        try {
            // Split by comma, but only the first 6 commas (inclusions may contain commas if misused)
            String[] parts = line.split(",", 7);
            if (parts.length < 7) return null;

            String packageId  = parts[0].trim();
            String vendorId   = parts[1].trim();
            String name       = parts[2].trim();
            String description= parts[3].trim();
            double price      = Double.parseDouble(parts[4].trim());
            List<String> inclusions = ServicePackage.parseInclusions(parts[5].trim());
            String tier       = parts[6].trim();

            // Polymorphism via factory: construct the correct subclass based on tier
            switch (tier.toUpperCase()) {
                case "BASIC":
                    return new BasicPackage(packageId, vendorId, name, description, price, inclusions);
                case "PREMIUM":
                    return new PremiumPackage(packageId, vendorId, name, description, price, inclusions);
                case "CUSTOM":
                    return new CustomPackage(packageId, vendorId, name, description, price, inclusions, "");
                default:
                    return new BasicPackage(packageId, vendorId, name, description, price, inclusions);
            }
        } catch (Exception e) {
            System.err.println("Error parsing line: " + line + " | " + e.getMessage());
            return null;
        }
    }

    /**
     * Utility: Generate a unique package ID
     */
    public static String generatePackageId() {
        return "PKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

