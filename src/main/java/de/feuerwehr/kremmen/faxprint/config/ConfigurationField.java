package de.feuerwehr.kremmen.faxprint.config;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The ConfigurationField class is used to annotate fields in the Config class.
 * @author smatyba
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurationField {
    
    /**
     * The name of the property in the properties file.
     * @return A String containing the key that represents this field in the 
     * config file.
     */
    String name();    
    
    /**
     * The default value of the field.
     * @return The field's default value
     */
    String defaultValue() default "";
    
    /**
     * Determines whether or not this value may be echoed to the log or any 
     * other output stream. If this is true, the value must never be printed to
     * anything.
     * @return True if the value is prohibited in outputs, false otherwise.
     */
    boolean obfuscated() default false;
    
    /**
     * Determines whether or not a config field can be empty.
     * @return True if the field can be empty, false otherwise 
     */
    boolean emptyAllowed() default false;
    
}