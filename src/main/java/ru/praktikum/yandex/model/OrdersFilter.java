package ru.praktikum.yandex.model;

import java.util.List;

public class OrdersFilter {
    private Integer courierId;
    private List<String> nearestStation;
    private Integer limit;
    private Integer page;

    public OrdersFilter() {
    }

    public OrdersFilter(Integer courierId, List<String> nearestStation, Integer limit, Integer page) {
        this.courierId = courierId;
        this.nearestStation = nearestStation;
        this.limit = limit;
        this.page = page;
    }

    public Integer getCourierId() {
        return courierId;
    }

    public void setCourierId(Integer courierId) {
        this.courierId = courierId;
    }

    public List<String> getNearestStation() {
        return nearestStation;
    }

    public void setNearestStation(List<String> nearestStation) {
        this.nearestStation = nearestStation;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
