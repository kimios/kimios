package org.kimios.kernel.index.query.model;


import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 2/7/13
 * Time: 5:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchRequest {


    private Long id;

    private String name;

    private String owner;

    private String ownerSource;

    private List<Criteria> criteriaList;

    private String criteriasListJson;

    public String getCriteriasListJson() {
        return criteriasListJson;
    }

    public void setCriteriasListJson(String criteriasListJson) {
        this.criteriasListJson = criteriasListJson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Criteria> getCriteriaList() {
        return criteriaList;
    }

    public void setCriteriaList(List<Criteria> criteriaList) {
        this.criteriaList = criteriaList;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerSource() {
        return ownerSource;
    }

    public void setOwnerSource(String ownerSource) {
        this.ownerSource = ownerSource;
    }

    @Override
    public String toString() {
        return "SearchRequest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", ownerSource='" + ownerSource + '\'' +
                ", criteriaList=" + criteriaList +
                ", criteriasListJson='" + criteriasListJson + '\'' +
                '}';
    }
}
