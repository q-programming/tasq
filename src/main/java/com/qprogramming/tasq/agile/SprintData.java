package com.qprogramming.tasq.agile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class SprintData extends AgileData {
    private Map<String, Float> left;
    private Map<String, Float> burned;
    private Map<String, Float> ideal;
    private Integer totalPoints = 0;

    public SprintData() {
        left = new LinkedHashMap<>();
        burned = new LinkedHashMap<>();
        ideal = new LinkedHashMap<>();
    }

    public Map<String, Float> getLeft() {
        return left;
    }

    public void setLeft(Map<String, Float> left) {
        this.left = left;
    }

    public Map<String, Float> getIdeal() {
        return ideal;
    }

    public void setIdeal(Map<String, Float> ideal) {
        this.ideal = ideal;
    }

    public Map<String, Float> getBurned() {
        return burned;
    }

    public void setBurned(Map<String, Float> pointsBurned) {
        this.burned = pointsBurned;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public void createIdeal(String startTime, Float value, String endTime) {
        ideal.put(startTime, value);
        left.put(startTime, value);
        burned.put(startTime, 0f);
        ideal.put(endTime, 0f);
    }

    public void putToLeft(String time, Float value) {
        left.put(time, value);
    }

    public void fillEnds(String endTime) {
        fillEnd(endTime, left);
        fillEnd(endTime, burned);
    }

    private void fillEnd(String time, Map<String, Float> map) {
        if (map.get(time) == null) {
            List<Map.Entry<String, Float>> entryList = new ArrayList<>(map.entrySet());
            Map.Entry<String, Float> entry = entryList.get(entryList.size() - 1);
            map.put(time, entry.getValue());
        }
    }

}
