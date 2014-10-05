/*

[The "BSD licence"]
Copyright (c) 2004 Jean Bovet
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package edu.usfca.vas.graphics;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class IconManager {

    public static final String ICON_APP = "app.png";
    
    public static final String ICON_RUN = "run.png";
    public static final String ICON_DEBUG = "debug.png";
    public static final String ICON_DEBUG_PROCEED = "resume.png";
    public static final String ICON_DEBUG_PAUSE= "pause.png";
    public static final String ICON_DEBUG_ONE_STEP = "stepinto.png";
    public static final String ICON_CONSOLE = "console.png";
    public static final String ICON_LINK = "link.png";

    public static final String ICON_ARROW = "arrow.png";
    public static final String ICON_STATE = "state.png";
    public static final String ICON_LEFT = "left.png";
    public static final String ICON_LEFT_UNTIL = "leftuntil.png";
    public static final String ICON_LEFT_UNTIL_NOT = "leftuntilnot.png";
    public static final String ICON_RIGHT = "right.png";
    public static final String ICON_RIGHT_UNTIL = "rightuntil.png";
    public static final String ICON_RIGHT_UNTIL_NOT = "rightuntilnot.png";
    public static final String ICON_WRITE = "write.png";
    public static final String ICON_YES = "yes.png";
    public static final String ICON_NO = "no.png";
    public static final String ICON_CALL = "call.png";
    public static final String ICON_OUTPUT = "output.png";
    public static final String ICON_BREAKPOINT = "breakpoint.png";

    public static final String path = "edu/usfca/vas/icons/";

    public static Map cachedIcons = new HashMap();
    public static Map cachedImages = new HashMap();

    public static ImageIcon getIcon(String iconFileName) {
        ImageIcon icon = (ImageIcon)cachedIcons.get(iconFileName);
        if(icon == null) {
            icon = createImageIcon(path+iconFileName);
            if(icon != null)
                cachedIcons.put(iconFileName, icon);
        }
        return icon;
    }

    public static Image getImage(String iconFileName) {
        Image image = (Image)cachedImages.get(iconFileName);
        if(image == null) {
            ImageIcon icon = createImageIcon(path+iconFileName);
            if(icon != null) {
                image = icon.getImage();
                cachedImages.put(iconFileName, image);
            }
        }
        return image;
    }

    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = ClassLoader.getSystemResource(path);
        return imgURL != null ? new ImageIcon(imgURL) : null;
    }

    public static void drawCentered(Graphics2D g, Image image, Rectangle r) {
        g.drawImage(image, (int) (r.x+(r.width-image.getWidth(null))*0.5),
                            (int) (r.y+(r.height-image.getHeight(null))*0.5)+1, null);
    }

    public static void drawUpperLeftCorner(Graphics2D g, Image image, Rectangle r) {
        g.drawImage(image, r.x+2, r.y+2, null);
    }

    public static void drawNorth(Graphics2D g, Image image, Rectangle r) {
        g.drawImage(image, (int) (r.x+(r.width-image.getWidth(null))*0.5), r.y+2, null);
    }

    public static void drawBottomLeftCorner(Graphics2D g, Image image, Rectangle r) {
        g.drawImage(image, r.x+2, r.y+r.height-image.getHeight(null)-2, null);
    }
}
