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

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class of static methods implementing File <-> String IO-operations.
 * @author Tomas Janecek
 */
public class FileIO {
    private static final Logger logger = LoggerFactory.getLogger(FileIO.class.getSimpleName());


    /**
     * Writes the string content to file path using the specified encoding.
     * @param path     Path to file where the string should be written.
     * @param encoding Encoding to be used.
     * @param content  String to be written to the file.
     */
    public static void writeStringToFile(String path, Charset encoding, String content) {
        try {
            writeStringToStream(new FileOutputStream(path), encoding, content, "file " + path);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("File " + path + " was not found !", ex);
        }
    }


    /**
     * Reads the content of the file using the specified encoding and returns it as String.
     * @param path     Path to the file whose content should be read.
     * @param encoding Encoding to be used.
     * @return The content of the file as String.
     */
    public static String readFileToString(String path, Charset encoding) {
        try {
            return readStreamToString(new FileInputStream(path), encoding, "file " + path);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("File " + path + " was not found !", ex);
        }
    }


    /**
     * Reads a resource with the given name into string.
     * @param resourceName Resource path to be read.
     * @return String content of the resource.
     */
    public static String readResourceToString(String resourceName) {
        BufferedInputStream inStream = new BufferedInputStream(FileIO.class.getResourceAsStream(resourceName));
        return readStreamToString(inStream, Charset.forName("UTF8"), "resource " + resourceName);
    }


    /**
     * Reads the input stream into string.
     * @param inStream Input stream to be read.
     * @param encoding Charset to be used for conversion of bytes to string.
     * @param description Description used in exceptions.
     * @return String content of the input stream.
     */
    public static String readStreamToString(InputStream inStream, Charset encoding, String description) {
        ReaderIterable readerIterable = null;
        try {
            readerIterable = new ReaderIterable(inStream, encoding, description);
            StringBuilder builder = new StringBuilder();
            for (String line : readerIterable) {
                builder.append(line).append("\n");
            }
            return builder.toString();
        } finally {
            if (readerIterable != null) {
                readerIterable.close();
            }
        }
    }


    /**
     * Writes a given string into output stream.
     * @param outStream Output stream to be written to.
     * @param encoding Charset to be used.
     * @param content Content to be written into the output stream.
     * @param description Description used in exceptions.
     */
    public static void writeStringToStream(OutputStream outStream, Charset encoding, String content, String description) {
        BufferedWriter bw = null;
        try {
            try {
                bw = new BufferedWriter(new OutputStreamWriter(outStream, encoding));
                bw.write(content);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to write to " + description, ex);
            }
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException ex) {
                logger.error("Failed to close " + description, ex);
            }
        }
    }


    /**
     * Copy the content of the input stream into the output stream.
     * - InputStream will be automatically closed after completion.
     * - OutputStream will remain open.
     * @param inputStream  The input stream to copy from.
     * @param outputStream The output stream to copy to.
     * @throws IOException If any error occurs during the copy.
     */
    public static void appendInputStreamToOutputStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        ReadableByteChannel inputChannel = Channels.newChannel(inputStream);

        try {
            WritableByteChannel outputChannel = Channels.newChannel(outputStream);
            ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
            while (inputChannel.read(buffer) != -1) {
                // prepare the buffer to be drained
                buffer.flip();

                // write to the channel, may block
                outputChannel.write(buffer);

                // If partial transfer, shift remainder down
                // If buffer is empty, same as doing clear()
                buffer.compact();
            }

            // EOF will leave buffer in fill state
            buffer.flip();

            // make sure the buffer is fully drained.
            while (buffer.hasRemaining()) {
                outputChannel.write(buffer);
            }
        } finally {
            inputChannel.close();
        }
    }


    public static void createDirectory(File directory) {
        // Path exists - nothing to do
        if (directory.exists()) {
            return;
        }

        // Create directory
        if (directory.mkdirs()) {
            logger.info("Directory {} created.", directory.getAbsolutePath());
        } else {
            throw new RuntimeException("Filed to create directory - " + directory.getAbsolutePath());
        }
    }


    public static void deleteDirectory(File directory) {
        // If directory does not exist do nothing
        if (!directory.exists()) {
            return;
        }

        if (directory.isDirectory()) {
            // Recursively delete children
            for (String child : directory.list()) {
                deleteDirectory(new File(directory, child));
            }
        }

        // The directory is now empty so delete it
        if (!directory.delete()) {
            throw new RuntimeException("Could not delete directory " + directory.getAbsolutePath());
        }
    }
}
