package com.leidos.xchangecore.adapter.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MappedRecord
implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(4096)")
    private String title;

    @Column(columnDefinition = "VARCHAR(4096)")
    private String category = "N/A";

    @Column(columnDefinition = "VARCHAR(65536)")
    private String content = "N/A";

    @Column(columnDefinition = "VARCHAR(65536)")
    private String description = "N/A";

    @Column(columnDefinition = "VARCHAR(4096)")
    private String index;

    private String creator;

    private String filter;

    private String igID = null;
    @Column(columnDefinition = "VARCHAR(4096)")
    private String workProductID = null;

    private String latitude;
    private String longitude;
    private Date lastUpdated;

    public String getCategory() {

        return category;
    }

    public String getContent() {

        return content;
    }

    public String getCreator() {

        return creator;
    }

    public String getDescription() {

        return description;
    }

    public String getFilter() {

        return filter;
    }

    public Integer getId() {

        return id;
    }

    public String getIgID() {

        return igID;
    }

    public String getIndex() {

        return index;
    }

    public Date getLastUpdated() {

        return lastUpdated;
    }

    public String getLatitude() {

        return latitude;
    }

    public String getLongitude() {

        return longitude;
    }

    public String getTitle() {

        return title;
    }

    public String getWorkProductID() {

        return workProductID;
    }

    public void setCategory(String category) {

        this.category = category;
    }

    public void setContent(String content) {

        this.content = content;
    }

    public void setCreator(String creator) {

        this.creator = creator;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public void setFilter(String filter) {

        this.filter = filter;
    }

    public void setId(Integer id) {

        this.id = id;
    }

    public void setIgID(String igID) {

        this.igID = igID;
    }

    public void setIndex(String index) {

        this.index = index;
    }

    public void setLastUpdated(Date lastUpdated) {

        this.lastUpdated = lastUpdated;
    }

    public void setLatitude(String latitude) {

        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {

        this.longitude = longitude;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public void setWorkProductID(String workProductID) {

        this.workProductID = workProductID;
    }

    @Override
    public String toString() {

        final StringBuffer sb = new StringBuffer();
        sb.append("\n\tID: " + id);
        sb.append("\n\tTitle: " + title);
        sb.append("\n\tCategory: " + category);
        sb.append("\n\tLat/Lon: " + latitude + "/" + longitude);
        sb.append("\n\tIndex Key: " + index);
        sb.append("\n\tFilter: " + filter);
        sb.append("\n\tDescription: " + description);
        sb.append("\n\tContent:  " + content);
        if (workProductID != null)
            sb.append("\n\tProductID: " + workProductID);
        if (igID != null)
            sb.append("\n\tIGID: " + igID);
        sb.append("\n");
        return sb.toString();
    }
}
