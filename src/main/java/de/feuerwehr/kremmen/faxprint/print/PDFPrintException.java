package de.feuerwehr.kremmen.faxprint.print;

/**
 *
 * @author jhomuth
 */
public class PDFPrintException extends Exception {

    /**
     * Creates a new instance of <code>PDFPrintException</code> without detail
     * message.
     */
    public PDFPrintException() {
    }

    /**
     * Constructs an instance of <code>PDFPrintException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public PDFPrintException(String msg) {
        super(msg);
    }

    public PDFPrintException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
}
