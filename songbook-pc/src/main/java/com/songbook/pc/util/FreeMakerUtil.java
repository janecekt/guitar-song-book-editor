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
package com.songbook.pc.util;

import java.io.StringWriter;
import java.io.Writer;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;

public final class FreeMakerUtil {
    private FreeMakerUtil() {}

    // Initialize FreeMaker configuration
    // N.B. It should not be change later as it not thread-safe.
    private static final Configuration configuration;
    static {
        configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setClassForTemplateLoading(FreeMakerUtil.class, "/");
        configuration.setObjectWrapper(new BeansWrapper(Configuration.VERSION_2_3_23));
    }

    public static void processTemplate(Object model, String template, Writer output) {
        try {
            Template fmTemplate = configuration.getTemplate(template);
            fmTemplate.process(model, output);
        } catch (Exception ex) {
            throw new RuntimeException("Processing of template failed - " + template, ex);
        }
    }


    public static String processTemplate(Object model, String template) {
        StringWriter stringWriter = new StringWriter();
        processTemplate(model,template,stringWriter);
        return stringWriter.toString();
    }
}
