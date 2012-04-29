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
package com.songbook.core.comparator;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.songbook.core.model.SongNode;

public class SongNodeSubTitleComparator implements Comparator<SongNode> {
    private final Collator collator;
    
    public SongNodeSubTitleComparator(Locale locale) {
        this.collator = Collator.getInstance(locale);
    }
    
    @Override
    public int compare(SongNode song1, SongNode song2) {
        if (song1.getTitleNode().getSubTitle() == null) {
            if (song2.getTitleNode().getSubTitle() == null) {
                // If both subtitles are null => compare by title
                return collator.compare(song1.getTitleNode().getTitle(), song2.getTitleNode().getTitle());
            } else {
                return -1;
            }
        } else {
            if (song2.getTitleNode().getSubTitle() == null) {
                return 1;
            } else {
                // Compare subtitles
                int result = collator.compare(song1.getTitleNode().getSubTitle(), song2.getTitleNode().getSubTitle());
                if (result != 0) {
                    return result;
                }
                // If subtitles are the same - compare by title
                return collator.compare(song1.getTitleNode().getTitle(), song2.getTitleNode().getTitle());
            }
        }
    }
}
