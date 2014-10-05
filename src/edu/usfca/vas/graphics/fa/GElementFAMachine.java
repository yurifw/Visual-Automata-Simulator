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

package edu.usfca.vas.graphics.fa;

import edu.usfca.vas.app.Localized;
import edu.usfca.vas.machine.Tool;
import edu.usfca.vas.machine.fa.FAMachine;
import edu.usfca.vas.machine.fa.FAState;
import edu.usfca.vas.machine.fa.FATransition;
import edu.usfca.xj.appkit.gview.GView;
import edu.usfca.xj.appkit.gview.object.GElement;
import edu.usfca.xj.appkit.gview.object.GLink;
import edu.usfca.xj.foundation.XJXMLSerializable;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GElementFAMachine extends GElement implements XJXMLSerializable {

    public static final int STATE_STOPPED = 0;
    public static final int STATE_READY = 1;
    public static final int STATE_RUNNING = 2;
    public static final int STATE_PAUSED = 3;

    protected FAMachine machine;
    protected transient int state = STATE_STOPPED;

    public GElementFAMachine() {
    }

    public GElementFAMachine(FAMachine faMachine) {
        setMachine(faMachine);
    }

    public Object[] getSymbols() {
        return machine.getSymbols().toArray();
    }

    public void setMachine(FAMachine machine) {
        this.machine = machine;
    }

    public FAMachine getMachine() {
        return machine;
    }

    public void addStateAtXY(String s, double x, double y) {
        FAState state = new FAState(s);
        machine.addState(state);
        addElement(new GElementFAState(state, x, y));
    }

    public GElementFAState getState(String name) {
        ListIterator e = elements.listIterator();
        while(e.hasNext()) {
            GElement element = (GElement)e.next();
            if(element.getClass().equals(GElementFAState.class)) {
                GElementFAState state = (GElementFAState)element;
                if(state.state.name.equals(name))
                    return state;
            }
        }
        return null;
    }

    public GElementFAState getState1(GLink link) {
        return (GElementFAState)link.source;
    }

    public GElementFAState getState2(GLink link) {
        return (GElementFAState)link.target;
    }

    public GLink getTransition(FATransition transition) {
        ListIterator e = elements.listIterator();
        while(e.hasNext()) {
            GElement element = (GElement)e.next();
            if(element.getClass().equals(GLink.class)) {
                GLink link = (GLink)element;
                if(getState1(link).state.name.equals(transition.s1) &&
                   getState2(link).state.name.equals(transition.s2)  &&
                    Tool.symbolsInPattern(link.pattern).contains(transition.symbol))
                {
                    return link;
                }
            }
        }
        return null;
    }
    
    public void removeState(GElementFAState s) {
        machine.removeState(s.state);
        removeElement(s);
        // Remove any other link which is using the state s
        ListIterator e = elements.listIterator();
        while(e.hasNext()) {
            GElement element = (GElement)e.next();
            if(element.getClass().equals(GLink.class)) {
                GLink link = (GLink)element;
                if(link.source == s || link.target == s) {
                    removeElement(link);
                    e = elements.listIterator();
                }
            }
        }
    }

    public boolean createLink(GElementFAState source, String sourceAnchorKey, GElementFAState target, String targetAnchorKey, int shape, Point mouse) {
        String pattern = (String)JOptionPane.showInputDialog(null, Localized.getString("faNewLinkMessage"),
                                    Localized.getString("faNewLinkTitle"),
                                    JOptionPane.QUESTION_MESSAGE, null, null, null);
        if(pattern != null) {
            machine.addTransitionPattern(source.state.name, pattern, target.state.name);
            addElement(new GLink(source, sourceAnchorKey, target, targetAnchorKey, shape, pattern, mouse, GView.DEFAULT_LINK_FLATENESS));
        }

        return pattern != null;
    }

    public boolean editLink(GLink link) {
        String pattern = (String)JOptionPane.showInputDialog(null, Localized.getString("faEditLinkMessage"),
                                    Localized.getString("faEditLinkTitle"),
                                    JOptionPane.QUESTION_MESSAGE, null, null, link.pattern);
        if(pattern != null) {
            removeLink(link);
            link.pattern = pattern;
            machine.addTransitionPattern(getState1(link).state.name, link.pattern, getState2(link).state.name);
            addElement(link);
        }

        return pattern != null;
    }

    public void removeLink(GLink link) {
        removeElement(link);
        machine.removeTransitionPattern(getState1(link).state.name, link.pattern, getState2(link).state.name);
    }

    public void reconstruct() {
        elements.clear();
        List stateNames = machine.getStateList();

        ListIterator iterator = stateNames.listIterator(stateNames.size());

        int x = 0;
        int y = 0;
        while(iterator.hasPrevious()) {
            addElement(new GElementFAState((FAState)iterator.previous(), 100+x*200, 50+y*200));
            x++;
            if(x>4) {
                y++;
                x = 0;
            }
        }

        List transitions = machine.getTransitions().getTransitions();
        iterator = transitions.listIterator();
        while(iterator.hasNext()) {
            FATransition transition = (FATransition)iterator.next();

            GElementFAState s1 = getState(transition.s1);
            GElementFAState s2 = s1;
            if(transition.s1.equals(transition.s2) == false)
                s2 = getState(transition.s2);

            GLink link = getLink(s1, s2);
            if(link == null)
                addElement(new GLink(s1, GElementFAState.ANCHOR_CENTER,
                            s2, GElementFAState.ANCHOR_CENTER,
                            GLink.SHAPE_ARC, transition.symbol, 20));
            else
                link.pattern = Tool.addSymbolToPattern(link.pattern, transition.symbol);
        }
    }

    public GLink getLink(GElementFAState s1, GElementFAState s2) {
        Iterator elements = getElements().iterator();
        while(elements.hasNext()) {
            Object e = elements.next();
            if(e instanceof GLink) {
                GLink l = (GLink)e;
                if(l.source == s1 && l.target == s2)
                    return l;
            }
        }
        return null;
    }

    // *** Exec methods

    public String check(String s) {
        String error = machine.check();
        if(error != null) {
            JOptionPane.showMessageDialog(null, Localized.getString("faCannotStart")+"\n"+error, Localized.getString("error"),
                    JOptionPane.INFORMATION_MESSAGE, null);
            return null;
        }

        if(s == null)
            s = (String)JOptionPane.showInputDialog(null, Localized.getString("faParseString"), Localized.getString("faStartTitle"),
                                    JOptionPane.QUESTION_MESSAGE, null, null, null);

        return s;
    }

    public void run(String s) {
        s = check(s);
        if(s == null)
            return;

        if(machine.accept(s))
            JOptionPane.showMessageDialog(null, Localized.getString("faAcceptString"), Localized.getString("automaton"),
                    JOptionPane.INFORMATION_MESSAGE, null);
        else
            JOptionPane.showMessageDialog(null, Localized.getString("faRejectString"), Localized.getString("automaton"),
                    JOptionPane.ERROR_MESSAGE, null);
    }

    public boolean isStopped() {
        return state == STATE_STOPPED;
    }

    public boolean isReady() {
        return state == STATE_READY;
    }

    public boolean isRunning() {
        return state == STATE_RUNNING;
    }

    public boolean isPaused() {
        return state == STATE_PAUSED;
    }

    // *** Debug methods

    public void debugReset(String s) {
        String r = check(s);
        if(r == null)
            return;

        state = STATE_READY;
        machine.debugReset(r);
    }

    public void debugStepForward() {
        if(machine.debugStepForward())
            state = STATE_PAUSED;
        else
            state = STATE_STOPPED;
    }

    public List debugLastStates() {
        List states = new ArrayList();

        Set set = machine.getStateSet();
        if(set.isEmpty())
            set = machine.getStartStates();

        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            states.add(getState((String)iterator.next()));
        }

        return states;
    }

    public List debugLastTransitions() {
        List transitions = new ArrayList();

        Iterator iterator = machine.getLastTransitionSet().iterator();
        while(iterator.hasNext()) {
            transitions.add(getTransition((FATransition)iterator.next()));
        }

        return transitions;
    }

    public void toggleStartState(GElementFAState state) {
        if(state.isStart() == false) {
            Iterator iterator = elements.iterator();
            while(iterator.hasNext()) {
                GElement element = (GElement)iterator.next();
                if(element instanceof GElementFAState) {
                    ((GElementFAState)element).setStart(false);
                }
            }
        }
        state.toggleStart();
    }

    public void clear() {
        machine.clear();
        elements.clear();
    }

}
