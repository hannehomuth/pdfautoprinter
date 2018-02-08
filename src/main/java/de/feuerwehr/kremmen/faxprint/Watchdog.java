package de.feuerwehr.kremmen.faxprint;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
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
     * The folder to move files to if there were printed
     */
    private File archiveFolder;

    /**
     * The name of the printservice to use
     */
    private String printService;

    /**
     * The time to wait between directory lookups
     */
    private int waitTime;

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Watchdog.class);

    @Override
    public void run() {
        while (true) {
            LOG.debug("Checking for new files in {}...", folderToMonitor.getAbsolutePath());
            LOG.debug("CanRead: {}", folderToMonitor.canRead());
            try {
                File[] folderContents = folderToMonitor.listFiles((File dir, String name) -> {
                    LOG.info("Checking file {}", name);
                    if (name != null && name.toLowerCase().endsWith("pdf")) {
                        return Boolean.TRUE;
                    } else {
                        return Boolean.FALSE;
                    }
                });
                if (folderContents != null) {
                    for (File folderContent : folderContents) {
                        try {
                            /* Take the pdf and print it */
                            printFile(folderContent);
                            LOG.info("Printed {}", folderContent);
                            moveFile(folderContent, archiveFolder);
                            LOG.info("File {} move to {}", folderContent, archiveFolder);
                        } catch (Exception ex) {
                            LOG.error("Unable to print document...", ex);
                            try {
                                File errorFolder = new File(archiveFolder, "errors");
                                moveFile(folderContent, errorFolder);
                                LOG.info("File {} move to {}", folderContent, errorFolder);
                            } catch (IOException ex1) {
                                LOG.error("Unable to move document...", ex1);
                                System.exit(1);
                            }
                        }
                    }
                }
                LOG.debug("Sleep...");
                Thread.sleep(1000 * waitTime);
            } catch (InterruptedException ex) {
                System.exit(1);
            }
        }

    }

    private void moveFile(File srcFile, File resultDir) throws IOException {
        if (!resultDir.exists()) {
            resultDir.mkdirs();
        }
        File res = new File(resultDir, srcFile.getName());
        Files.move(srcFile.toPath(), res.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private void printFile(File f) throws IOException, PrinterException {
        try (PDDocument document = PDDocument.load(f)) {
            PrintService myPrintService = findPrintService(printService);
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(document));
            job.setPrintService(myPrintService);
            job.print();
        }
    }

    private static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        return null;
    }

    public static List<String> getPrinters() {
        List<String> printers = new ArrayList<>();
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            printers.add(printService.getName());
        }
        return printers;
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
     * Set the value of waitTime
     *
     * @param waitTime new value of waitTime
     */
    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    /**
     * Get the value of printService
     *
     * @return the value of printService
     */
    public String getPrintService() {
        return printService;
    }

    /**
     * Set the value of printService
     *
     * @param printService new value of printService
     */
    public void setPrintService(String printService) {
        this.printService = printService;
    }

    /**
     * Get the value of archiveFolder
     *
     * @return the value of archiveFolder
     */
    public File getArchiveFolder() {
        return archiveFolder;
    }

    /**
     * Set the value of archiveFolder
     *
     * @param archiveFolder new value of archiveFolder
     */
    public void setArchiveFolder(File archiveFolder) {
        this.archiveFolder = archiveFolder;
    }

    /**
     * Get the value of folderToMonitor
     *
     * @return the value of folderToMonitor
     */
    public File getFolderToMonitor() {
        return folderToMonitor;
    }

    /**
     * Set the value of folderToMonitor
     *
     * @param folderToMonitor new value of folderToMonitor
     */
    public void setFolderToMonitor(File folderToMonitor) {
        this.folderToMonitor = folderToMonitor;
    }

}
