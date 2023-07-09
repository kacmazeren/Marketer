package com.example.marketer10;

import android.location.Location;

public class Market {
    private String name, formatted_address;
    private Geometry  geometry ;
    private float distance;


    // Constructor
    public Market(String name, String address, Geometry geometry, Float distance) {
        this.name = name;
        this.formatted_address= formatted_address;
        this.geometry = geometry;
        this.distance = distance;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getFormatted_address() {return formatted_address; }

    public Geometry getGeometry() {
        return geometry;
    }
    public void setDistance(float distance) {
        this.distance = distance;
    }


    public void setName(String name) {
        this.name = name;
    }
    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
    public float getDistance() {
        return distance;
    }

    public class Geometry {
        private Location location;

        public Location getLocation() {
            return location;
        }
    }

    public class Location {
        private double lat;
        private double lng;

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }



    @Override
    public String toString() {
        return "Name: " + name + ", Address: " + formatted_address + ", Distance: " +distance ;
    }

}

