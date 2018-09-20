/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kimios.kernel.index.query.model;

import java.util.List;

/**
 * @author Fabien Alin <a href="mailto:fabien.alin@gmail.com">fabien.alin@gmail.com</a>
 * @version 1.0
 */
public class Criteria
{
    private String query;

    private String fieldName;

    private String operator;

    private int position = 0;

    private boolean isFaceted;

    private Integer level = 0;

    private boolean filterQuery;

    private List<String> addonsFields;

    private List<String> filtersValues;

    private String facetField;

    private boolean exclusiveFacet = false;

    private boolean isFacetRange;

    private String facetRangeGap;

    private Long metaId;

    private Long metaType;

    private String rangeMin;

    private String rangeMax;

    private String dateFacetGapType;

    private String dateFacetGapRange;

    private boolean rawQuery = false;

    private String dateFormat;

    public List<String> getAddonsFields() {
        return addonsFields;
    }

    public void setAddonsFields(List<String> addonsFields) {
        this.addonsFields = addonsFields;
    }

    public boolean isFilterQuery() {
        return filterQuery;
    }

    public void setFilterQuery(boolean filterQuery) {
        this.filterQuery = filterQuery;
    }

    public boolean isExclusiveFacet() {
        return exclusiveFacet;
    }

    public void setExclusiveFacet(boolean exclusiveFacet) {
        this.exclusiveFacet = exclusiveFacet;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDateFacetGapType()
    {
        return dateFacetGapType;
    }

    public void setDateFacetGapType( String dateFacetGapType )
    {
        this.dateFacetGapType = dateFacetGapType;
    }

    public String getDateFacetGapRange()
    {
        return dateFacetGapRange;
    }

    public void setDateFacetGapRange( String dateFacetGapRange )
    {
        this.dateFacetGapRange = dateFacetGapRange;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery( String query )
    {
        this.query = query;
    }

    public boolean isFaceted()
    {
        return isFaceted;
    }

    public void setFaceted( boolean faceted )
    {
        isFaceted = faceted;
    }

    public Integer getLevel()
    {
        return level;
    }

    public void setLevel( Integer level )
    {
        this.level = level;
    }

    public List<String> getFiltersValues()
    {
        return filtersValues;
    }

    public void setFiltersValues( List<String> filtersValues )
    {
        this.filtersValues = filtersValues;
    }

    public String getFacetField()
    {
        return facetField;
    }

    public void setFacetField( String facetField )
    {
        this.facetField = facetField;
    }

    public boolean isFacetRange()
    {
        return isFacetRange;
    }

    public void setFacetRange( boolean facetRange )
    {
        isFacetRange = facetRange;
    }

    public String getFacetRangeGap()
    {
        return facetRangeGap;
    }

    public void setFacetRangeGap( String facetRangeGap )
    {
        this.facetRangeGap = facetRangeGap;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName( String fieldName )
    {
        this.fieldName = fieldName;
    }

    public Long getMetaId()
    {
        return metaId;
    }

    public void setMetaId( Long metaId )
    {
        this.metaId = metaId;
    }

    public Long getMetaType()
    {
        return metaType;
    }

    public void setMetaType( Long metaType )
    {
        this.metaType = metaType;
    }

    public String getRangeMin()
    {
        return rangeMin;
    }

    public void setRangeMin( String rangeMin )
    {
        this.rangeMin = rangeMin;
    }

    public String getRangeMax()
    {
        return rangeMax;
    }

    public void setRangeMax( String rangeMax )
    {
        this.rangeMax = rangeMax;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition( int position )
    {
        this.position = position;
    }

    public boolean isRawQuery() {
        return rawQuery;
    }

    public void setRawQuery(boolean rawQuery) {
        this.rawQuery = rawQuery;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public String toString()
    {
        return "Criteria{" +
            "query='" + query + '\'' +
            ", fieldName='" + fieldName + '\'' +
            ", position=" + position +
            ", isFaceted=" + isFaceted +
            ", level=" + level +
            ", filtersValues=" + filtersValues +
            ", facetField='" + facetField + '\'' +
            ", isFacetRange=" + isFacetRange +
            ", facetRangeGap='" + facetRangeGap + '\'' +
            ", metaId=" + metaId +
            ", metaType=" + metaType +
            ", rangeMin='" + rangeMin + '\'' +
            ", rangeMax='" + rangeMax + '\'' +
            '}';
    }
}
