package com.joshimbriani.mymovement.db;

import android.util.Log;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.joshimbriani.mymovement.db.Movement;
import com.joshimbriani.mymovement.db.MovementPoint;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MovementWithPoints {
    @Embedded public Movement movement;
    @Relation(
            parentColumn = "id",
            entityColumn = "movement_id"
    )
    public List<MovementPoint> points;
    public long recent_point;

    public String getDatetimeRange() {
        if (points.size() == 0) {
            return "No points";
        }

        Collections.sort(points, (o1, o2) -> o1.getDateTime().compareTo(o2.getDateTime()));

        MovementPoint first = points.get(0);
        MovementPoint last = points.get(points.size() - 1);
        String dateString = "";
        DateTimeFormatter formatterWithDate = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mma");
        DateTimeFormatter formattedWithoutDate = DateTimeFormatter.ofPattern("hh:mma");

        dateString += first.getDateTime().format(formatterWithDate);
        dateString += " - ";
        if (first.getDateTime().getDayOfYear() != last.getDateTime().getDayOfYear() || first.getDateTime().getYear() != last.getDateTime().getYear()) {
            dateString += last.getDateTime().format(formatterWithDate);
        } else {
            dateString += last.getDateTime().format(formattedWithoutDate);
        }
        return dateString;
    }

    public CameraUpdate getCameraUpdateForList() {
        if (points.size() == 0) {
            return CameraUpdateFactory.newLatLngZoom(new LatLng(37.404310, -121.924650), 13f);
        }

        if (points.size() == 1) {
            return CameraUpdateFactory.newLatLngZoom(new LatLng(points.get(0).getLat(), points.get(0).getLon()), 10f);
        }

        float zoom = 13f;

        Collections.sort(points, (o1, o2) -> Double.compare(o1.getLat(), o2.getLat()));
        double firstLat = points.get(0).getLat();
        double lastLat = points.get(points.size() - 1).getLat();
        double lat = (lastLat + firstLat) / 2;

        Collections.sort(points, (o1, o2) -> Double.compare(o1.getLon(), o2.getLon()));
        double firstLon = points.get(0).getLon();
        double lastLon = points.get(points.size() - 1).getLon();
        double lon = (lastLon + firstLon) / 2;

        double maxDiff = Double.max(lastLat - firstLat, lastLon - firstLon);
        if (maxDiff < 0.0001) {
            zoom = 18f;
        } else if (maxDiff < 0.0005) {
            zoom = 16f;
        } else if (maxDiff < 0.001) {
            zoom = 14f;
        } else if (maxDiff < 0.005) {
            zoom = 12f;
        } else if (maxDiff < 0.01) {
            zoom = 10f;
        } else if (maxDiff < 0.05) {
            zoom = 8f;
        } else {
            zoom = 6f;
        }

        return CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), zoom);
    }

    public void sortPoints() {
        Log.e("TEST", points.get(0) + "f");
        //Collections.sort(points, (o1, o2) -> o1.getDateTime().compareTo(o2.getDateTime()));
        points.sort((o1, o2) -> o2.getDateTime().compareTo(o1.getDateTime()));
        Log.e("TEST", points.get(0) + "f");
    }
}
