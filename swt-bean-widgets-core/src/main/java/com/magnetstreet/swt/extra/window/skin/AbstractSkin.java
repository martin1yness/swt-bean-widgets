package com.magnetstreet.swt.extra.window.skin;

import com.magnetstreet.swt.extra.window.ColorScheme;
import org.eclipse.swt.graphics.Image;

import java.util.Hashtable;

/**
 * Abstract Skin
 *
 * Intended to be implemented and handed to a Window class to apply a high level
 * skin to an application. Skins are designed to be global in nature but will attempt
 * to limit features that require this usage.
 * @author Martin Dale Lyness <martin.lyness@gmail.com>
 * @since 1.1.0
 */
public abstract class AbstractSkin {
    private ColorScheme colorScheme;
    private Hashtable<String, Image> customRegisteredImages = new Hashtable<String, Image>();

}
