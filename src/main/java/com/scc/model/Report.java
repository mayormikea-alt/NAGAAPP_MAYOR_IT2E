package com.scc.model;

public record Report(int id, int userId, int assignedResponderId, String imagePath, String description, double latitude, double longitude, String status, String datePosted) {

    public double calculateDistance(double refLat, double refLon) {
        return Math.sqrt(Math.pow(this.latitude - refLat, 2) + Math.pow(this.longitude - refLon, 2)) * 111.0; // km approx
    }
}