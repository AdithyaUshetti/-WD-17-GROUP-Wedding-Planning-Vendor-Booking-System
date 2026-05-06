package com.wedding.weddingsystem.servlets;

import com.wedding.weddingsystem.models.BasicPackage;
import com.wedding.weddingsystem.models.CustomPackage;
import com.wedding.weddingsystem.models.PremiumPackage;
import com.wedding.weddingsystem.models.ServicePackage;
import com.wedding.weddingsystem.utils.PackageFileHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * PackageServlet — handles all HTTP requests for Package/Service Management.
 * Routes:
 *   GET  /package?action=list          → show all packages
 *   GET  /package?action=edit&id=XXX   → show edit form
 *   GET  /package?action=delete&id=XXX → delete and redirect
 *   GET  /package?action=add           → show add form
 *   POST /package?action=add           → create new package
 *   POST /package?action=update        → update existing package
 */
@WebServlet("/package")
public class PackageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "list":
                listPackages(request, response);
                break;
            case "add":
                request.getRequestDispatcher("/WEB-INF/views/add-package.jsp")
                        .forward(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deletePackage(request, response);
                break;
            default:
                listPackages(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "add":
                addPackage(request, response);
                break;
            case "update":
                updatePackage(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/package?action=list");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // LIST — READ all packages and display them
    // ─────────────────────────────────────────────────────────────
    private void listPackages(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<ServicePackage> packages = PackageFileHandler.readAllPackages();
        request.setAttribute("packages", packages);
        request.getRequestDispatcher("/WEB-INF/views/package-list.jsp")
                .forward(request, response);
    }

    // ─────────────────────────────────────────────────────────────
    // ADD — CREATE a new package from the form
    // ─────────────────────────────────────────────────────────────
    private void addPackage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String vendorId    = request.getParameter("vendorId");
        String name        = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr    = request.getParameter("price");
        String[] inclArr   = request.getParameterValues("inclusions");
        String tier        = request.getParameter("tier");

        // Validation
        if (name == null || name.trim().isEmpty() || priceStr == null) {
            request.setAttribute("errorMsg", "Name and price are required.");
            request.getRequestDispatcher("/WEB-INF/views/add-package.jsp").forward(request, response);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMsg", "Invalid price format.");
            request.getRequestDispatcher("/WEB-INF/views/add-package.jsp").forward(request, response);
            return;
        }

        List<String> inclusions = (inclArr != null) ? Arrays.asList(inclArr) : List.of();

        // ✅ FIX: use PackageFileHandler.generatePackageId() — NOT PackageServlet
        String packageId = PackageFileHandler.generatePackageId();

        // Polymorphism: create correct subclass based on tier
        ServicePackage pkg;
        switch (tier != null ? tier.toUpperCase() : "BASIC") {
            case "PREMIUM":
                pkg = new PremiumPackage(packageId, vendorId, name, description, price, inclusions);
                break;
            case "CUSTOM":
                pkg = new CustomPackage(packageId, vendorId, name, description, price, inclusions, "");
                break;
            default:
                pkg = new BasicPackage(packageId, vendorId, name, description, price, inclusions);
        }

        // ✅ FIX: use PackageFileHandler.savePackage() — NOT PackageServlet
        boolean success = PackageFileHandler.savePackage(pkg);

        if (success) {
            request.getSession().setAttribute("successMsg", "Package '" + name + "' added successfully!");
        } else {
            request.getSession().setAttribute("errorMsg", "Failed to save package. Please try again.");
        }

        response.sendRedirect(request.getContextPath() + "/package?action=list");
    }

    // ─────────────────────────────────────────────────────────────
    // EDIT — Show the edit form pre-filled with existing data
    // ─────────────────────────────────────────────────────────────
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String packageId = request.getParameter("id");

        if (packageId == null || packageId.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/package?action=list");
            return;
        }

        // ✅ FIX: use PackageFileHandler.findById() — NOT PackageServlet
        ServicePackage pkg = PackageFileHandler.findById(packageId);
        if (pkg == null) {
            request.getSession().setAttribute("errorMsg", "Package not found.");
            response.sendRedirect(request.getContextPath() + "/package?action=list");
            return;
        }

        request.setAttribute("package", pkg);
        request.getRequestDispatcher("/WEB-INF/views/package-edit.jsp").forward(request, response);
    }

    // ─────────────────────────────────────────────────────────────
    // UPDATE — Save edited package back to file
    // ─────────────────────────────────────────────────────────────
    private void updatePackage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String packageId   = request.getParameter("packageId");
        String vendorId    = request.getParameter("vendorId");
        String name        = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr    = request.getParameter("price");
        String[] inclArr   = request.getParameterValues("inclusions");
        String tier        = request.getParameter("tier");

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMsg", "Invalid price.");
            response.sendRedirect(request.getContextPath() + "/package?action=edit&id=" + packageId);
            return;
        }

        List<String> inclusions = (inclArr != null) ? Arrays.asList(inclArr) : List.of();

        ServicePackage updated;
        switch (tier != null ? tier.toUpperCase() : "BASIC") {
            case "PREMIUM":
                updated = new PremiumPackage(packageId, vendorId, name, description, price, inclusions);
                break;
            case "CUSTOM":
                updated = new CustomPackage(packageId, vendorId, name, description, price, inclusions, "");
                break;
            default:
                updated = new BasicPackage(packageId, vendorId, name, description, price, inclusions);
        }

        boolean success = PackageFileHandler.updatePackage(updated);

        if (success) {
            request.getSession().setAttribute("successMsg", "Package updated successfully!");
        } else {
            request.getSession().setAttribute("errorMsg", "Update failed. Package not found.");
        }

        response.sendRedirect(request.getContextPath() + "/package?action=list");
    }

    // ─────────────────────────────────────────────────────────────
    // DELETE — Remove package from file
    // ─────────────────────────────────────────────────────────────
    private void deletePackage(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String packageId = request.getParameter("id");

        if (packageId != null && !packageId.trim().isEmpty()) {
            // ✅ FIX: use PackageFileHandler.deletePackage() — NOT PackageServlet
            boolean success = PackageFileHandler.deletePackage(packageId);
            if (success) {
                request.getSession().setAttribute("successMsg", "Package deleted successfully.");
            } else {
                request.getSession().setAttribute("errorMsg", "Delete failed. Package not found.");
            }
        }

        response.sendRedirect(request.getContextPath() + "/package?action=list");
    }
}
