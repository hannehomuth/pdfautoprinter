/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.feuerwehr.kremmen.faxprint.smb;

/**
 *
 * @author jhomuth
 */
public class SmbException extends Exception {

    /**
     * Creates a new instance of <code>SmbException</code> without detail
     * message.
     */
    public SmbException() {
    }

    /**
     * Constructs an instance of <code>SmbException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public SmbException(String msg) {
        super(msg);
    }

    public SmbException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
    
}
