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
package com.songbook.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {
    private StringUtil() {}

    private static final Pattern PATTERN = Pattern.compile("([\"\'<>&%])");
    private static Map<String,String> replaceMap = new HashMap<String, String>();
    static {
        replaceMap.put("\"","&quot;");
        replaceMap.put("'","&apos;");
        replaceMap.put("'","&apos;");
        replaceMap.put("<","&lt;");
        replaceMap.put(">","&gt;");
        replaceMap.put("&","&amp;");
        replaceMap.put("%","%25");
    }

    public static String htmlEscape(String string) {
        StringBuffer sb = new StringBuffer();
        appendAndHtmlEscape(sb, string);
        return sb.toString();
    }

    public static void appendAndHtmlEscape(StringBuffer sb, String string) {
        Matcher matcher = PATTERN.matcher(string);
        while (matcher.find()) {
            String replaceCharacter = matcher.group(1);
            String replacement = replaceMap.get(replaceCharacter);
            if (replacement == null) {
                throw new IllegalStateException("Replacement Map does not contain the replacement for " + replaceCharacter);
            }
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
    }

}
