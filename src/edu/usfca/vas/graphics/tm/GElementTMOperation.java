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

import edu.usfca.vas.graphics.IconManager;
import edu.usfca.vas.machine.Tool;
import edu.usfca.vas.machine.tm.TMMachine;
import edu.usfca.vas.machine.tm.TMOperation;
import edu.usfca.xj.appkit.gview.base.Anchor2D;
import edu.usfca.xj.appkit.gview.base.Vector2D;
import edu.usfca.xj.appkit.gview.object.GElementRect;
import edu.usfca.xj.appkit.gview.shape.SArrow;
import edu.usfca.xj.appkit.gview.shape.SLabel;
import edu.usfca.xj.foundation.XJXMLSerializable;

import java.awt.*;
import java.util.Iterator;
import java.util.Set;

public class GElementTMOperation extends GElementRect implements XJXMLSerializable {

    protected TMOperation operation = null;

    protected transient SArrow startArrow = new SArrow();
    protected transient Vector2D startArrowDirection = new Vector2D(-1, 0);

    public static GElementTMOperation parse(StringBuffer pattern) {
        TMOperation op = new TMOperation();
        GElementTMOperation gop = new GElementTMOperation();
        gop.setOperation(op);
        return parse(pattern, gop)?gop:null;
    }

    public static boolean parse(StringBuffer pattern, GElementTMOperation gop) {
        TMOperation op = gop.getOperation();
        String s = pattern.toString();

        if(s.startsWith(TMOperation.OPS_LEFT_UNTIL)) {
            pattern.delete(0, 2);
            gop.setLabel(TMOperation.OPS_LEFT_UNTIL+pattern.charAt(0));
            op.setOperation(TMOperation.OP_LEFT_UNTIL);
            op.setParameter(pattern.substring(0, 1));
            pattern.deleteCharAt(0);
        } else if(s.startsWith(TMOperation.OPS_LEFT_UNTIL_NOT)) {
            pattern.delete(0, 2);
            gop.setLabel(TMOperation.OPS_LEFT_UNTIL_NOT+pattern.charAt(0));
            op.setOperation(TMOperation.OP_LEFT_UNTIL_NOT);
            op.setParameter(pattern.substring(0, 1));
            pattern.deleteCharAt(0);
        } else if(s.startsWith(TMOperation.OPS_LEFT)) {
            pattern.deleteCharAt(0);
            gop.setLabel(TMOperation.OPS_LEFT);
            op.setOperation(TMOperation.OP_LEFT);
        } else if(s.startsWith(TMOperation.OPS_RIGHT_UNTIL)) {
            pattern.delete(0, 2);
            gop.setLabel(TMOperation.OPS_RIGHT_UNTIL+pattern.charAt(0));
            op.setOperation(TMOperation.OP_RIGHT_UNTIL);
            op.setParameter(pattern.substring(0, 1));
            pattern.deleteCharAt(0);
        } else if(s.startsWith(TMOperation.OPS_RIGHT_UNTIL_NOT)) {
            pattern.delete(0, 2);
            gop.setLabel(TMOperation.OPS_RIGHT_UNTIL_NOT+pattern.charAt(0));
            op.setOperation(TMOperation.OP_RIGHT_UNTIL_NOT);
            op.setParameter(pattern.substring(0, 1));
            pattern.deleteCharAt(0);
        } else if(s.startsWith(TMOperation.OPS_RIGHT)) {
            pattern.deleteCharAt(0);
            gop.setLabel(TMOperation.OPS_RIGHT);
            op.setOperation(TMOperation.OP_RIGHT);
        } else if(s.startsWith(TMOperation.OPS_OUTPUT)) {
            gop.setLabel(TMOperation.OPS_OUTPUT);
            op.setOperation(TMOperation.OP_OUTPUT);
            pattern.deleteCharAt(0);
        } else if(s.startsWith(TMOperation.OPS_YES)) {
            gop.setLabel(TMOperation.OPS_YES);
            op.setOperation(TMOperation.OP_YES);
            pattern.deleteCharAt(0);
        } else if(s.startsWith(TMOperation.OPS_NO)) {
            gop.setLabel(TMOperation.OPS_NO);
            op.setOperation(TMOperation.OP_NO);
            pattern.deleteCharAt(0);
        } else if(s.startsWith(TMOperation.OPS_CALL)) {
            pattern.deleteCharAt(0); // consume M
            pattern.deleteCharAt(0); // consume [
            String machineName = pattern.substring(0, pattern.indexOf("]"));
            pattern.delete(0, pattern.indexOf("]")+1);

            op.setOperation(TMOperation.OP_CALL);
            op.setParameter(machineName);
            gop.setLabel(TMOperation.OP_CALL+"["+op.getParameter()+"]");
        } else {
            op.setOperation(TMOperation.OP_WRITE);
            if(s.startsWith(TMMachine.SYMBOL_VAR)) {
                op.setParameter(pattern.substring(0, 2));
                pattern.delete(0, 2);
            } else {
                op.setParameter(pattern.substring(0, 1));
                pattern.deleteCharAt(0);
            }
            gop.setLabel(op.getParameter());
        }

        return true;
    }

    public GElementTMOperation() {
        setDraggable(true);
    }

    public GElementTMOperation(TMOperation op, double x, double y) {
        setOperation(op);
        setPosition(x, y);
        setDraggable(true);
    }

    public void setOperation(TMOperation op) {
        this.operation = op;
    }

    public TMOperation getOperation() {
        return operation;
    }

    public void addLinkToOperation(GElementTMOperation op, String pattern) {
        Set symbols = Tool.symbolsInPattern(pattern);
        Set variables = Tool.variablesInPattern(pattern);
        if(symbols.size() == 0) {
            operation.addLinkToOperation(op.operation, "", variables);
        } else {
            Iterator iterator = symbols.iterator();
            while(iterator.hasNext()) {
                String symbol = (String)iterator.next();
                operation.addLinkToOperation(op.operation, symbol, variables);
            }
        }
    }

    public void removeLinkToOperation(GElementTMOperation op, String pattern) {
        Set symbols = Tool.symbolsInPattern(pattern);
        if(symbols.size() == 0) {
            operation.removeLinkToOperation(op.operation, "");
        } else {
            Iterator iterator = symbols.iterator();
            while(iterator.hasNext()) {
                String symbol = (String)iterator.next();
                operation.removeLinkToOperation(op.operation, symbol);
            }
        }
    }

    public void machineDidRename(String oldName, String newName) {
        if(getOperation().getOperation() == TMOperation.OP_CALL
            && getOperation().getParameter().equals(oldName))
        {
            setLabel(TMOperation.OPS_CALL+"["+newName+"]");
            getOperation().setParameter(newName);
        }
    }

    public void setStart(boolean start) {
        operation.setStart(start);
    }

    public boolean isStart() {
        return operation.isStart();
    }

    public void toggleStart() {
        operation.setStart(!operation.isStart());
    }

    public void toggleBreakpoint() {
        operation.setBreakpoint(!operation.isBreakPoint());
    }

    public boolean acceptIncomingLink() {
        return true;
    }

    public boolean acceptOutgoingLink() {
        return true;
    }

    public void updateAnchors() {
        setAnchor(ANCHOR_TOP, position.add(new Vector2D(0, -height*0.5)), Anchor2D.DIRECTION_TOP);
        setAnchor(ANCHOR_BOTTOM, position.add(new Vector2D(0, height*0.5)), Anchor2D.DIRECTION_BOTTOM);
        setAnchor(ANCHOR_LEFT, position.add(new Vector2D(-width*0.5, 0)), Anchor2D.DIRECTION_LEFT);
        setAnchor(ANCHOR_RIGHT, position.add(new Vector2D(width*0.5, 0)), Anchor2D.DIRECTION_RIGHT);
    }

    public void drawShape(Graphics2D g) {
        super.drawShape(g);

        if(operation.isStart()) {
            startArrow.setAnchor(getPositionX()-getWidth()*0.5, getPositionY());
            startArrow.setDirection(startArrowDirection);
            startArrow.setLength(20);
            startArrow.setAngle(30);
            startArrow.draw(g);
        }

        Rectangle r = getFrame().rectangle();

        switch(operation.getOperation()) {
            case TMOperation.OP_LEFT:
                IconManager.drawCentered(g, IconManager.getImage(IconManager.ICON_LEFT), r);
                break;
            case TMOperation.OP_LEFT_UNTIL:
                IconManager.drawNorth(g, IconManager.getImage(IconManager.ICON_LEFT_UNTIL), r);
                break;
            case TMOperation.OP_LEFT_UNTIL_NOT:
                IconManager.drawNorth(g, IconManager.getImage(IconManager.ICON_LEFT_UNTIL_NOT), r);
                break;
            case TMOperation.OP_RIGHT:
                IconManager.drawCentered(g, IconManager.getImage(IconManager.ICON_RIGHT), r);
                break;
            case TMOperation.OP_RIGHT_UNTIL:
                IconManager.drawNorth(g, IconManager.getImage(IconManager.ICON_RIGHT_UNTIL), r);
                break;
            case TMOperation.OP_RIGHT_UNTIL_NOT:
                IconManager.drawNorth(g, IconManager.getImage(IconManager.ICON_RIGHT_UNTIL_NOT), r);
                break;
            case TMOperation.OP_WRITE:
                IconManager.drawUpperLeftCorner(g, IconManager.getImage(IconManager.ICON_WRITE), r);
                break;
            case TMOperation.OP_YES:
                IconManager.drawCentered(g, IconManager.getImage(IconManager.ICON_YES), r);
                break;
            case TMOperation.OP_NO:
                IconManager.drawCentered(g, IconManager.getImage(IconManager.ICON_NO), r);
                break;
            case TMOperation.OP_OUTPUT:
                IconManager.drawCentered(g, IconManager.getImage(IconManager.ICON_OUTPUT), r);
                break;
            case TMOperation.OP_CALL:
                IconManager.drawCentered(g, IconManager.getImage(IconManager.ICON_CALL), r);
                break;
        }

        if(operation.isBreakPoint()) {
            IconManager.drawBottomLeftCorner(g, IconManager.getImage(IconManager.ICON_BREAKPOINT), r);
        }
    }

    public void drawLabel(Graphics2D g) {
        int op = operation.getOperation();
        switch(op) {
            case TMOperation.OP_LEFT:
            case TMOperation.OP_RIGHT:
            case TMOperation.OP_NONE:
            case TMOperation.OP_NO:
            case TMOperation.OP_YES:
            case TMOperation.OP_OUTPUT:
                return;
        }

        Rectangle r = getFrame().rectangle();

        if(op == TMOperation.OP_CALL)
            SLabel.drawCenteredString(operation.getParameter(), getPositionX(), r.y+r.height+8, g);
        else
            SLabel.drawCenteredString(operation.getParameter(), getPositionX(), getPositionY()+8, g);
    }

}
