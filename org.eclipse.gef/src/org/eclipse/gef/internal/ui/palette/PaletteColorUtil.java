/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gef.internal.ui.palette;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import org.eclipse.draw2d.ColorConstants;

/**
 * A class to keep miscellaneous palette color utilities.
 * 
 * @author crevells
 * @since 3.4
 */
public class PaletteColorUtil {

public static final Color WIDGET_BACKGROUND = ColorConstants.button;

public static final Color WIDGET_NORMAL_SHADOW = ColorConstants.buttonDarker;

public static final Color WIDGET_DARK_SHADOW = ColorConstants.buttonDarkest;

public static final Color WIDGET_LIST_BACKGROUND = ColorConstants.listBackground;

public static final Color INFO_FOREGROUND = ColorConstants.tooltipForeground;

private static final Color HOVER_COLOR = new Color(null, 254, 237, 205);

private static final Color SELECTED_COLOR = new Color(null, 224, 233, 246);

private static final Color HOVER_COLOR_HICONTRAST = new Color(null, 0, 128, 0);

private static final Color SELECTED_COLOR_HICONTRAST = new Color(null, 128, 0,
    128);

/**
 * Gets the color to be used when hovering over palette items. The color differs
 * in high contrast mode.
 * 
 * @return the hover color
 * @since 3.4
 */
public static Color getHoverColor() {
    Display display = Display.getCurrent();
    if (display == null) {
        display = Display.getDefault();
    }
    if (display.getHighContrast()) {
        return HOVER_COLOR_HICONTRAST;
    }
    return HOVER_COLOR;
}

/**
 * Gets the color to be used when selecting palette items. The color differs in
 * high contrast mode.
 * 
 * @return the selected color
 * @since 3.4
 */
public static Color getSelectedColor() {
    Display display = Display.getCurrent();
    if (display == null) {
        display = Display.getDefault();
    }
    if (display.getHighContrast()) {
        return SELECTED_COLOR_HICONTRAST;
    }
    return SELECTED_COLOR;
}
}