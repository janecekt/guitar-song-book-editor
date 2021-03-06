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

public class SongNodeTitleComparator implements Comparator<SongNode> {
    private final Collator collator;

    public SongNodeTitleComparator(Locale locale) {
        this.collator = Collator.getInstance(locale);
    }

    @Override
    public int compare(SongNode song1, SongNode song2) {
        return collator.compare(song1.getTitleNode().getTitle(), song2.getTitleNode().getTitle());
    }
}
