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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipInputStreamIterable extends GenericIterable<DataEntry> {
    private static Logger logger = LoggerFactory.getLogger(ZipInputStreamIterable.class);
    private String description;
    private ZipInputStream zipInputStream;
    private byte[] buffer = new byte[1024];


    public ZipInputStreamIterable(ZipInputStream inputStream, String description) {
        this.zipInputStream = inputStream;
        this.description = description;
    }

    public ZipInputStreamIterable(InputStream inputStream, String description) {
        this(new ZipInputStream(inputStream), description);
    }


    @Override
    protected DataEntry readNextEntry() {
        try {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            if (zipEntry != null) {
                // Read into stream
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                int count;
                while ((count = zipInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, count);
                }
                return new DataEntry(zipEntry.getName(), outputStream.toByteArray());
            } else {
                zipInputStream.close();
                return null;
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read " + description, ex);
        }
    }


    public void close() {
        try {
            if (zipInputStream != null) {
                zipInputStream.close();
            }
        } catch (IOException ex) {
            logger.warn("Failed to close " + description, ex);
        }
    }
}
