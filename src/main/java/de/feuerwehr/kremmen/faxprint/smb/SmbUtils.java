/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.feuerwehr.kremmen.faxprint.smb;

import de.feuerwehr.kremmen.faxprint.watchdog.Watchdog;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jhomuth
 */
public class SmbUtils {

    /**
     * Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(SmbUtils.class);

    private static final SmbFileFilter PDF_FILE_FILTER = new PDFFileFilter();

    /**
     * Creates a new SmbFile with password authentication
     * @param path 
     * @param user
     * @param pass
     * @return
     * @throws SmbException 
     */
    public static SmbFile getSmbFile(String path, String user, String pass) throws SmbException {
        NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication(null, user, pass);
        try {
            return new SmbFile(path, authentication);
        } catch (MalformedURLException ex) {
            throw new SmbException("Unable to access smbfile. Wrong path?", ex);
        }
    }

    /**
     * Copies all content to local
     *
     * @param src
     * @param destinationFolder
     * @throws SmbException
     */
    public static void moveDirectoryContentToLocal(SmbFile src, File destinationFolder) throws SmbException {
        try {
            SmbFile smbFolder = src;
            smbFolder.connect();
            if (!smbFolder.isDirectory()) {
                throw new SmbException("The path " + src + " doesn't point to a directory");
            }

            for (SmbFile srcfile : smbFolder.listFiles(PDF_FILE_FILTER)) {
                copyToLocal(srcfile, new File(destinationFolder, srcfile.getName()));
                srcfile.delete();
            }
        } catch (IOException e) {
            throw new SmbException("Something went wrong during smb copy", e);
        }
    }

    /**
     * Method will copy the file from smb to local harddrive
     *
     * @param src
     * @param destFile
     */
    public static void copyToLocal(SmbFile src, File destFile) {
        InputStream in = null;
        OutputStream out = null;
        try {

            SmbFile fileToGet = src;
            fileToGet.connect();

            in = new BufferedInputStream(new SmbFileInputStream(fileToGet));
            out = new BufferedOutputStream(new FileOutputStream(destFile));

            byte[] buffer = new byte[4096];
            int len = 0;
            while ((len = in.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            LOG.error("Unable to copy file from smb to local", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * Method will copy the file from smb to local harddrive
     *
     * @param src
     * @param destFile
     */
    public static void copyToSmb(File src, SmbFile destFile) {
        InputStream in = null;
        OutputStream out = null;
        try {

            destFile.connect();

            in = new BufferedInputStream(new FileInputStream(src));
            out = new BufferedOutputStream(new SmbFileOutputStream(destFile));

            byte[] buffer = new byte[4096];
            int len = 0;
            while ((len = in.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            LOG.error("Unable to copy file from local to smb", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
            }
        }
    }

    private static class PDFFileFilter implements SmbFileFilter {

        @Override
        public boolean accept(SmbFile file) throws jcifs.smb.SmbException {
            if (file != null && file.isFile() && file.getName().toLowerCase().endsWith("pdf")) {
                return Boolean.TRUE;
            } else {
                LOG.debug("File {} is not accepted... skipping.", file != null ? file.getPath() : "null");
                return Boolean.FALSE;
            }
        }

    }

}
