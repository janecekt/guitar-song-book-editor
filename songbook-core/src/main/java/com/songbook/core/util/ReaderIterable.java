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
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReaderIterable implements Iterable<String> {
    private static Logger logger = LoggerFactory.getLogger(SongNodeLoader.class);
    private StringIterator iterator;
    private boolean iteratorReturned = false;


    public ReaderIterable(InputStream inStream, Charset encoding, String description) {
        this(new BufferedReader(new InputStreamReader(inStream, encoding)), description);
    }


    public ReaderIterable(BufferedReader reader, String description) {
        iterator = new StringIterator(reader, description);
    }


    @Override
    public Iterator<String> iterator() {
        if (!iteratorReturned) {
            iteratorReturned = true;
            return iterator;
        } else {
            throw new UnsupportedOperationException("Iterator can be returned only once !");
        }
    }


    public void close() {
        iterator.close();
    }


    private static class StringIterator implements Iterator<String> {
        private final BufferedReader reader;
        private String nextLine;
        private String description;


        public StringIterator(BufferedReader reader, String description) {
            this.reader = reader;
            this.description = description;
            readNextLine();
        }


        private void readNextLine() {
            try {
                this.nextLine = reader.readLine();
                if (nextLine == null) {
                    reader.close();
                }
            } catch (IOException ex) {
                throw new RuntimeException("Failed to read " + description, ex);
            }

        }


        @Override
        public boolean hasNext() {
            return (nextLine != null);
        }


        @Override
        public String next() {
            if (hasNext()) {
                // Read next line
                String result = nextLine;
                readNextLine();
                return result;
            } else {
                // No next elements - throw exception as per spec !
                throw new NoSuchElementException("There are no more lines !");
            }

        }


        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported !");
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
}
