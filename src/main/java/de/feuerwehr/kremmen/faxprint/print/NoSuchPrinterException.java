
package de.feuerwehr.kremmen.faxprint.print;

/**
 *
 * @author jhomuth
 */
public class NoSuchPrinterException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchPrinterException</code> without
     * detail message.
     */
    public NoSuchPrinterException() {
    }

    /**
     * Constructs an instance of <code>NoSuchPrinterException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchPrinterException(String msg) {
        super(msg);
    }
}
