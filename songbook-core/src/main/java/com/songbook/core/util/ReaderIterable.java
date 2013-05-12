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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReaderIterable extends GenericIterable<String> {
    private static Logger logger = LoggerFactory.getLogger(ReaderIterable.class);
    private final BufferedReader reader;
    private final String description;

    public ReaderIterable(InputStream inStream, Charset encoding, String description) {
        this.reader = new BufferedReader(new InputStreamReader(inStream, encoding));
        this.description = description;
    }


    public ReaderIterable(BufferedReader reader, String description) {
        this.reader = reader;
        this.description = description;
    }


    @Override
    protected String readNextEntry() {
        try {
            String nextLine = reader.readLine();
            if (nextLine == null) {
                reader.close();
            }
            return nextLine;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read " + description, ex);
        }
    }


    public void close() {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ex) {
            logger.error("Failed to close " + description, ex);
        }

    }
}
