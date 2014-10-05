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
import edu.usfca.xj.foundation.XJXMLSerializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FAStates implements XJXMLSerializable {

    protected List states = new ArrayList();

    public FAStates() {
    }

    public void addState(FAState s) {
        states.add(s);
    }

    public void removeState(FAState s) {
        states.remove(s);
    }

    public void setStates(List states) {
        this.states = states;
    }

    public List getStates() {
        return states;
    }

    public ArrayList getStateNames() {
        ArrayList names = new ArrayList();
        for(int i=0; i<states.size(); i++) {
            FAState wrapper = (FAState)states.get(i);
            names.add(wrapper.name);
        }
        return names;
    }

    public boolean contains(String name) {
        for(int i=0; i<states.size(); i++) {
            FAState wrapper = (FAState)states.get(i);
            if(wrapper.name.equals(name))
                return true;
        }
        return false;
    }

    public void clear() {
        states.clear();
    }

    public int numberOfStartStates() {
        int count = 0;
        for(int i=0; i<states.size(); i++) {
            FAState wrapper = (FAState)states.get(i);
            if(wrapper.start)
                count++;
        }
        return count;
    }

    public int numberOfAcceptedStates() {
        int count = 0;
        for(int i=0; i<states.size(); i++) {
            FAState wrapper = (FAState)states.get(i);
            if(wrapper.accepted)
                count++;
        }
        return count;
    }

    public String check() {
        if(numberOfAcceptedStates() == 0)
            return Localized.getString("faNoAcceptedState");
        if(numberOfStartStates() == 0)
            return Localized.getString("faNoStartState");
        if(numberOfStartStates() > 1)
            return Localized.getString("faMultipleStartStates");

        return null;
    }

    public String getStartState() {
        for(int i=0; i<states.size(); i++) {
            FAState wrapper = (FAState)states.get(i);
            if(wrapper.start)
                return wrapper.name;
        }
        return null;
    }

    public List getFinalStates() {
        List finalStates = new ArrayList();
        for(int i=0; i<states.size(); i++) {
            FAState wrapper = (FAState)states.get(i);
            if(wrapper.accepted)
                finalStates.add(wrapper.name);
        }
        return finalStates;
    }

    public boolean isAccepted(String state) {
        for(int i=0; i<states.size(); i++) {
            FAState wrapper = (FAState)states.get(i);
            if(wrapper.name.equals(state))
                return wrapper.accepted;
        }
        return false;
    }

    public boolean isAccepted(Set stateSet) {
        Iterator iterator = stateSet.iterator();
        while(iterator.hasNext()) {
            if(isAccepted((String)iterator.next()))
                return true;
        }
        return false;
    }

    public String toString() {
        String s = "* states *\n";
        for(int i=0; i<states.size(); i++) {
            FAState state = (FAState)states.get(i);
            s += state+"\n";
        }
        return s;
    }

}
