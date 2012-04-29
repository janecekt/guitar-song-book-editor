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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

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
    public static void writeStringToFile(String path, String encoding, String content) {
        BufferedWriter bw = null;
        try {
            try {
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), encoding));
                bw.write(content);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to write to file " + path, ex);
            }
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException ex) {
                logger.error("Failed to close file " + path, ex);
            }
        }
    }


    /**
     * Reads the content of the file using the specified encoding and returns it as String.
     * @param path     Path to the file whose content should be read.
     * @param encoding Encoding to be used.
     * @return The content of the file as String.
     */
    public static String readFileToString(String path, String encoding) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path), encoding));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return builder.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read file into string - " + path, ex);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                logger.error("Failed to close file " + path, ex);
            }
        }
    }


    public static String readResourceToString(String resourceName) {
        BufferedInputStream inStream = new BufferedInputStream(FileIO.class.getResourceAsStream(resourceName));
        try {
            StringBuilder builder = new StringBuilder();
            byte[] chars = new byte[1024];
            int bytesRead;
            while ((bytesRead = inStream.read(chars)) > -1) {
                builder.append(new String(chars, 0, bytesRead));
            }
            return builder.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read resource " + resourceName, ex);
        } finally {
            try {
                inStream.close();
            } catch (IOException ex) {
                logger.error("Failed to close resource " + resourceName, ex);
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
