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

import edu.usfca.vas.app.Localized;
import edu.usfca.xj.foundation.XJXMLSerializable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TMMachine implements XJXMLSerializable {

    public static final String SYMBOL_BLANK = "#";
    public static final String SYMBOL_NOT = "!";
    public static final String SYMBOL_VAR = "@";

    protected Set operations = new HashSet();
    protected String name = "";

    public TMMachine() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setOperations(Set operations) {
        this.operations = operations;
    }

    public Set getOperations() {
        return operations;
    }

    public void addOperation(TMOperation op) {
        operations.add(op);
    }

    public void removeOperation(TMOperation operation) {
        Iterator iterator =  operations.iterator();
        while(iterator.hasNext()) {
            TMOperation op = (TMOperation)iterator.next();
            op.removeLinkToOperation(operation);
        }
        operations.remove(operation);
    }

    public TMOperation getStartOperation() {
        Iterator iterator =  operations.iterator();
        while(iterator.hasNext()) {
            TMOperation op = (TMOperation)iterator.next();
            if(op.isStart())
                return op;
        }
        return null;
    }

    public void clear() {
        operations.clear();
    }

    public String dumpTransitions() {
        String s = Localized.getString("machine")+": "+getName()+"\n";

        int count = 0;
        Iterator iterator =  operations.iterator();
        while(iterator.hasNext()) {
            TMOperation op = (TMOperation)iterator.next();
            Iterator linkIterator = op.getLinks().iterator();
            while(linkIterator.hasNext()) {
                TMOperationLink link = (TMOperationLink)linkIterator.next();
                s += "\n("+op.getOperationName()+", "+link.getSymbol()+") -> "+link.getTargetOperation().getOperationName();
                count++;
            }
        }

        s += "\n\n"+Localized.getString("total")+" = "+count;

        return s;
    }

}

