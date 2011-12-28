package songer.exporter;


import java.io.File;

import songer.parser.nodes.SongBook;

public interface Exporter {
    void export(File baseDir, SongBook songBook);
}
