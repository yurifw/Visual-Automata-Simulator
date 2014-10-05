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

import edu.usfca.vas.app.Preferences;
import edu.usfca.vas.graphics.tm.GElementTMMachine;
import edu.usfca.vas.machine.tm.TMMachine;
import edu.usfca.xj.foundation.XJXMLSerializable;

import java.awt.*;

public class DataWrapperTM extends DataWrapperAbstract implements XJXMLSerializable {

    protected TMMachine machine = null;
    protected GElementTMMachine graphicMachine = null;
    protected String string = "";

    protected Dimension size = null;

    public DataWrapperTM() {
        machine = new TMMachine();
        graphicMachine = new GElementTMMachine(machine);
        size = new Dimension(Preferences.getDefaultGraphicWidth(), Preferences.getDefaultGraphicHeight());
    }

    public DataWrapperTM(TMMachine taMachine, GElementTMMachine gtmMachine, String string) {
        this.machine = taMachine;
        this.graphicMachine = gtmMachine;
        this.string = string;
    }

    public void setName(String name) {
        machine.setName(name);
    }

    public String getName() {
        return machine.getName();
    }
    
    public void setString(String s) {
        string = s;
    }

    public String getString() {
        return string;
    }

    public void setSymbolsString(String s) {
    }

    public String getSymbolsString() {
        return "";
    }

    public void setMachine(TMMachine machine) {
        this.machine = machine;
    }

    public TMMachine getTMMachine() {
        return machine;
    }

    public void setGraphicMachine(GElementTMMachine machine) {
        this.graphicMachine = machine;
    }

    public GElementTMMachine getGraphicMachine() {
        return graphicMachine;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

    public Dimension getSize() {
        return size;
    }

}
