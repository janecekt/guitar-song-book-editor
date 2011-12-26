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
package songer.util;

import java.io.*;

/**
 * Wrapper class of static methods implementing File <-> String IO-operations.
 * @author Tomas Janecek
 */
public class FileIO {
	
	/** 
	 * Writes the string content to file path using the specified encoding.
	 * @param path Path to file where the string should be written.
	 * @param encoding Encoding to be used.
	 * @param content String to be written to the file.
	 */
	public static void writeStringToFile(String path, String encoding, String content) throws FileNotFoundException, UnsupportedEncodingException, IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), encoding));
			bw.write(content);
		} finally {
			if (bw != null) {
				bw.close();
			}
		}
	}

	
	
	
	/**
	 * Reads the content of the file using the specified encoding and returns it as String.
	 * @param path Path to the file whose content should be read.
	 * @param encoding Encoding to be used.
	 * @return The content of the file as String.
	 */
	public static String readFileToString(String path, String encoding) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path), encoding));
			String out = "";
			String line;
			while ((line = br.readLine()) != null) {
				out += line + "\n";
			}
			return out;
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}
    
    
    public static String readResourceToString(String resourceName) {
        StringBuilder sb = new StringBuilder();
        BufferedInputStream inStream = new BufferedInputStream(FileIO.class.getResourceAsStream(resourceName));
        try {
            byte[] chars = new byte[1024];
            int bytesRead = 0;
            while( (bytesRead = inStream.read(chars)) > -1){
                sb.append(new String(chars, 0, bytesRead));
            }
            return sb.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read resource " + resourceName, ex);
        } finally {
            try {
                inStream.close();
            } catch (IOException ex) {
                throw new RuntimeException("Failed to close resource " + resourceName, ex);
            }
        }
    }
}
