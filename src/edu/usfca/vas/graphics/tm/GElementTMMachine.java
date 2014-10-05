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
import edu.usfca.vas.machine.tm.TMMachine;
import edu.usfca.vas.machine.tm.TMOperation;
import edu.usfca.xj.appkit.gview.GView;
import edu.usfca.xj.appkit.gview.object.GElement;
import edu.usfca.xj.appkit.gview.object.GLink;
import edu.usfca.xj.foundation.XJXMLSerializable;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.ListIterator;

public class GElementTMMachine extends GElement implements XJXMLSerializable {

    private TMMachine machine = null;

    public GElementTMMachine() {
        
    }

    public GElementTMMachine(TMMachine machine) {
        setMachine(machine);
    }

    public void setMachine(TMMachine machine) {
        this.machine = machine;
    }

    public TMMachine getMachine() {
        return machine;
    }

    public void addOperationAtXY(String pattern, double x, double y) {
        StringBuffer sbp = new StringBuffer(pattern);

        int count = 0;
        GElementTMOperation lastOp = null;
        while(sbp.length()>0) {
            double positionX = x+count*(2*GElementTMOperation.DEFAULT_WIDTH+50);

            GElementTMOperation gop = GElementTMOperation.parse(sbp);
            if(gop == null)
                break;

            gop.setPosition(positionX, y);
            getMachine().addOperation(gop.getOperation());
            addElement(gop);

            if(lastOp != null) {
                lastOp.addLinkToOperation(gop, "");
                addElement(new GLink(lastOp, GElementTMOperation.ANCHOR_RIGHT,
                                        gop, GElementTMOperation.ANCHOR_LEFT,
                                        GLink.SHAPE_ELBOW, "", 20));
            }
            lastOp = gop;

            count++;
        }
    }

    public GElementTMOperation getOperation1(GLink link) {
        return (GElementTMOperation)link.source;
    }

    public GElementTMOperation getOperation2(GLink link) {
        return (GElementTMOperation)link.target;
    }

    public boolean createLink(GElementTMOperation source, String sourceAnchorKey, GElementTMOperation target, String targetAnchorKey, int shape, Point mouse) {
        String pattern = (String)JOptionPane.showInputDialog(null, Localized.getString("tmNewLinkMessage"), Localized.getString("tmNewLinkTitle"),
                                    JOptionPane.QUESTION_MESSAGE, null, null, null);
        if(pattern != null) {
            source.addLinkToOperation(target, pattern);
            addElement(new GLink(source, sourceAnchorKey, target, targetAnchorKey, shape, pattern, mouse, GView.DEFAULT_LINK_FLATENESS));
        }

        return pattern != null;
    }

    public boolean editLink(GLink link) {
        String pattern = (String)JOptionPane.showInputDialog(null, Localized.getString("tmEditLinkMessage"), Localized.getString("tmEditLinkTitle"),
                                    JOptionPane.QUESTION_MESSAGE, null, null, link.pattern);
        if(pattern != null) {
            removeLink(link);
            link.pattern = pattern;
            getOperation1(link).addLinkToOperation(getOperation2(link), pattern);
            addElement(link);
        }

        return pattern != null;
    }

    public void removeLink(GLink link) {
        removeElement(link);
        getOperation1(link).removeLinkToOperation(getOperation2(link), link.pattern);
    }

    public GElementTMOperation getGraphicOperation(TMOperation operation) {
        ListIterator iterator = elements.listIterator();
        while(iterator.hasNext()) {
            GElement element = (GElement)iterator.next();
            if(element instanceof GElementTMOperation) {
                GElementTMOperation op = (GElementTMOperation)element;
                if(op.getOperation() == operation)
                    return op;
            }
        }
        return null;
    }

    public GLink getGraphicOperationLink(GElementTMOperation o1, GElementTMOperation o2) {
        Iterator iterator = elements.iterator();
        while(iterator.hasNext()) {
            GElement element = (GElement)iterator.next();
            if(element instanceof GLink) {
                GLink link = (GLink)element;
                if(link.source == o1 && link.target == o2) {
                    return link;
                }
            }
        }
        return null;
    }

    public void removeOperation(GElementTMOperation op) {
        getMachine().removeOperation(op.getOperation());
        removeElement(op);
        // Remove any other link which is using the operator op
        Iterator iterator = elements.iterator();
        while(iterator.hasNext()) {
            GElement element = (GElement)iterator.next();
            if(element instanceof GLink) {
                GLink link = (GLink)element;
                if(link.source == op || link.target == op) {
                    removeElement(link);
                    iterator = elements.listIterator();
                }
            }
        }
    }

    public void machineDidRename(String oldName, String newName) {
        Iterator iterator = elements.iterator();
        while(iterator.hasNext()) {
            GElement element = (GElement)iterator.next();
            if(element instanceof GElementTMOperation) {
                GElementTMOperation op = (GElementTMOperation)element;
                op.machineDidRename(oldName, newName);
            }
        }
    }

    public void toggleStartOperation(GElementTMOperation op) {
        if(op.isStart() == false) {
            Iterator iterator = elements.iterator();
            while(iterator.hasNext()) {
                GElement element = (GElement)iterator.next();
                if(element instanceof GElementTMOperation) {
                    ((GElementTMOperation)element).setStart(false);
                }
            }
        }
        op.toggleStart();
    }

    public void clear() {
        machine.clear();
        elements.clear();
    }

}
