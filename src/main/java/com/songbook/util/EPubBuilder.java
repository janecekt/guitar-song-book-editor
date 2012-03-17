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
package com.songbook.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class EPubBuilder {
    private static final String RESOURCE_TOC_NCX_FTL = "util/epub/toc.ncx.ftl";
    private static final String RESOURCE_MIMETYPE = "/util/epub/content/mimetype";
    private static final String RESOURCE_CONTAINER_XML = "/util/epub/content/META-INF/container.xml";
    private static final String RESOURCE_CONTENT_OPF_FTL = "util/epub/content.opf.ftl";

    private String bookTitle;
    private String bookCreator;
    private String bookId;
    private final List<Entry> entryList;


    public EPubBuilder() {
        this.bookTitle = "Title";
        this.bookCreator = "Creator";
        this.bookId = UUID.randomUUID().toString();
        this.entryList = new ArrayList<Entry>();
    }


    public EPubBuilder withBookTitle(String title) {
        this.bookTitle = title;
        return this;
    }


    public EPubBuilder withBookCreator(String creator) {
        this.bookCreator = creator;
        return this;
    }


    @SuppressWarnings("unused")
    public EPubBuilder withBookId(String bookId) {
        this.bookId = bookId;
        return this;
    }


    public EPubBuilder withNewEntry(String name, String fileName, String mime, boolean userReadable, byte[] data) {
        entryList.add(new Entry(name, fileName, mime, userReadable, data));
        return this;
    }


    public void build(File output) throws IOException {
        // FreeMaker model
        List<Entry> fullEntryList = new ArrayList<Entry>(entryList);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("bookTitle", bookTitle);
        model.put("bookCreator", bookCreator);
        model.put("bookId", bookId);
        model.put("entryList", fullEntryList);

        // Add TOC
        fullEntryList.add(new Entry("ncx", "toc.ncx", "application/x-dtbncx+xml", false,
                FreeMakerUtil.processTemplate(model, RESOURCE_TOC_NCX_FTL).getBytes("UTF8")));

        // Create zip stream
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(output));

        // ADD TO ZIP: /mimetype
        out.putNextEntry(new ZipEntry("mimetype"));
        FileIO.appendInputStreamToOutputStream(
                EPubBuilder.class.getResourceAsStream(RESOURCE_MIMETYPE), out);

        // ADD TO ZIP: /META-INF/container.xml
        out.putNextEntry(new ZipEntry("META-INF/"));
        out.putNextEntry(new ZipEntry("META-INF/container.xml"));
        FileIO.appendInputStreamToOutputStream(
                EPubBuilder.class.getResourceAsStream(RESOURCE_CONTAINER_XML), out);

        // ADD TO ZIP: /OEBPS/<user files>
        out.putNextEntry(new ZipEntry("OEBPS/"));
        for (Entry entry : fullEntryList) {
            out.putNextEntry(new ZipEntry("OEBPS/" + entry.getFileName()));
            out.write(entry.getData());
        }

        // ADD TO ZIP: /OEBPS/contents.opf
        out.putNextEntry(new ZipEntry("OEBPS/content.opf"));
        String data = FreeMakerUtil.processTemplate(model, RESOURCE_CONTENT_OPF_FTL);
        out.write(data.getBytes("UTF8"));

        out.close();
    }


    public static class Entry {
        private final String id;
        private final String name;
        private final String fileName;
        private final String mime;
        private final byte[] data;
        private final boolean userReadable;


        public Entry(String id, String name, String fileName, String mime, boolean userReadable, byte[] data) {
            this.id = id;
            this.name = name;
            this.fileName = fileName;
            this.mime = mime;
            this.data = data;
            this.userReadable = userReadable;
        }


        public Entry(String name, String fileName, String mime, boolean userReadable, byte[] data) {
            this(fileName.toLowerCase().replaceAll(" ", "-"), name, fileName, mime, userReadable, data);
        }


        @SuppressWarnings("unused")
        public String getId() {
            return id;
        }


        @SuppressWarnings("unused")
        public String getName() {
            return name;
        }


        @SuppressWarnings("unused")
        public String getFileName() {
            return fileName;
        }


        @SuppressWarnings("unused")
        public String getMime() {
            return mime;
        }


        @SuppressWarnings("unused")
        public byte[] getData() {
            return data;
        }


        @SuppressWarnings("unused")
        public boolean isUserReadable() {
            return userReadable;
        }
    }
}
