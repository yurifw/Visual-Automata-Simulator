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

package edu.usfca.vas.window.tools;

import edu.usfca.vas.app.Localized;
import edu.usfca.vas.graphics.IconManager;

import javax.swing.*;

public class DesignToolsFA extends DesignToolsAbstract {

    public static final int TOOL_ARROW = 0;
    public static final int TOOL_LINK = 1;
    public static final int TOOL_STATE = 2;

    public DesignToolsFA() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        mouseButton = createDesignToolButton(IconManager.ICON_ARROW, Localized.getString("faDTSelect"), TOOL_ARROW);
        createDesignToolSeparator(20);
        createDesignToolButton(IconManager.ICON_LINK, Localized.getString("faDTLink"), TOOL_LINK);
        createDesignToolButton(IconManager.ICON_STATE, Localized.getString("faDTState"), TOOL_STATE);

        selectButton(mouseButton);
    }


    public String popSelectedStatePattern() {
        String s = (String)JOptionPane.showInputDialog(null, Localized.getString("faDTNewStateMessage"),
                                        Localized.getString("faDTNewStateTitle"),
                                        JOptionPane.QUESTION_MESSAGE, null, null, null);
        if(s != null) {
            consumeSelectedState();
        }

        return s;
    }

}
