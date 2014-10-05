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

package edu.usfca.vas.machine.tm;

import edu.usfca.vas.app.Preferences;
import edu.usfca.vas.graphics.device.OutputDevice;
import edu.usfca.vas.machine.tm.exception.TMException;
import edu.usfca.xj.foundation.XJXMLSerializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TMOperation implements XJXMLSerializable {

    public static final int OP_NONE = 0;
    public static final int OP_LEFT = 1;
    public static final int OP_RIGHT = 2;
    public static final int OP_WRITE = 3;
    public static final int OP_OUTPUT = 4;
    public static final int OP_CALL = 5;
    public static final int OP_YES = 6;
    public static final int OP_NO = 7;
    public static final int OP_LEFT_UNTIL = 8;
    public static final int OP_LEFT_UNTIL_NOT = 9;
    public static final int OP_RIGHT_UNTIL = 10;
    public static final int OP_RIGHT_UNTIL_NOT = 11;

    public static final String OPS_LEFT = "L";
    public static final String OPS_LEFT_UNTIL = "L=";
    public static final String OPS_LEFT_UNTIL_NOT = "L!";
    public static final String OPS_RIGHT = "R";
    public static final String OPS_RIGHT_UNTIL = "R=";
    public static final String OPS_RIGHT_UNTIL_NOT = "R!";
    public static final String OPS_CALL = "C";
    public static final String OPS_YES = "Y";
    public static final String OPS_NO = "N";
    public static final String OPS_OUTPUT = "O";
    public static final String OPS_WRITE = "W";

    protected List links = new ArrayList();

    protected int operation = OP_NONE;
    protected String parameter = null;
    protected boolean start = false;
    protected boolean breakpoint = false;

    protected boolean haltedOnInfinite = false;

    public TMOperation() {
    }

    public void setOperation(int op) {
        this.operation = op;
    }

    public int getOperation() {
        return operation;
    }

    public String getOperationName() {
        String name = "";

        switch(getOperation()) {
            case OP_LEFT:
                name = OPS_LEFT;
                break;
            case OP_LEFT_UNTIL:
                name = OPS_LEFT_UNTIL;
                break;
            case OP_LEFT_UNTIL_NOT:
                name = OPS_LEFT_UNTIL_NOT;
                break;
            case OP_RIGHT:
                name = OPS_RIGHT;
                break;
            case OP_RIGHT_UNTIL:
                name = OPS_RIGHT_UNTIL;
                break;
            case OP_RIGHT_UNTIL_NOT:
                name = OPS_RIGHT_UNTIL_NOT;
                break;
            case OP_WRITE:
                name = OPS_WRITE;
                break;
            case OP_OUTPUT:
                name = OPS_OUTPUT;
                break;
            case OP_NO:
                name = OPS_NO;
                break;
            case OP_YES:
                name = OPS_YES;
                break;
            case OP_CALL:
                name = OPS_CALL;
                break;
        }
        return name;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public boolean isStart() {
        return start;
    }

    public void setBreakpoint(boolean flag) {
        this.breakpoint = flag;
    }

    public boolean isBreakPoint() {
        return breakpoint;
    }

    public void setHaltedOnInfinite(boolean flag) {
        this.haltedOnInfinite = flag;
    }

    public boolean isHaltedOnInfinite() {
        return haltedOnInfinite;
    }

    public void addLinkToOperation(TMOperation op, String symbol, Set variables) {
        links.add(new TMOperationLink(this, op, symbol, variables));
    }

    public void setLinks(List links) {
        this.links = links;
    }

    public List getLinks() {
        return links;
    }

    public TMOperationLink getLinkToOperation(TMOperation op, String symbol) {
        Iterator iterator = links.iterator();
        while(iterator.hasNext()) {
            TMOperationLink link = (TMOperationLink)iterator.next();
            if(link.getTargetOperation().equals(op) && link.getSymbol().equals(symbol))
                return link;
        }
        return null;
    }

    public void removeLinkToOperation(TMOperation op) {
        Iterator iterator = links.iterator();
        while(iterator.hasNext()) {
            TMOperationLink link = (TMOperationLink)iterator.next();
            if(link.getTargetOperation().equals(op)) {
                links.remove(link);
                iterator = links.iterator();
            }
        }
    }

    public void removeLinkToOperation(TMOperation op, String symbol) {
        Iterator iterator = links.iterator();
        while(iterator.hasNext()) {
            TMOperationLink link = (TMOperationLink)iterator.next();
            if(link.getTargetOperation().equals(op) && link.getSymbol().equals(symbol)) {
                links.remove(link);
                iterator = links.iterator();
                break;
            }
        }
    }

    /** Return the next operation corresponding to the specified symbol. First look at symbol
     * link and then at epsilon (null symbol) link.
     */

    public TMOperationLink getFirstCorrespondingLink(String symbol) {
        TMOperationLink link = null;

        if(symbol != null && symbol.length() > 0)
            link = getLinkToOperationForSymbol(symbol);

        if(link == null)
            link = getFirstEpsilonLink();

        return link;
    }

    /** Return the first epsilon link (null symbol) found
     */

    public TMOperationLink getFirstEpsilonLink() {
        Iterator iterator = links.iterator();
        while(iterator.hasNext()) {
            TMOperationLink link = (TMOperationLink)iterator.next();
            String linkSymbol = link.getSymbol();
            if(linkSymbol == null || linkSymbol.length() == 0)
                return link;
        }
        return null;
    }

    public TMOperationLink getLinkToOperationForSymbol(String symbol) {
        Iterator iterator = links.iterator();
        while(iterator.hasNext()) {
            TMOperationLink link = (TMOperationLink)iterator.next();
            String linkSymbol = link.getSymbol();
            if(linkSymbol == null || linkSymbol.length() == 0)
                continue;

            if(linkSymbol.startsWith(TMMachine.SYMBOL_NOT)) {
                if(linkSymbol.substring(1).equals(symbol) == false)
                    return link;
            } else if(linkSymbol.equals(symbol))
                return link;
        }
        return null;
    }

    public boolean execute(OutputDevice od, TMInterpretor interpretor, TMMachine machine, TMTape tape)
                throws TMException
    {
        boolean stop = false;

        int limit = Preferences.getInfiniteTMSteps();

        //System.out.println(getOperationName()+(parameter!=null?parameter:""));

        switch(getOperation()) {
            case OP_LEFT:
                tape.moveLeft();
                break;
            case OP_LEFT_UNTIL:
                tape.moveLeft();
                while(!tape.readSymbol().equals(parameter) && --limit>0) {
                    tape.moveLeft();
                    interpretor.stepCount++;
                }
                break;
            case OP_LEFT_UNTIL_NOT:
                tape.moveLeft();
                while(tape.readSymbol().equals(parameter) && --limit>0) {
                    tape.moveLeft();
                    interpretor.stepCount++;
                }
                break;
            case OP_RIGHT:
                tape.moveRight();
                break;
            case OP_RIGHT_UNTIL:
                tape.moveRight();
                while(!tape.readSymbol().equals(parameter) && --limit>0) {
                    tape.moveRight();
                    interpretor.stepCount++;
                }
                break;
            case OP_RIGHT_UNTIL_NOT:
                tape.moveRight();
                while(tape.readSymbol().equals(parameter) && --limit>0) {
                    tape.moveRight();
                    interpretor.stepCount++;
                }
                break;
            case OP_WRITE:
                if(parameter.startsWith(TMMachine.SYMBOL_VAR)) {
                    tape.writeSymbol(interpretor.recallVariable(machine, parameter.substring(1)));
                } else
                    tape.writeSymbol(parameter);
                break;
            case OP_OUTPUT:
                od.println("OUTPUT="+tape.getContent());
                break;
            case OP_NO:
                od.println("NO");
                stop = true;
                break;
            case OP_YES:
                od.println("YES");
                stop = true;
                break;
            case OP_CALL:
                interpretor.call(parameter);
                break;
        }

        if(limit <= 0) {
            setHaltedOnInfinite(true);
            stop = true;
        }

        return !stop;
    }

}
