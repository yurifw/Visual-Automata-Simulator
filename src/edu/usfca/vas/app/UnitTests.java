package edu.usfca.vas.app;

import edu.usfca.vas.machine.fa.FAMachine;
import edu.usfca.vas.machine.fa.FAState;
import edu.usfca.vas.machine.fa.FATransitions;

import java.lang.reflect.Method;
/*

[The "BSD licence"]
Copyright (c) 2005 Jean Bovet
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

public class UnitTests {

    public UnitTests() {
        // Make sure the Preferences don't get called to retrieve the
        // epsilon transition.
        FATransitions.epsilonSymbol = "#";
    }

    public boolean run() {
        for (int i = 0; i < getClass().getMethods().length; i++) {
            Method method = getClass().getMethods()[i];
            if(method.getName().startsWith("test")) {
                try {
                    System.out.println(method.getName());
                    method.invoke(this, null);
                } catch (Exception e) {
                    System.err.println(e.getCause());
                    return false;
                }
            }
        }
        return true;
    }

    public void test_NFA_A() {
        FAMachine machine = new FAMachine();
        machine.setType(FAMachine.MACHINE_TYPE_NFA);
        machine.addState(FAState.createStartState("A"));
        machine.addState(FAState.createAcceptedState("B"));
        machine.addTransitionPattern("A", "#", "A");
        machine.addTransitionPattern("A", "1", "B");
        machine.setSymbolsString("01");
        assert machine.check() == null;
        assert !machine.accept("0") : "0";
        assert !machine.accept("010") : "010";
        assert !machine.accept("11") : "11";
        assert machine.accept("1") : "1";
    }

    public void test_NFA_B() {
        FAMachine machine = new FAMachine();
        machine.setType(FAMachine.MACHINE_TYPE_NFA);

        machine.addState(FAState.createStartState("1"));
        machine.addState(FAState.createState("2"));
        machine.addState(FAState.createAcceptedState("3"));

        machine.addTransitionPattern("1", "0", "2");
        machine.addTransitionPattern("2", "#", "3");

        machine.setSymbolsString("01");

        assert machine.check() == null;
        assert machine.accept("0") : "0";
        assert !machine.accept("00") : "00";
        assert !machine.accept("000") : "000";
        assert !machine.accept("1") : "1";
        assert !machine.accept("10") : "10";
        assert !machine.accept("010") : "010";
    }

    public void test_NFA_C() {
        // Description: last symbol unique

        FAMachine machine = new FAMachine();
        machine.setType(FAMachine.MACHINE_TYPE_NFA);

        machine.addState(FAState.createStartState("0"));
        machine.addState(FAState.createState("1"));
        machine.addState(FAState.createState("2"));
        machine.addState(FAState.createState("3"));
        machine.addState(FAState.createAcceptedState("4"));

        machine.addTransitionPattern("0", "#", "1");
        machine.addTransitionPattern("0", "#", "2");
        machine.addTransitionPattern("0", "#", "3");

        machine.addTransitionPattern("1", "ab", "1");
        machine.addTransitionPattern("2", "bc", "2");
        machine.addTransitionPattern("3", "ac", "3");

        machine.addTransitionPattern("1", "c", "4");
        machine.addTransitionPattern("2", "a", "4");
        machine.addTransitionPattern("3", "b", "4");

        machine.setSymbolsString("abc");

        assert machine.check() == null;
        assert machine.accept("bbaabac") : "bbaabac";
        assert !machine.accept("aa") : "aa";
        assert !machine.accept("bb") : "bb";
        assert !machine.accept("cc") : "cc";
        assert machine.accept("ab") : "ab";
        assert machine.accept("bc") : "bc";
        assert machine.accept("ac") : "ac";
        assert machine.accept("abc") : "abc";
        assert !machine.accept("abca") : "abca";
        assert !machine.accept("bbaabacb") : "bbaabacb";
        assert !machine.accept("bbaabacb") : "bbaabacb";
    }

    public void test_NFA_D() {
        // Description: multiple of 4

        FAMachine machine = new FAMachine();
        machine.setType(FAMachine.MACHINE_TYPE_NFA);

        machine.addState(FAState.createStartState("s"));
        machine.addState(FAState.createAcceptedState("0"));
        machine.addState(FAState.createState("1"));
        machine.addState(FAState.createState("2"));
        machine.addState(FAState.createState("3"));

        machine.addTransitionPattern("s", "048", "0");
        machine.addTransitionPattern("s", "159", "1");
        machine.addTransitionPattern("s", "26", "2");
        machine.addTransitionPattern("s", "37", "3");

        machine.addTransitionPattern("0", "#", "s");

        machine.addTransitionPattern("1", "26", "0");
        machine.addTransitionPattern("1", "048", "2");
        machine.addTransitionPattern("1", "159", "3");
        machine.addTransitionPattern("1", "37", "1");

        machine.addTransitionPattern("2", "#", "s");

        machine.addTransitionPattern("3", "26", "0");
        machine.addTransitionPattern("3", "37", "1");
        machine.addTransitionPattern("3", "048", "2");
        machine.addTransitionPattern("3", "159", "3");

        machine.setSymbolsString("0123456789");

        assert machine.check() == null;
        assert machine.accept("1260") : "1260";
        assert machine.accept("60") : "60";
        assert machine.accept("12") : "12";
        assert machine.accept("4") : "4";
        assert !machine.accept("5") : "5";
        assert !machine.accept("77") : "77";
        assert !machine.accept("123") : "123";
    }

    public static void main(String args[]) {
        if(new UnitTests().run())
            System.out.println("Passed.");
        else
            System.err.println("Error");
    }
}
