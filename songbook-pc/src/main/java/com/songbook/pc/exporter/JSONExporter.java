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
package com.songbook.pc.exporter;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.songbook.core.model.SongBook;
import com.songbook.core.util.FileIO;
import com.songbook.core.util.StringUtil;
import com.songbook.pc.util.FreeMakerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONExporter implements Exporter {
    private static final Logger logger = LoggerFactory.getLogger(EPubExporter.class);
    private static final String SONGBOOK_JSON_SONG_TEMPLATE = "/export/json/json.ftl";

    @Override
    public File export(File baseDir, SongBook songBook) {
        logger.info("Starting export to JSON.");

        // Create directory
        File outputDir = new File(baseDir, "json");
        FileIO.createDirectory(outputDir);

        // Export to JSON using FreeMarker template (content only)
        String content = exportJson(songBook, null);
        String hash = generateHash(content);

        String contentWithDate = exportJson(songBook, new Date());

        File outputFileName = new File(outputDir, "songbook-" + hash + ".json");
        FileIO.writeStringToFile(outputFileName.getAbsolutePath(), StandardCharsets.UTF_8, contentWithDate);

        logger.info("COMPLETED export to JSON");

        return outputFileName;
    }

    private String exportJson(SongBook songBook, Date generatedOn) {
        Map<String, Object> model = new HashMap<>();
        model.put("songNodes", songBook.getSongNodeList());
        model.put("generatedOn", generatedOn);
        return FreeMakerUtil.processTemplate(model, SONGBOOK_JSON_SONG_TEMPLATE);
    }

    private String generateHash(String content) {
         try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = digest.digest(content.getBytes());
            return StringUtil.bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Unable to calculate hash JSON songbook " + ex.getMessage(), ex);
        }

    }
}
