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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {
    private StringUtil() {}
    private static final Pattern CLEANUP_PATTERN = Pattern.compile("(\\p{InCombiningDiacriticalMarks}+)|(\\W)");
    private static final Pattern HTML_SEARCH_PATTERN = Pattern.compile("([\"\'<>&%])");
    private static final Map<String,String> HTML_REPLACE_MAP = new HashMap<String, String>();

    private static Method NORMALIZE_METHOD;
    private static Object NFD_VALUE;

    static {
        HTML_REPLACE_MAP.put("\"", "&quot;");
        HTML_REPLACE_MAP.put("'", "&apos;");
        HTML_REPLACE_MAP.put("'", "&apos;");
        HTML_REPLACE_MAP.put("<", "&lt;");
        HTML_REPLACE_MAP.put(">", "&gt;");
        HTML_REPLACE_MAP.put("&", "&amp;");
        HTML_REPLACE_MAP.put("%", "%25");

        // Initialize Normalizer reflections
        try {
            // Get NFD value
            Class<?> formClass = Class.forName("java.text.Normalizer$Form");
            Field nfdField = formClass.getDeclaredField("NFD");
            NFD_VALUE = nfdField.get(null);

            // Get normalize method
            Class<?> normalizerClass = Class.forName("java.text.Normalizer");
            NORMALIZE_METHOD = normalizerClass.getDeclaredMethod("normalize", CharSequence.class, formClass);
        } catch (Exception ex) {
            // If there is any exception => Normalizer is not present on classpath
            NFD_VALUE = null;
            NORMALIZE_METHOD = null;
        }
    }

    public static String htmlEscape(String string) {
        StringBuffer sb = new StringBuffer();
        appendAndHtmlEscape(sb, string);
        return sb.toString();
    }

    public static void appendAndHtmlEscape(StringBuffer sb, String string) {
        Matcher matcher = HTML_SEARCH_PATTERN.matcher(string);
        while (matcher.find()) {
            String replaceCharacter = matcher.group(1);
            String replacement = HTML_REPLACE_MAP.get(replaceCharacter);
            if (replacement == null) {
                throw new IllegalStateException("Replacement Map does not contain the replacement for " + replaceCharacter);
            }
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
    }

    public static String removeAccentsAndNonStandardCharacters(String string) {
        String normalizedText = string;

        // Normalize text using a Normalizer if possible
        if (NORMALIZE_METHOD != null) {
            try {
                // Invoke the bellow via reflection
                // java.text.Normalizer.normalize(string, java.text.Normalizer.Form.NFD);

                // Normalize text
                normalizedText = (String) NORMALIZE_METHOD.invoke(null, string, NFD_VALUE);
            } catch (Exception ex) {
                normalizedText = string;
            }
        }

        // Update pattern.
        return CLEANUP_PATTERN.matcher(normalizedText).replaceAll("").toLowerCase();
    }

    public static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
