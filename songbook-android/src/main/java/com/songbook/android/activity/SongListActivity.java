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
package com.songbook.android.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import com.google.inject.Inject;
import com.songbook.android.R;
import com.songbook.android.framework.ComplexListAdapter;
import com.songbook.android.framework.ViewMapper;
import com.songbook.android.util.PreferencesManager;
import com.songbook.android.util.SongListManager;
import com.songbook.core.model.SongNode;
import com.songbook.core.util.Transformer;
import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectResource;

public class SongListActivity extends RoboListActivity {
    @InjectResource(R.string.label_unknown)
    String labelUnknown;

    @InjectResource(R.string.label_noSongsLoaded)
    String labelNoSongsLoaded;

    @Inject
    SongListManager songListManager;

    @Inject
    PreferencesManager preferencesManager;

    private ComplexListAdapter adapter;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // Set ListView adapter
        adapter = new ComplexListAdapter( Arrays.asList(
                new SongNodeViewMapper(this.getBaseContext(), R.layout.song_list_item_layout),
                new GroupViewMapper(this.getBaseContext(), R.layout.group_list_item_layout)));
        setListAdapter(adapter);

        // Make view clickable and start listening to events
        getListView().setClickable(true);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClicked((int) id);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        refreshActivity();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    public void refreshActivity() {
        // Set grouping as per the preferences
        Transformer<SongNode,String> groupTransformer;
        switch (preferencesManager.getOrdering()) {
            case BY_INDEX:
                groupTransformer = new SongNodeIndexGroupTransformer();
                break;
            case BY_SUBTITLE:
                groupTransformer = new SongNodeSubTitleGroupTransformer();
                break;
            case BY_TITLE:
            default:
                groupTransformer = new SongNodeTitleGroupTransformer();
                break;
        }
        groupTransformer = preferencesManager.isGroupingEnabled() ? groupTransformer : null;


        // Build grouped list
        if (songListManager.getSongNodeList().size() > 0) {
            List<Object> groupedList = new ArrayList<Object>();
            List<Long> idMap = new ArrayList<Long>();
            String previousGroup = null;
            long songNodeIndex = 0;
            for (SongNode songNode : songListManager.getSongNodeList()) {
                if (groupTransformer != null) {
                    String currentGroup = groupTransformer.transform(songNode);
                    if (!currentGroup.equals(previousGroup)) {
                        groupedList.add(currentGroup);
                        idMap.add(-1L);
                        previousGroup = currentGroup;
                    }
                }
                groupedList.add(songNode);
                idMap.add(songNodeIndex);
                songNodeIndex++;
            }
            adapter.setData( groupedList, idMap);
        } else {
            adapter.setData(Arrays.asList(labelNoSongsLoaded));
        }
    }
    
    
    public void onItemClicked(int id) {
        songListManager.setSelectedIndex(id);
        startActivity(new Intent(this, SongViewActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.song_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemPrefs:
                startActivity(new Intent(this, PreferencesActivity.class));
                break;
        }
        // True = menu event fully handled
        return true;
    }

    private class SongNodeTitleGroupTransformer implements Transformer<SongNode,String> {
        @Override
        public String transform(SongNode songNode) {
            return "- " + songNode.getTitleNode().getTitle().substring(0,1) + " -";
        }
    }
    
    private class SongNodeSubTitleGroupTransformer implements Transformer<SongNode,String> {
        @Override
        public String transform(SongNode songNode) {
            String subTitle = songNode.getTitleNode().getSubTitle();
            return (subTitle == null) ? labelUnknown : subTitle;
        }
    }

    private class SongNodeIndexGroupTransformer implements Transformer<SongNode,String> {
        @Override
        public String transform(SongNode songNode) {
            if (songNode.getIndex() == null) {
                return labelUnknown;
            } else {
                int tens = songNode.getIndex() / 10;
                return (tens*10) + " - " + ((tens+1)*10);
            }
        }
    }


    private static class GroupViewMapper extends ViewMapper<String, GroupViewMapper.GroupViewHolder> {
        public GroupViewMapper(Context context, int dataLayoutResource) {
            super(context, dataLayoutResource);
        }

        @Override
        protected Class<String> getType() {
            return String.class;
        }


        @Override
        protected GroupViewHolder buildViewHolder(View view) {
            GroupViewHolder viewHolder = new GroupViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.group_list_item_title);
            return  viewHolder;
        }


        @Override
        protected void populateView(String groupTitle, GroupViewHolder viewHolder) {
            viewHolder.textView.setText(groupTitle);
        }


        @Override
        protected boolean isEnabled() {
            return false;
        }


        /**
         * Helper ViewHolder class.
         */
        protected static class GroupViewHolder {
            TextView textView;
        }
    }
    
            
    private static class SongNodeViewMapper extends ViewMapper<SongNode, SongNodeViewMapper.SongNodeViewHolder> {
        public SongNodeViewMapper(Context context, int dataLayoutResource) {
            super(context, dataLayoutResource);
        }

        @Override
        protected Class<SongNode> getType() {
            return SongNode.class;
        }


        @Override
        protected SongNodeViewHolder buildViewHolder(View view) {
            SongNodeViewHolder viewHolder = new SongNodeViewHolder();
            viewHolder.titleView = (TextView) view.findViewById(R.id.song_list_item_title);
            viewHolder.subTitleView = (TextView) view.findViewById(R.id.song_list_item_subTitle);
            viewHolder.indexView = (TextView) view.findViewById(R.id.song_list_item_index);
            return  viewHolder;
        }


        @Override
        protected void populateView(SongNode songNode, SongNodeViewHolder viewHolder) {
            viewHolder.titleView.setText(songNode.getTitleNode().getTitle());
            viewHolder.subTitleView.setText(songNode.getTitleNode().getSubTitle());
            viewHolder.indexView.setText( (songNode.getIndex() != null) ? Integer.toString(songNode.getIndex()) : "");
        }


        @Override
        protected boolean isEnabled() {
            return true;
        }


        /**
         * Helper ViewHolder class.
         */
        protected static class SongNodeViewHolder {
            TextView titleView;
            TextView subTitleView;
            TextView indexView;
        }        
    }
}
