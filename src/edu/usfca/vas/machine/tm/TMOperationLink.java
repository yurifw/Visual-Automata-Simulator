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

import edu.usfca.xj.foundation.XJXMLSerializable;

import java.util.Set;


public class TMOperationLink implements XJXMLSerializable {

    protected String symbol = "";
    protected TMOperation sourceOperation = null;
    protected TMOperation targetOperation = null;
    protected Set variables = null;

    public TMOperationLink() {

    }

    public TMOperationLink(TMOperation source, TMOperation target, String symbol, Set variables) {
        this.sourceOperation = source;
        this.targetOperation = target;
        this.symbol = symbol;
        this.variables = variables;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public TMOperation getSourceOperation() {
        return sourceOperation;
    }

    public void setSourceOperation(TMOperation sourceOperation) {
        this.sourceOperation = sourceOperation;
    }

    public TMOperation getTargetOperation() {
        return targetOperation;
    }

    public void setTargetOperation(TMOperation targetOperation) {
        this.targetOperation = targetOperation;
    }

    public Set getVariables() {
        return variables;
    }

    public void setVariables(Set variables) {
        this.variables = variables;
    }
}
