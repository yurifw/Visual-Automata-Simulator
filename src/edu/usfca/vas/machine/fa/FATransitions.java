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

import edu.usfca.vas.app.Localized;
import edu.usfca.vas.app.Preferences;
import edu.usfca.vas.machine.Tool;
import edu.usfca.xj.foundation.XJXMLSerializable;

import java.util.*;

public class FATransitions implements XJXMLSerializable {

    protected List transitions = new ArrayList();
    protected transient Set lastTransitionSet = new HashSet();

    public static transient String epsilonSymbol = null;

    public FATransitions() {
    }

    public String getEpsilonSymbol() {
        if(epsilonSymbol == null)
            return Preferences.getEpsilonTransition();
        else
            return epsilonSymbol;
    }

    public void addTransitionPattern(String s1, String pattern, String s2) {
        Iterator iterator = Tool.symbolsInPattern(pattern).iterator();
        while(iterator.hasNext())
            addTransition(s1, (String)iterator.next(), s2);
    }

    public void addTransition(String s1, String symbol, String s2) {
        transitions.add(new FATransition(s1, symbol, s2));
    }

    public void addTransition(FATransition transition) {
        transitions.add(transition);
    }

    public boolean containsTransition(String s1, String symbol, String s2) {
        Iterator iterator = transitions.listIterator();
        while(iterator.hasNext()) {
            FATransition w = (FATransition)iterator.next();
            if(w.s1.equals(s1) && w.s2.equals(s2) && w.symbol.equals(symbol)) {
                return true;
            }
        }
        return false;
    }

    public void setTransitions(List transitions) {
        this.transitions = transitions;
    }

    public List getTransitions() {
        return transitions;
    }
    
    public void removeTransition(String s1, String symbol, String s2) {
        Iterator iterator = transitions.listIterator();
        while(iterator.hasNext()) {
            FATransition w = (FATransition)iterator.next();
            if(w.s1.equals(s1) && w.s2.equals(s2) && w.symbol.equals(symbol)) {
                transitions.remove(w);
                iterator = transitions.listIterator();
            }
        }
    }

    public void removeState(String s) {
        Iterator iterator = transitions.listIterator();
        while(iterator.hasNext()) {
            FATransition w = (FATransition)iterator.next();
            if(w.s1.equals(s) || w.s2.equals(s)) {
                transitions.remove(w);
                iterator = transitions.listIterator();
            }
        }
    }

    public void renameState(String oldName, String newName) {
        Iterator iterator = transitions.listIterator();
        while(iterator.hasNext()) {
            FATransition w = (FATransition)iterator.next();
            if(w.s1.equals(oldName))
                w.s1 = newName;
            if(w.s2.equals(oldName))
                w.s2 = newName;            
        }
    }

    public void clear() {
        transitions.clear();
    }

    public int transitionCountForState(String s) {
        int count = 0;
        Iterator iterator = transitions.listIterator();
        while(iterator.hasNext()) {
            FATransition w = (FATransition)iterator.next();
            if(w.s1.equals(s))
                count++;
        }
        return count;
    }

    public String check(int requiredNumberOfTransitions, FAStates states) {
        if(transitions.size() == 0)
            return Localized.getString("faNoTransition");

        Iterator iterator = states.getStateNames().listIterator();
        while(iterator.hasNext()) {
            String s = (String)iterator.next();
            if(transitionCountForState(s) != requiredNumberOfTransitions) {
                Object[] args = { s, new Integer(requiredNumberOfTransitions) };
                return Localized.getFormattedString("faStateNeedTransition", args);
            }
        }

        return null;
    }

    public Set getNextStateSet(String state, String symbol) {
        Set stateSet = new HashSet();

        Iterator iterator = transitions.iterator();
        while(iterator.hasNext()) {
            FATransition w = (FATransition)iterator.next();
            if(w.s1.equals(state) && w.symbol.equals(symbol)) {
                lastTransitionSet.add(w);
                stateSet.add(w.s2);
            }
        }
        return stateSet;
    }

    public void epsilonClosureStateSet(String state, Set stateSet) {
        Iterator iterator = transitions.iterator();
        while(iterator.hasNext()) {
            FATransition w = (FATransition)iterator.next();
            if(w.s1.equals(state) && w.symbol.equals(getEpsilonSymbol())) {
                if(!stateSet.contains(w.s2)) {
                    lastTransitionSet.add(w);
                    stateSet.add(w.s2);
                    epsilonClosureStateSet(w.s2, stateSet);
                }
            }
        }
    }

    public Set getEpsilonClosureStateSet(String state) {
        Set stateSet = new HashSet();
        stateSet.add(state);
        epsilonClosureStateSet(state, stateSet);
        return stateSet;
    }

    public Set getClosureStateSet(String state, String symbol) {
        Set stateSet = new HashSet();
        Iterator iterator = transitions.iterator();
        while(iterator.hasNext()) {
            FATransition w = (FATransition)iterator.next();
            if(w.s1.equals(state) && w.symbol.equals(symbol)) {
                lastTransitionSet.add(w);
                stateSet.add(w.s2);
                epsilonClosureStateSet(w.s2, stateSet);
            }
        }
        // VAS 1.3: removed the following line because the epsilon transition
        // must be followed when jumping to a state along a non-epsilon transition,
        // not when trying to find the set set of possible states for a particular symbol.
        // epsilonClosureStateSet(state, stateSet);
        return stateSet;
    }

    public Set getLastTransitionSet() {
        return lastTransitionSet;
    }

    public String toString() {
        String s = "* transitions *\n";
        Iterator iterator = transitions.listIterator();
        while(iterator.hasNext()) {
            FATransition w = (FATransition)iterator.next();
            s += "<"+w.s1+", "+w.symbol+" -> "+w.s2+">\n";
        }
        return s;
    }

}
