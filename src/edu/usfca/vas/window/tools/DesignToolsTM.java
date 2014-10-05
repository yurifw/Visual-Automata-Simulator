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
import edu.usfca.vas.machine.tm.TMOperation;

import javax.swing.*;

public class DesignToolsTM extends DesignToolsAbstract {

    public static final int TOOL_ARROW = 0;
    public static final int TOOL_WRITE = 1;
    public static final int TOOL_LEFT = 2;
    public static final int TOOL_LEFT_UNTIL = 3;
    public static final int TOOL_LEFT_UNTIL_NOT = 4;
    public static final int TOOL_RIGHT = 5;
    public static final int TOOL_RIGHT_UNTIL = 6;
    public static final int TOOL_RIGHT_UNTIL_NOT = 7;
    public static final int TOOL_OUTPUT = 8;
    public static final int TOOL_CALL = 9;
    public static final int TOOL_NO = 10;
    public static final int TOOL_YES = 11;
    public static final int TOOL_LINK = 12;

    public DesignToolsTM() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        mouseButton = createDesignToolButton(IconManager.ICON_ARROW, Localized.getString("tmDTSelect"), TOOL_ARROW);
        createDesignToolSeparator(20);
        createDesignToolButton(IconManager.ICON_LINK, Localized.getString("tmDTLink"), TOOL_LINK);
        createDesignToolSeparator(20);
        createDesignToolButton(IconManager.ICON_WRITE, Localized.getString("tmDTWrite"), TOOL_WRITE);
        createDesignToolButton(IconManager.ICON_OUTPUT, Localized.getString("tmDTOutput"), TOOL_OUTPUT);
        createDesignToolSeparator(20);
        createDesignToolButton(IconManager.ICON_LEFT, Localized.getString("tmDTLeft"), TOOL_LEFT);
        createDesignToolButton(IconManager.ICON_LEFT_UNTIL, Localized.getString("tmDTLeftUntil"), TOOL_LEFT_UNTIL);
        createDesignToolButton(IconManager.ICON_LEFT_UNTIL_NOT, Localized.getString("tmDTLeftUntilNot"), TOOL_LEFT_UNTIL_NOT);
        createDesignToolSeparator(10);
        createDesignToolButton(IconManager.ICON_RIGHT, Localized.getString("tmDTRight"), TOOL_RIGHT);
        createDesignToolButton(IconManager.ICON_RIGHT_UNTIL, Localized.getString("tmDTRightUntil"), TOOL_RIGHT_UNTIL);
        createDesignToolButton(IconManager.ICON_RIGHT_UNTIL_NOT, Localized.getString("tmDTRightUntilNot"), TOOL_RIGHT_UNTIL_NOT);
        createDesignToolSeparator(20);
        createDesignToolButton(IconManager.ICON_CALL, Localized.getString("tmDTCall"), TOOL_CALL);
        createDesignToolSeparator(20);
        createDesignToolButton(IconManager.ICON_NO, Localized.getString("tmDTReject"), TOOL_NO);
        createDesignToolButton(IconManager.ICON_YES, Localized.getString("tmDTAccept"), TOOL_YES);

        selectButton(mouseButton);
    }


    public String popSelectedOperationPattern() {
        String message = null;
        String pattern = null;

        switch(selectedButton.tool) {
            case TOOL_WRITE:
                message = Localized.getString("tmDTNewOpWrite");
                break;
            case TOOL_CALL:
                message = Localized.getString("tmDTNewOpCall");
                break;

            case TOOL_LEFT_UNTIL:
                message = Localized.getString("tmDTNewOpLeftUntil");
                break;
            case TOOL_LEFT_UNTIL_NOT:
                message = Localized.getString("tmDTNewOpLeftUntilNot");
                break;

            case TOOL_RIGHT_UNTIL:
                message = Localized.getString("tmDTNewOpRightUntil");
                break;
            case TOOL_RIGHT_UNTIL_NOT:
                message = Localized.getString("tmDTNewOpRightUntilNot");
                break;

            case TOOL_LEFT:
                pattern = TMOperation.OPS_LEFT;
                break;
            case TOOL_RIGHT:
                pattern = TMOperation.OPS_RIGHT;
                break;
            case TOOL_NO:
                pattern = TMOperation.OPS_NO;
                break;
            case TOOL_YES:
                pattern = TMOperation.OPS_YES;
                break;
            case TOOL_OUTPUT:
                pattern = TMOperation.OPS_OUTPUT;
                break;
        }

        if(message != null) {
            String s = (String)JOptionPane.showInputDialog(null, message, Localized.getString("tmDTNewOpTitle"),
                                            JOptionPane.QUESTION_MESSAGE, null, null, null);
            if(s != null) {
                switch(selectedButton.tool) {
                    case TOOL_WRITE:
                        pattern = s;
                        break;
                    case TOOL_CALL:
                        pattern = TMOperation.OPS_CALL+"["+s+"]";
                        break;
                    case TOOL_LEFT_UNTIL:
                        pattern = TMOperation.OPS_LEFT_UNTIL+s;
                        break;
                    case TOOL_LEFT_UNTIL_NOT:
                        pattern = TMOperation.OPS_LEFT_UNTIL_NOT+s;
                        break;
                    case TOOL_RIGHT_UNTIL:
                        pattern = TMOperation.OPS_RIGHT_UNTIL+s;
                        break;
                    case TOOL_RIGHT_UNTIL_NOT:
                        pattern = TMOperation.OPS_RIGHT_UNTIL_NOT+s;
                        break;
                }
            }
        }

        if(pattern != null)
            consumeSelectedState();

        return pattern;
    }

}
