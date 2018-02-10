package de.feuerwehr.kremmen.faxprint.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Config class is a Singleton class providing the configuration of the SWP
 * Migration tool in a conventient way. It also provides methods for loading and
 * saving the configuration from/to a file. It implements the Changeable
 * interface to keep track of configuration changes from other classes.
 *
 * @author jhomuth
 */
public final class Config {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    /**
     * The singleton instance of this class.
     */
    private static final Config INSTANCE = new Config();

    /**
     * Key used for the configuration parameter describing the path to monitor
     * for new pdf's
     */
    public static final String KEY_MONITOR_FOLDER_PATH = "de.feuerwehr.kremmen.fax.monitor.folder.path";

    @ConfigurationField(name = KEY_MONITOR_FOLDER_PATH, emptyAllowed = false)
    private static String monitorFolderPath;
    
    

    /**
     * Key used for the configuration parameter describing whether the path to
     * monitor for new pdf's is a smb share or not
     */
    public static final String KEY_MONITOR_FOLDER_IS_SMB_SHARE = "de.feuerwehr.kremmen.fax.monitor.folder.is.smb";

    @ConfigurationField(name = KEY_MONITOR_FOLDER_IS_SMB_SHARE, defaultValue = "true", emptyAllowed = false)
    private static String monitorFolderIsSmbShare;
    
    

    /**
     * Key used for the configuration parameter describing the path to monitor
     * for new pdf's
     */
    public static final String KEY_ARCHIVE_FOLDER_PATH = "de.feuerwehr.kremmen.fax.archive.folder.path";

    @ConfigurationField(name = KEY_ARCHIVE_FOLDER_PATH, emptyAllowed = false)
    private static String archiveFolderPath;
    
    

    /**
     * Key used for the configuration parameter describing whether the path to
     * copy printed pdf's to is a smb share or not
     */
    public static final String KEY_ARCHIVE_FOLDER_IS_SMB_SHARE = "de.feuerwehr.kremmen.fax.archive.folder.is.smb";

    @ConfigurationField(name = KEY_ARCHIVE_FOLDER_IS_SMB_SHARE, defaultValue = "true", emptyAllowed = false)
    private static String archiveFolderIsSmbShare;
    
    
    
    /**
     * Key used for the configuration parameter describing the user name
     * to use when authenticate at smb share
     */
    public static final String KEY_SMB_USER_NAME = "de.feuerwehr.kremmen.smb.user.name";

    @ConfigurationField(name = KEY_SMB_USER_NAME, emptyAllowed = true, obfuscated = false)
    private static String smbUserName;
    
    
    
    /**
     * Key used for the configuration parameter describing the user name
     * to use when authenticate at smb share
     */
    public static final String KEY_SMB_USER_PASS = "de.feuerwehr.kremmen.smb.user.password";

    @ConfigurationField(name = KEY_SMB_USER_PASS, emptyAllowed = true, obfuscated = true)
    private static String smbUserPassword;
    
    
    
    /**
     * Key used for the configuration parameter describing the printer to use
     *
     */
    public static final String KEY_PRINTER_NAME = "de.feuerwehr.kremmen.printer.name";

    @ConfigurationField(name = KEY_PRINTER_NAME, emptyAllowed = false, obfuscated = false)
    private static String printService;
    
    
    
    /**
     * Key used for the configuration parameter describing the printer to use
     *
     */
    public static final String KEY_FAX_POLL_TIME = "de.feuerwehr.kremmen.fax.polltime";

    @ConfigurationField(name = KEY_FAX_POLL_TIME, emptyAllowed = false, obfuscated = false)
    private static String polltime;
    
    

    /**
     * As this is a singleton, the only constructor is private.
     */
    private Config() {
    }

    /**
     * Gets the only instance of this class.
     *
     * @return The only instance of Config there is.
     */
    public static Config getInstance() {
        return INSTANCE;
    }

    /**
     * Determines whether or not the singleton instance of the Config class
     * contains a value mapped to a given key.
     *
     * @param key The key to search a mapped value for.
     * @return True if there is a value mapped to {@code key}, false otherwise.
     */
    public boolean hasKey(final String key) {
        try {
            final Field[] allFields = getClass().getDeclaredFields();
            for (final Field f : allFields) {
                final ConfigurationField cf = f.getAnnotation(
                        ConfigurationField.class);
                if (cf != null) {
                    if (cf.name().equalsIgnoreCase(key)) {
                        return true;
                    }
                }
            }
        } catch (IllegalArgumentException | SecurityException ex) {
            LOG.error("Error getting value '{}' from config: {}",
                    key, ex.getMessage(), ex);
        }

        return false;
    }

    public int getInt(final String key, final int defaultValue) {
        try {
            return Integer.parseInt(this.get(key));
        } catch (Exception ex) {
            LOG.warn("Unable to parse {} as integer, returning default value ({})", key, defaultValue);
            return defaultValue;
        }
    }

    public boolean getBool(final String key) {
        try {
            return Boolean.parseBoolean(this.get(key));
        } catch (final Exception ex) {
            LOG.warn("Unable to parse {} as boolean, returning default value ({})", key, getDefaultValue(key));
            return Boolean.valueOf(getDefaultValue(key));
        }
    }

    /**
     * Obtains a value mapped to a provided key.
     *
     * @param key The key to search the mapped value for.
     * @return The String value mapped to {@code key} or null if there is no
     * such key. If the value mapped to {@code key} has no content, a default
     * value will be returned that depends on the queried field.
     */
    public String get(String key) {
        try {
            final Field[] allFields = getClass().getDeclaredFields();
            for (final Field f : allFields) {
                final ConfigurationField cf = f.getAnnotation(ConfigurationField.class);
                if (cf != null) {

                    if (cf.name().equalsIgnoreCase(key)) {
                        final Object o = f.get(this);
                        if (o == null) {
                            return cf.defaultValue();
                        }

                        final String currVal = f.get(this).toString();
                        if (currVal == null || currVal.length() == 0) {
                            final String defVal = cf.defaultValue();
                            if (defVal == null || defVal.isEmpty()) {
                                return "";
                            }
                            return cf.defaultValue();
                        }
                        return currVal;
                    }
                }
            }
        } catch (IllegalAccessException |
                IllegalArgumentException |
                SecurityException ex) {
            LOG.error("Error getting value '{}' from config: {}",
                    key, ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * Sets a value in the singleton instance of Config based on the key that
     * value is mapped to.
     *
     * @param key The key the value to change is mapped to
     * @param value The new value.
     * @return True if the update succeeds, false otherwise (or when there is no
     * value mapped to {@code key}).
     */
    public boolean set(String key, String value) {
        LOG.debug("Setting Config value '{}' --> '{}", key, value);
        try {
            final Field[] allFields = getClass().getDeclaredFields();
            for (final Field f : allFields) {
                final ConfigurationField cf = f.getAnnotation(ConfigurationField.class);
                if (cf != null) {
                    if (cf.name().equalsIgnoreCase(key)) {
                        String currentValue = null;
                        if (f.get(this) == null) {
                            currentValue = "";
                        } else {
                            currentValue = f.get(this).toString();
                        }
                        if (currentValue.equalsIgnoreCase(value)) {
                            LOG.warn("Not changing value of setting '{}', new value is the same", key);
                            return true;
                        }
                        f.set(this, value);
                        return true;
                    }
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException ex) {
            LOG.error("Error setting value '{}' to '{}': {}", key, value, ex.getMessage(), ex);
        }

        return false;
    }

    /**
     * Loads a file as a properties file and fills the singleton instance of
     * Config with the values inside the file.
     *
     * @param configFile The file to load as the new configuraiton.
     * @throws IOException If loading the file fails.
     */
    public void load(File configFile) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(configFile));

        try {
            final Field[] allFields = getClass().getDeclaredFields();
            for (final Field f : allFields) {
                final ConfigurationField cf = f.getAnnotation(ConfigurationField.class);
                if (cf != null) {
                    if (!cf.obfuscated()) {
                        LOG.info("Loading config value '{}' --> '{}'", cf.name(), properties.get(cf.name()));
                    } else {
                        LOG.info("Loading config value '{}' --> '{}'", cf.name(), "***");
                    }

                    f.set(this, properties.get(cf.name()));
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException ex) {
            LOG.error("Unable to process config (reflection problem): {}", ex.getMessage(), ex);
        }
    }

    public String getDefaultValue(String key) {
        try {
            final Field[] allFields = getClass().getDeclaredFields();
            for (final Field f : allFields) {
                final ConfigurationField cf = f.getAnnotation(ConfigurationField.class);
                if (cf != null) {
                    if (cf.name().equalsIgnoreCase(key)) {
                        return cf.defaultValue();
                    }
                }
            }
        } catch (IllegalArgumentException |
                SecurityException ex) {
            LOG.error("Error getting value '{}' from config: {}",
                    key, ex.getMessage(), ex);
        }

        return null;
    }

}
