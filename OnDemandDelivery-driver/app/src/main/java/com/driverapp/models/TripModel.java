package com.driverapp.models;

/**
 * Created by bridgelabz on 18/07/18.
 */

public class TripModel {

    private String _id;
    private String created_at;
    private double[] starts_at;
    private double[] ends_at;
    private String ttl;
    private String invoice_id;
    private String web_key;
    private String tracking_id;
    private String status;
    private String end_location;
    private String start_location;
    private String agent_id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public double[] getStarts_at() {
        return starts_at;
    }

    public void setStarts_at(double[] starts_at) {
        this.starts_at = starts_at;
    }

    public double[] getEnds_at() {
        return ends_at;
    }

    public void setEnds_at(double[] ends_at) {
        this.ends_at = ends_at;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    public String getInvoice_id() {
        return invoice_id;
    }

    public void setInvoice_id(String invoice_id) {
        this.invoice_id = invoice_id;
    }

    public String getWeb_key() {
        return web_key;
    }

    public void setWeb_key(String web_key) {
        this.web_key = web_key;
    }

    public String getTracking_id() {
        return tracking_id;
    }

    public void setTracking_id(String tracking_id) {
        this.tracking_id = tracking_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEnd_location() {
        return end_location;
    }

    public void setEnd_location(String end_location) {
        this.end_location = end_location;
    }

    public String getStart_location() {
        return start_location;
    }

    public void setStart_location(String start_location) {
        this.start_location = start_location;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

}
