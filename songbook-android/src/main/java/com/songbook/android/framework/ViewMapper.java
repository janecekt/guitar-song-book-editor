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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public abstract class ViewMapper<DATA, VIEWHOLDER> {
    private final int dataLayoutResource;
    private final LayoutInflater inflater;

        
    protected ViewMapper(Context context, int dataLayoutResource) {
        this.dataLayoutResource = dataLayoutResource;
        this.inflater = LayoutInflater.from(context);
    }

    protected abstract Class<DATA> getType();
    
    protected abstract VIEWHOLDER buildViewHolder(View view);

    protected abstract void populateView(DATA item, VIEWHOLDER viewHolder);

    protected abstract boolean isEnabled();

    @SuppressWarnings("unchecked")
    public View buildView(DATA data, View convertView) {
        VIEWHOLDER itemViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(dataLayoutResource, null);
            itemViewHolder = buildViewHolder(convertView);
            convertView.setTag(itemViewHolder);
        }else {
            // Get the ViewHolder back to get fast access the relevant parts of the view
            itemViewHolder = (VIEWHOLDER) convertView.getTag();
        }

        // Populate data
        populateView(data, itemViewHolder);

        return convertView;
    }
}
