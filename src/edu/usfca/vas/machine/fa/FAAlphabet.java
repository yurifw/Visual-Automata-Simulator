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

package edu.usfca.vas.machine.fa;

import edu.usfca.vas.app.Preferences;
import edu.usfca.xj.foundation.XJXMLSerializable;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class FAAlphabet implements XJXMLSerializable {

    protected Set symbols = new LinkedHashSet();
    protected transient FAMachine machine = null;

    public void setMachine(FAMachine machine) {
        this.machine = machine;
    }

    public void setSymbolsString(String s) {
        symbols.clear();
        for(int c=0; c<s.length(); c++)
            symbols.add(String.valueOf(s.charAt(c)));
    }

    public String getSymbolsString() {        
        String s = "";
        Iterator iterator = symbols.iterator();
        while(iterator.hasNext()) {
            s += iterator.next();
        }
        return s;
    }

    public void addSymbol(String s) {
        symbols.add(s);
    }

    public void setSymbols(Set symbols) {
        this.symbols = symbols;
    }

    public Set getSymbols() {
        if(machine != null && machine.getType() == FAMachine.MACHINE_TYPE_NFA)
            symbols.add(Preferences.getEpsilonTransition());

        return symbols;
    }

}
