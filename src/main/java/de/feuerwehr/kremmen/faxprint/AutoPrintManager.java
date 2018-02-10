/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.feuerwehr.kremmen.faxprint;

import de.feuerwehr.kremmen.faxprint.config.Config;
import de.feuerwehr.kremmen.faxprint.print.NoSuchPrinterException;
import de.feuerwehr.kremmen.faxprint.print.PDFPrintException;
import de.feuerwehr.kremmen.faxprint.print.Printer;
import de.feuerwehr.kremmen.faxprint.watchdog.Watchdog;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jhomuth
 */
public class AutoPrintManager {

    private static final Logger LOG = LoggerFactory.getLogger(AutoPrintManager.class);

    /**
     * The folder to move files to if there were printed
     */
    private File archiveFolder;

    /**
     * The name of the printservice to use
     */
    private String printService;

    public AutoPrintManager() {
        Config config = Config.getInstance();
        this.printService = config.get(Config.KEY_PRINTER_NAME);
        this.archiveFolder = new File(config.get(Config.KEY_ARCHIVE_FOLDER_PATH));
    }

    public void notify(File folderContent) {
        try {
            /* Take the pdf and print it */
            Printer.printFile(folderContent, printService);
            LOG.info("Printed {}", folderContent);
            moveFile(folderContent, archiveFolder);
            LOG.info("File {} archived to {}", folderContent, archiveFolder);
        } catch (IOException | PDFPrintException ex) {
            LOG.error("Unable to print document...", ex);
            try {
                File errorFolder = new File(archiveFolder, "errors");
                moveFile(folderContent, errorFolder);
                LOG.info("File {} moved to {}", folderContent, errorFolder);
            } catch (IOException ex1) {
                LOG.error("Unable to move document...", ex1);
                throw new IllegalStateException("Couldn't print and move. What should I do? Bye...", ex);
            }
        } catch (NoSuchPrinterException nse) {
            LOG.error("Unable to print to printer {} because printer doesn't exists", printService);
            LOG.error("Please pick a printer from list below");
            for (String printer : Printer.getPrinters()) {
                LOG.error("\t{}", printer);
            }
            throw new IllegalStateException("Please pick correct printer");
        }
    }

    private void moveFile(File srcFile, File resultDir) throws IOException {
        if (!resultDir.exists()) {
            resultDir.mkdirs();
        }
        File res = new File(resultDir, srcFile.getName());
        Files.move(srcFile.toPath(), res.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

}
