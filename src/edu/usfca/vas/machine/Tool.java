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

package edu.usfca.vas.machine;

import edu.usfca.vas.machine.tm.TMMachine;

import java.util.*;

public class Tool {

    public static Set symbolsInPattern(String pattern) {
        Set symbols = new HashSet();
        Iterator iterator = tokensInPattern(pattern).iterator();
        while(iterator.hasNext()) {
            String token = (String)iterator.next();
            if(!token.startsWith(TMMachine.SYMBOL_VAR))
                symbols.add(token);
        }
        return symbols;
    }

    public static Set variablesInPattern(String pattern) {
        Set variables = new HashSet();
        Iterator iterator = tokensInPattern(pattern).iterator();
        while(iterator.hasNext()) {
            String token = (String)iterator.next();
            if(token.startsWith(TMMachine.SYMBOL_VAR))
                variables.add(token);
        }
        return variables;
    }

    public static List tokensInPattern(String pattern) {
        List tokens = new ArrayList();

        if(pattern == null || pattern.length() == 0)
            return tokens;

        if(pattern.indexOf(",")>0) {
            // List with "," as separator
            String[] array = pattern.split(",");
            for(int i=0; i<array.length; i++) {
                String symb = array[i].trim();
                if(symb.length()>0)
                    tokens.add(symb);
            }
        } else {
            // List with no separator
            int i = 0;
            while(i<pattern.length()) {
                String s = pattern.substring(i, i+1);
                if(s.startsWith(TMMachine.SYMBOL_NOT) || s.startsWith(TMMachine.SYMBOL_VAR)) {
                    tokens.add(pattern.substring(i, i+2));
                    i++;
                } else
                    tokens.add(s);
                i++;
            }
        }
        return tokens;
    }

    public static String addSymbolToPattern(String pattern, String symbol) {
        if(pattern.indexOf(",")>0 || pattern.length() < 2)
            pattern += ", "+symbol;
        else
            pattern += symbol;
        return pattern;
    }

}
