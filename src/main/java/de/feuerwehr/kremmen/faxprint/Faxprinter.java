package de.feuerwehr.kremmen.faxprint;

import de.feuerwehr.kremmen.faxprint.watchdog.Watchdog;
import de.feuerwehr.kremmen.faxprint.config.Config;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import org.slf4j.LoggerFactory;

public class Faxprinter {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Faxprinter.class);

    /**
     * Main entry point. Provide path to properties at parameter one!
     * @param args
     * @throws Exception 
     */
    public static void main(String args[]) throws Exception {
        if(args == null || args.length == 0){
            throw new IllegalStateException("Parameter 0 (path to properties) is not provided");
        }
        
        /* Load configuration */
        Config faxConfig = Config.getInstance();
        faxConfig.load(new File(args[0]));
        

        /**
         * Start watchdog
         */
        Watchdog w = new Watchdog();        
        Thread t = new Thread(w);
        t.start();

    }

}
