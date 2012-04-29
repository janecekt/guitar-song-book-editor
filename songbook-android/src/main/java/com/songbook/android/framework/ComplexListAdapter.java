/*
 *  Copyright (c) 2008 - Tomas Janecek.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.songbook.android.framework;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ComplexListAdapter extends BaseAdapter {
    private Map<Class<?>, ViewMapper<?,?>> viewMapperMap;
    private Map<Class<?>, Integer> typeIdMap;
    private List<?> data;
    private List<Long> idMapping;
    private boolean isAllItemsEnabled;


    public ComplexListAdapter(Iterable<ViewMapper<?,?>> viewMappers) {
        this.viewMapperMap = new HashMap<Class<?>, ViewMapper<?, ?>>();
        this.typeIdMap = new HashMap<Class<?>, Integer>();
        this.data = Collections.emptyList();
        isAllItemsEnabled = true;
        int typeId=0;
        for (ViewMapper<?,?> viewMapper : viewMappers) {
            viewMapperMap.put(viewMapper.getType(), viewMapper);
            typeIdMap.put(viewMapper.getType(), typeId);
            isAllItemsEnabled = isAllItemsEnabled && viewMapper.isEnabled();
            typeId++;
        }
    }


    public void setData(List<?> data, List<Long> idMapping) {
        if ((idMapping != null) && (data.size() != idMapping.size())) {
            throw new IllegalArgumentException("IdMapping must have the same number of elements as the data list");
        }
        this.idMapping = idMapping;
        setData(data);
    }


    public void setData(List<?> data) {
        // Set data
        this.data = data;

        // Notify data changed
        this.notifyDataSetChanged();
    }


    @Override
    public boolean isEnabled(int position) {
        Object item = getItem(position);
        return viewMapperMap.get(item.getClass()).isEnabled();
    }


    @Override
    public boolean areAllItemsEnabled() {
        return isAllItemsEnabled;
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }


    @Override
    public int getCount() {
        return data.size();
    }


    @Override
    public Object getItem(int position) {
        return data.get(position);
    }


    @Override
    public long getItemId(int position) {
        return  (idMapping != null) ? idMapping.get(position) : position;
    }


    @Override
    public int getItemViewType(int position) {
        Object item = getItem(position);
        return typeIdMap.get(item.getClass());
    }


    @Override
    public int getViewTypeCount() {
        return typeIdMap.size();
    }


    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
        Object item = getItem(position);
        ViewMapper viewMapper = viewMapperMap.get(item.getClass());
        return viewMapper.buildView(item, convertView);
    }
}
