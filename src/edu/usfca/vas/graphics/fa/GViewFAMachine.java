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

package edu.usfca.vas.graphics.fa;

import edu.usfca.vas.app.Localized;
import edu.usfca.vas.window.tools.DesignToolsFA;
import edu.usfca.xj.appkit.frame.XJFrame;
import edu.usfca.xj.appkit.gview.GView;
import edu.usfca.xj.appkit.gview.object.GElement;
import edu.usfca.xj.appkit.gview.object.GLink;
import edu.usfca.xj.appkit.menu.XJMenu;
import edu.usfca.xj.appkit.menu.XJMenuItem;
import edu.usfca.xj.appkit.utils.XJAlert;

import javax.swing.*;
import java.awt.*;

public class GViewFAMachine extends GView {

    // ** Menu items

    private static final int MI_ADD_STATE = 0;
    private static final int MI_REMOVE_STATE = 1;
    private static final int MI_SET_START_STATE = 2;
    private static final int MI_SET_ACCEPTED_STATE = 3;
    private static final int MI_CLEAR_ALL = 4;
    private static final int MI_EDIT_LINK = 5;
    private static final int MI_REMOVE_LINK = 6;

    protected DesignToolsFA designToolFA = null;
    protected XJFrame parent;

    public GViewFAMachine(XJFrame parent) {
        this.parent = parent;
    }

    public int defaultLinkShape() {
        return GLink.SHAPE_ARC;
    }

    public void setDesignToolsPanel(DesignToolsFA designToolFA) {
        this.designToolFA = designToolFA;
    }

    public void setMachine(GElementFAMachine machine) {
        setRootElement(machine);
    }

    public GElementFAMachine getMachine() {
        return (GElementFAMachine)getRootElement();
    }

    public JPopupMenu getContextualMenu(GElement element) {
        boolean stateSelected = false;
        boolean linkSelected = false;

        if(element != null) {
            stateSelected = element.getClass().equals(GElementFAState.class);
            linkSelected = element.getClass().equals(GLink.class);
        }

        JPopupMenu menu = new JPopupMenu();
        menu.addPopupMenuListener(new MyContextualMenuListener());

        if(stateSelected) {
            GElementFAState state = (GElementFAState)element;
            addMenuItem(menu, state.state.start?Localized.getString("faMIRemoveStartState"):Localized.getString("faMISetStartState"), MI_SET_START_STATE, element);
            addMenuItem(menu, state.state.accepted?Localized.getString("faMIRemoveAcceptedState"):Localized.getString("faMISetAcceptedState"), MI_SET_ACCEPTED_STATE, element);
            menu.addSeparator();
            addMenuItem(menu, Localized.getString("faMIDelete"), MI_REMOVE_STATE, element);
        } else if(linkSelected) {
            addMenuItem(menu, Localized.getString("faMIEdit"), MI_EDIT_LINK, element);
            addMenuItem(menu, Localized.getString("faMIDelete"), MI_REMOVE_LINK, element);
        } else {
            addMenuItem(menu, Localized.getString("faMIAddState"), MI_ADD_STATE, null);
            menu.addSeparator();
            addMenuItem(menu, Localized.getString("faMIDeleteAll"), MI_CLEAR_ALL, null);
        }

        return menu;
    }

    public void createStateAtXY(double x, double y) {
        String s = (String)JOptionPane.showInputDialog(null, Localized.getString("faNewStateMessage"), Localized.getString("faNewStateTitle"),
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        if(s != null) {
            if(getMachine().getMachine().containsStateName(s))
                XJAlert.display(parent.getJavaContainer(), Localized.getString("faNewStateTitle"), Localized.getString("faNewStateAlreadyExists"));
            else {
                getMachine().addStateAtXY(s, x, y);
                changeDone();
                repaint();
            }
        }
    }

    public void editState(GElementFAState state) {
        String s = (String)JOptionPane.showInputDialog(null, Localized.getString("faEditStateMessage"), Localized.getString("faEditStateTitle"),
                                    JOptionPane.QUESTION_MESSAGE, null, null, state.state.name);
        if(s != null) {
            if(getMachine().getMachine().containsStateName(s))
                XJAlert.display(parent.getJavaContainer(), Localized.getString("faEditStateTitle"), Localized.getString("faEditStateAlreadyExists"));
            else {
                getMachine().getMachine().renameState(state.state, state.state.name, s);
                changeDone();
                repaint();
            }
        }
    }

    public void handleMenuEvent(XJMenu menu, XJMenuItem item) {
        switch(item.getTag()) {
            case MI_ADD_STATE:
                createStateAtXY(getLastMousePosition().getX(), getLastMousePosition().getY());
                break;

            case MI_REMOVE_STATE: {
                getMachine().removeState((GElementFAState)item.getObject());
                changeDone();
                break;
            }

            case MI_SET_START_STATE: {
                GElementFAState state = (GElementFAState)item.getObject();
                getMachine().toggleStartState(state);
                changeDone();
                break;
            }

            case MI_SET_ACCEPTED_STATE: {
                GElementFAState state = (GElementFAState)item.getObject();
                state.toggleAccepted();
                changeDone();
                break;
            }

            case MI_CLEAR_ALL:
                getMachine().clear();
                changeDone();
                break;

            case MI_EDIT_LINK:
                getMachine().editLink((GLink)item.getObject());
                changeDone();
                break;

            case MI_REMOVE_LINK:
                getMachine().removeLink((GLink)item.getObject());
                changeDone();
                break;
        }
    }

    public void eventCreateElement(Point p, boolean doubleclick) {
        if(doubleclick)
            createStateAtXY(p.x, p.y);
        else if(designToolFA.getSelectedTool() != DesignToolsFA.TOOL_ARROW) {
            int tool = designToolFA.getSelectedTool();
            if(tool == DesignToolsFA.TOOL_ARROW)
                return;

            if(tool == DesignToolsFA.TOOL_LINK)
                return;

            String pattern = designToolFA.popSelectedStatePattern();
            if(pattern != null) {
                getMachine().addStateAtXY(pattern, p.x, p.y);
                changeDone();
                repaint();
            }
        }

    }

    public boolean eventCanCreateLink() {
        int tool = designToolFA.getSelectedTool();
        if(tool == DesignToolsFA.TOOL_LINK) {
            designToolFA.consumeSelectedState();
            return true;
        } else
            return false;
    }

    public void eventCreateLink(GElement source, String sourceAnchorKey, GElement target, String targetAnchorKey, int shape, Point p) {
        getMachine().createLink((GElementFAState)source, sourceAnchorKey, (GElementFAState)target, targetAnchorKey, shape, p);
        changeDone();
    }

    public void eventEditElement(GElement e) {
        if(e instanceof GLink) {
            if(getMachine().editLink((GLink)e)) {
                changeDone();
                repaint();
            }
        } else if(e instanceof GElementFAState) {
            editState((GElementFAState)e);
        }
    }

}
