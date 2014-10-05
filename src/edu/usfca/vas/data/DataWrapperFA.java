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

package edu.usfca.vas.data;

import edu.usfca.vas.graphics.fa.GElementFAMachine;
import edu.usfca.vas.machine.fa.FAMachine;
import edu.usfca.xj.foundation.XJXMLSerializable;

import java.awt.*;

public class DataWrapperFA extends DataWrapperAbstract implements XJXMLSerializable {

    protected FAMachine machine = null;
    protected GElementFAMachine graphicMachine = null;
    protected String string = "";

    protected Dimension size = new Dimension(1024, 768);

    public DataWrapperFA() {
        machine = new FAMachine();
        graphicMachine = new GElementFAMachine(machine);
    }

    public DataWrapperFA(FAMachine faMachine, GElementFAMachine gfaMachine, String string) {
        this.machine = faMachine;
        this.graphicMachine = gfaMachine;
        this.string = string;
    }

    public void setMachineType(int type) {
        machine.setType(type);
    }

    public int getMachineType() {
        return machine.getType();
    }

    public void setSymbolsString(String s) {
        machine.setSymbolsString(s);
    }

    public String getSymbolsString() {
        return machine.getSymbolsString();
    }

    public void setString(String s) {
        this.string = s;
    }

    public String getString() {
        return string;
    }

    public void setMachine(FAMachine machine) {
        this.machine = machine;
    }

    public FAMachine getMachine() {
        return machine;
    }

    public void setGraphicMachine(GElementFAMachine machine) {
        this.graphicMachine = machine;
    }

    public GElementFAMachine getGraphicMachine() {
        return graphicMachine;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

    public Dimension getSize() {
        return size;
    }

    public void wrapperDidLoad() {
        graphicMachine.elementDidLoad();
    }

}
