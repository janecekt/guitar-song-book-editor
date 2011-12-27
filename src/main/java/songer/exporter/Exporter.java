package songer.exporter;


import songer.parser.nodes.SongBook;

import java.io.File;

public interface Exporter {
    void export(File baseDir, SongBook songBook);
}
