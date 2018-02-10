package de.feuerwehr.kremmen.faxprint.print;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jhomuth
 */
public class Printer {

    /**
     * Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(Printer.class);

    public static void printFile(File f, String printService) throws PDFPrintException, NoSuchPrinterException {
        LOG.debug("Printer {} is called to print file {}",printService,f.getAbsolutePath());
        try (PDDocument document = PDDocument.load(f)) {
            PrintService myPrintService = findPrintService(printService);
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPageable(new PDFPageable(document));
            job.setPrintService(myPrintService);
            job.print();
        } catch (IOException | PrinterException ioe) {
            throw new PDFPrintException("Unable to print file", ioe);
        }
    }

    /**
     * Collects the printer with the provided name
     * @param printerName
     * @return 
     */
    private static PrintService findPrintService(String printerName) throws NoSuchPrinterException {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().trim().equals(printerName)) {
                return printService;
            }
        }
        throw new NoSuchPrinterException("Printer with name "+printerName+" is not available");
    }

    /**
     * Get a list of all printer names
     * @return 
     */
    public static List<String> getPrinters() {
        List<String> printers = new ArrayList<>();
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            printers.add(printService.getName());
        }
        return printers;
    }

}
