package de.feuerwehr.kremmen.faxprint;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.LoggerFactory;

public class Faxprinter {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Faxprinter.class);

    public static void main(String args[]) throws Exception {
        for (String arg : Watchdog.getPrinters()) {
            LOG.info("Printer {}", arg);
        }
        Properties p = loadFromProperties(new File(args[0]));

        Watchdog w = new Watchdog();
        w.setArchiveFolder(new File(p.getProperty("ARCHIVE_FOLDER")));
        w.setFolderToMonitor(new File(p.getProperty("MONITOR_FOLDER")));
        w.setWaitTime(Integer.valueOf(p.getProperty("POLL_TIME")));
        w.setPrintService(p.getProperty("PRINTER_NAME"));
        Thread t = new Thread(w);
        t.start();

    }

    public static Properties loadFromProperties(File f) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(f));
        return props;
    }

}
