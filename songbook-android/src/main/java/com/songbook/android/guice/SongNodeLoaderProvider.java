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
package com.songbook.android.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.songbook.core.model.SongNode;
import com.songbook.core.parser.Parser;
import com.songbook.core.util.SongNodeLoader;

public class SongNodeLoaderProvider implements Provider<SongNodeLoader> {
    @Inject
    Parser<SongNode> parser;

    @Override
    public SongNodeLoader get() {
        return new SongNodeLoader(parser);
    }
}
