package de.feuerwehr.kremmen.faxprint.watchdog;

import de.feuerwehr.kremmen.faxprint.AutoPrintManager;
import de.feuerwehr.kremmen.faxprint.config.Config;
import de.feuerwehr.kremmen.faxprint.print.NoSuchPrinterException;
import de.feuerwehr.kremmen.faxprint.print.PDFPrintException;
import de.feuerwehr.kremmen.faxprint.print.Printer;
import de.feuerwehr.kremmen.faxprint.smb.SmbUtils;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jhomuth
 */
public class Watchdog implements Runnable {

    /**
     * The folder to monitor
     */
    private File folderToMonitor;

   

    /**
     * The time to wait between directory lookups
     */
    private int waitTime;

    private NtlmPasswordAuthentication sambaAuth;

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Watchdog.class);
    
    private static final FileFilter PDFFILE_FILTER = new PDFFileFilter();
    
    private static final AutoPrintManager manager = new AutoPrintManager();

    public Watchdog() {
        Config config = Config.getInstance();
        if (config.getBool(Config.KEY_ARCHIVE_FOLDER_IS_SMB_SHARE)) {
            /* Dealing with samba... Set folder to monitor to something temporary */
            this.folderToMonitor = new File(System.getProperty("java.io.tmpdir"), "faxprinter-temp");
            sambaAuth = new NtlmPasswordAuthentication(null, config.get(Config.KEY_SMB_USER_NAME), config.get(Config.KEY_SMB_USER_PASS));
        } else {
            /* No samba... Just set the parameter */
            this.folderToMonitor = new File(config.get(Config.KEY_MONITOR_FOLDER_PATH));
        }
        if(!folderToMonitor.exists()){
            folderToMonitor.mkdirs();
        }
        
        this.waitTime = config.getInt(Config.KEY_FAX_POLL_TIME, 60);

    }

    @Override
    public void run() {
        Config config = Config.getInstance();
        LOG.info("Start scanning");
        while (true) {
            try {
                if (Config.getInstance().getBool(Config.KEY_MONITOR_FOLDER_IS_SMB_SHARE)) {
                    /* Okay, we're dealing with a samba share... Move all contents to local first */
                    SmbFile smbSourceDirectory = new SmbFile(config.get(Config.KEY_MONITOR_FOLDER_PATH), sambaAuth);
                    SmbUtils.moveDirectoryContentToLocal(smbSourceDirectory, folderToMonitor);
                }

                for (File folderContent : folderToMonitor.listFiles(PDFFILE_FILTER)) {
                    manager.notify(folderContent);
                }
            } catch (Exception ex) {
                LOG.error("Something went wrong...", ex);
            }
            
            try {
                Thread.sleep(1000 * waitTime);                
            } catch (Exception e) {
                LOG.error("Was interrupted during wait...");
                System.exit(1);
            }
        }

    }

    /**
     * Get the value of waitTime
     *
     * @return the value of waitTime
     */
    public int getWaitTime() {
        return waitTime;
    }

   
    /**
     * Get the value of folderToMonitor
     *
     * @return the value of folderToMonitor
     */
    public File getFolderToMonitor() {
        return folderToMonitor;
    }

    
    private static class PDFFileFilter implements FileFilter {

       
        @Override
        public boolean accept(File file) {
              if (file != null && file.isFile() && file.getName().toLowerCase().endsWith("pdf")) {
                return Boolean.TRUE;
            } else {
                LOG.debug("File {} is not accepted... skipping.", file != null ? file.getPath() : "null");
                return Boolean.FALSE;
            }
        }

    }

    
}
