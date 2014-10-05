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
import edu.usfca.vas.machine.tm.exception.TMTapeException;
import edu.usfca.xj.foundation.XJXMLSerializable;

public class TMTape implements XJXMLSerializable {

    protected StringBuffer tape = new StringBuffer();
    protected int position = 0;

    public TMTape() {

    }

    public void setTape(StringBuffer tape) {
        this.tape = tape;
    }

    public StringBuffer getTape() {
        return tape;
    }

    public void clear() {
        setContent("");
        try {
            setPosition(0);
        } catch (TMTapeException e) {
            System.err.println("Cannot clear the tape: "+e);
        }
    }

    public void setContent(String content) {
        tape = new StringBuffer(content);
    }

    public String getContent() {
        return tape.toString();
    }

    public void writeSymbolAtPosition(String symbol, int position) throws TMTapeException {
        int old_position = getPosition();
        setPosition(position);
        writeSymbol(symbol);
        setPosition(old_position);
    }

    public void writeSymbol(String symbol) {
        tape.replace(getPosition(), getPosition()+1, symbol);
    }

    public String readSymbol() {
        if(position>=tape.length())
            return "";
        else
            return tape.substring(getPosition(), getPosition()+1);
    }

    public void setPosition(int position) throws TMTapeException {
        if(position<0)
            throw new TMTapeException(Localized.getString("tmTapeHeadLeft"));

        this.position = position;
        optimize();
    }

    public int getPosition() {
        return position;
    }

    public void moveRight() throws TMTapeException {
        setPosition(getPosition()+1);
    }

    public void moveLeft() throws TMTapeException {
        setPosition(getPosition()-1);
    }

    public void optimize() {
        while(tape.length()<=position) {
            tape.append(TMMachine.SYMBOL_BLANK);
        }
        while(tape.length()-1>position && tape.charAt(tape.length()-1) == TMMachine.SYMBOL_BLANK.charAt(0)) {
            tape.deleteCharAt(tape.length()-1);
        }
    }

    public String toString() {
        return tape.substring(0, position)+"["+tape.substring(position, position+1)+"]"+tape.substring(position+1);
    }

    public static boolean isContentEquals(String s1, String s2) {
        if(s1.equals(s2))
            return true;

        if(trim(s1).equals(trim(s2)))
            return true;

        return false;
    }

    private static String trim(String s) {
        StringBuffer sb = new StringBuffer(s);
        for(int i=sb.length()-1; i>=0; i--) {
            if(sb.substring(i, i+1).equals(TMMachine.SYMBOL_BLANK))
                sb.deleteCharAt(i);
        }
        return sb.toString();
    }
}
