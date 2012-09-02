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
package com.songbook.android.paging;

import android.view.View;

/**
 * Class used by the PagingView to affect the underlying view.
 */
public interface PagingProvider {
    /**
     * Returns the view being overlayed.
     * This will be used by the CurlView to capture the state of the underlying view just
     * before the rendering of the curl effect.
     */
    View getOverlayedView();


    /**
     * Switches to next page.
     */
    void goToNextPage();


    /**
     * Switches to previous page.
     */
    void goToPreviousPage();
}
