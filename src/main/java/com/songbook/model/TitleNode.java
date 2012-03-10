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
package com.songbook.model;

public class TitleNode implements Node {
    private final String title;
    private final String subTitle;


    public TitleNode(String rawTitle) {
        String[] titleParts = rawTitle.split("-");
        title = titleParts[0].trim();
        subTitle = (titleParts.length > 1) ? titleParts[1].trim() : null;
    }


    public TitleNode(String title, String subTitle) {
        this.title = title;
        this.subTitle = subTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    @Override
    public String getAsText(int transposition) {
        if (subTitle == null) {
            return title;
        } else {
            return title + " - " + subTitle;
        }
    }

    @Override
    public String getAsHTML(int transposition) {
        if (subTitle == null) {
            return "<DIV class=\"title\">" + title + "</DIV>\n";
        } else {
            return "<DIV class=\"title\">" + title + " - " + subTitle + "</DIV>\n";
        }
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("TitleNode[title=").append(title);
        if (subTitle != null) {
            out.append(", subTitle=").append(subTitle);
        }
        out.append("]");
        return out.toString();
    }
}
