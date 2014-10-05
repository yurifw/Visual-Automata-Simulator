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

package edu.usfca.vas.graphics.tm;

import edu.usfca.vas.app.Localized;
import edu.usfca.vas.window.tools.DesignToolsTM;
import edu.usfca.xj.appkit.gview.GView;
import edu.usfca.xj.appkit.gview.object.GElement;
import edu.usfca.xj.appkit.gview.object.GLink;
import edu.usfca.xj.appkit.menu.XJMenu;
import edu.usfca.xj.appkit.menu.XJMenuItem;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

public class GViewTMMachine extends GView {

    // ** Menu items

    private static final int MI_ADD_OP = 0;
    private static final int MI_REMOVE_OP = 1;
    private static final int MI_SET_START_OP = 2;
    private static final int MI_CLEAR_ALL = 3;
    private static final int MI_EDIT_LINK = 4;
    private static final int MI_REMOVE_LINK = 5;
    private static final int MI_LINK_CHANGE_SHAPE = 6;
    private static final int MI_SET_OP_BREAKPOINT = 7;

    private DesignToolsTM designToolTM = null;

    public GViewTMMachine() {
    }

    public int defaultLinkShape() {
        return GLink.SHAPE_ELBOW;
    }

    public void setDesignToolsPanel(DesignToolsTM designToolTM) {
        this.designToolTM = designToolTM;
    }

    public void setMachine(GElementTMMachine machine) {
        setRootElement(machine);
    }

    public GElementTMMachine getMachine() {
        return (GElementTMMachine)getRootElement();
    }

    public JPopupMenu getContextualMenu(GElement element) {
        boolean opSelected = false;
        boolean linkSelected = false;

        if(element != null) {
            opSelected = element instanceof GElementTMOperation;
            linkSelected = element instanceof GLink;
        }

        JPopupMenu menu = new JPopupMenu();
        menu.addPopupMenuListener(new MyContextualMenuListener());

        if(opSelected) {
            GElementTMOperation op = (GElementTMOperation)element;
            addMenuItem(menu, op.getOperation().isStart()?Localized.getString("tmMIRemoveStartOperation"):Localized.getString("tmMISetStartOperation"), MI_SET_START_OP, element);
            addMenuItem(menu, op.getOperation().isBreakPoint()?Localized.getString("tmMIRemoveBreakpoint"):Localized.getString("tmMISetBreakpoint"), MI_SET_OP_BREAKPOINT, element);
            menu.addSeparator();
            addMenuItem(menu, Localized.getString("tmMIDelete"), MI_REMOVE_OP, element);
        } else if(linkSelected) {
            addMenuItem(menu, Localized.getString("tmMIEdit"), MI_EDIT_LINK, element);
            addMenuItem(menu, Localized.getString("tmMIDelete"), MI_REMOVE_LINK, element);
            menu.addSeparator();
            addMenuItem(menu, Localized.getString("tmMILinkToggleShape"), MI_LINK_CHANGE_SHAPE, element);
        } else {
            addMenuItem(menu, Localized.getString("tmMIAddOperation"), MI_ADD_OP, null);
            menu.addSeparator();
            addMenuItem(menu, Localized.getString("tmMIDeleteAll"), MI_CLEAR_ALL, null);
        }

        return menu;
    }

    public void createOperationAtXY(double x, double y) {
        String pattern = (String)JOptionPane.showInputDialog(null, Localized.getString("tmNewOperationMessage"),
                Localized.getString("tmNewOperationTitle"),
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        if(pattern != null) {
            getMachine().addOperationAtXY(pattern, x, y);
            changeDone();
            repaint();
        }
    }

    public void editOperation(GElementTMOperation op) {
        String pattern = (String)JOptionPane.showInputDialog(null, Localized.getString("tmEditOperationMessage"),
                                    Localized.getString("tmEditOperationTitle"),
                                    JOptionPane.QUESTION_MESSAGE, null, null, op.getLabel());
        if(pattern != null) {
            GElementTMOperation.parse(new StringBuffer(pattern), op);
            op.setLabel(pattern);
            changeDone();
            repaint();
        }
    }

    public void removeOperations(GElementTMOperation e) {
        getMachine().removeOperation(e);
        Iterator iterator = getSelectedElements().iterator();
        while(iterator.hasNext()) {
            GElement element = (GElement)iterator.next();
            if((element instanceof GElementTMOperation) && element != e)
                getMachine().removeOperation((GElementTMOperation)element);
        }
        changeDone();
    }

    public void handleMenuEvent(XJMenu menu, XJMenuItem item) {
        switch(item.getTag()) {
            case MI_ADD_OP:
                createOperationAtXY(getLastMousePosition().getX(), getLastMousePosition().getY());
                break;

            case MI_REMOVE_OP: {
                removeOperations((GElementTMOperation)item.getObject());
                break;
            }

            case MI_SET_START_OP: {
                GElementTMOperation op = (GElementTMOperation)item.getObject();
                getMachine().toggleStartOperation(op);
                changeDone();
                break;
            }

            case MI_SET_OP_BREAKPOINT: {
                GElementTMOperation op = (GElementTMOperation)item.getObject();
                op.toggleBreakpoint();
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

            case MI_LINK_CHANGE_SHAPE:
                GLink link = (GLink)item.getObject();
                link.toggleShape();
                repaint();
                changeDone();
                break;
        }
    }

    public void eventCreateElement(Point p, boolean doubleclick) {
        if(doubleclick)
            createOperationAtXY(p.x, p.y);
        else if(designToolTM.getSelectedTool() != DesignToolsTM.TOOL_ARROW) {
            int tool = designToolTM.getSelectedTool();
            if(tool == DesignToolsTM.TOOL_ARROW)
                return;

            if(tool == DesignToolsTM.TOOL_LINK)
                return;

            String pattern = designToolTM.popSelectedOperationPattern();
            if(pattern != null) {
                getMachine().addOperationAtXY(pattern, p.x, p.y);
                changeDone();
                repaint();
            }
        }
    }

    public void eventCreateLink(GElement source, String sourceAnchorKey, GElement target, String targetAnchorKey, int shape, Point p) {
        getMachine().createLink((GElementTMOperation)source, sourceAnchorKey, (GElementTMOperation)target, targetAnchorKey, shape, p);
        changeDone();
    }

    public boolean eventCanCreateLink() {
        int tool = designToolTM.getSelectedTool();
        if(tool == DesignToolsTM.TOOL_LINK) {
            designToolTM.consumeSelectedState();
            return true;
        } else
            return false;
    }

    public void eventEditElement(GElement e) {
        if(e instanceof GLink) {
            if(getMachine().editLink((GLink)e)) {
                changeDone();
                repaint();
            }
        } else if(e instanceof GElementTMOperation) {
            editOperation((GElementTMOperation)e);
        }
    }

}