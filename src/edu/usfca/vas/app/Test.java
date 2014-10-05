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

package edu.usfca.vas.app;

import edu.usfca.vas.machine.fa.FAMachine;
import edu.usfca.vas.machine.fa.FAState;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Test {

    public FAMachine machine = new FAMachine();
    public FAMachine result = null;
    public String symbols = null;
    public List states = null;

    public Test(int nbSymbols, int nbStates, int minTransitions, int maxTransitions, int statesLimit, int nbSeconds) {

        symbols = generateSymbols(nbSymbols);
        states = generateStates(nbStates);

        System.out.println("** Simulation started **");
        System.out.println("  - states = "+states);
        System.out.println("  - symbols = "+symbols);
        System.out.println("  - transitions = "+minTransitions+" to "+maxTransitions);
        System.out.println("  - limit to reach = "+statesLimit+" states");
        System.out.println("  - duration = "+nbSeconds+" s");

        machine.setType(FAMachine.MACHINE_TYPE_NFA);
        machine.setSymbolsString(symbols);

        for(int s=0; s<states.size(); s++) {
            machine.addState(new FAState((String)states.get(s), s==0, false));
        }

        long startT = System.currentTimeMillis();
        while((System.currentTimeMillis()-startT)<nbSeconds*1000) {
            for(int nb_transitions=minTransitions; nb_transitions<maxTransitions; nb_transitions++) {
                for(int i=0; i<5000; i++) {
                    machine.getTransitions().clear();
                    for(int t=0; t<nb_transitions; t++) {
                        String rs1 = randomState();
                        String rs2 = randomState();
                        String rsymbol = randomSymbol();

                        if(machine.containsTransition(rs1, rsymbol, rs2))
                            t--;
                        else
                            machine.addTransitionPattern(rs1, rsymbol, rs2);
                    }

                    result = machine.convertNFA2DFA();

                    int nb_states = result.getStateList().size();
                    if(nb_states>=statesLimit) {
                        String r = "States = "+nb_states+"\n";
                        r += "NFA Transitions = "+machine.getTransitions().toString()+"\n";
                        r += "DFA States = "+result.getStateNames().toString()+"\n";
                        r += "DFA Transitions = "+result.getTransitions().toString()+"\n";
                        System.out.println(r);
                        writeToFile(r);
                        return;
                    }
                }
            }
        }
    }

    public void writeToFile(String s) {
        String file = "result-"+new SimpleDateFormat("yyyy-dd-MM").format(new Date())+".txt";
        FileWriter fw = null;
        try {
            fw = new FileWriter(file, true);
            fw.write(s);
            fw.close();
        } catch(IOException e) {
            System.out.println("Cannot write result to log file \""+file+"\" ("+e+")");
        } finally {
            if(fw != null) {
                try {
                    fw.close();
                } catch(IOException e) {
                    System.out.println("Cannot close the result log file \""+file+"\" ("+e+")");
                }
            }
        }
    }

    public String generateSymbols(int count) {
        String s ="";
        for(int i=0; i<count; i++) {
            s += (char)('a'+i);
        }
        return s;
    }

    public List generateStates(int count) {
        List states = new ArrayList();
        for(int i=0; i<count; i++) {
            states.add(String.valueOf(i));
        }
        return states;
    }

    public String randomSymbol() {
        char s = symbols.charAt((int) (symbols.length()*Math.random()));
        return String.valueOf(s);
    }

    public String randomState() {
        return (String)states.get((int) (states.size()*Math.random()));
    }
}
