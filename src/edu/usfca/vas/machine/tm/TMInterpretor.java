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
import edu.usfca.vas.app.Preferences;
import edu.usfca.vas.graphics.device.OutputDevice;
import edu.usfca.vas.machine.tm.exception.TMException;
import edu.usfca.vas.machine.tm.exception.TMInterpretorException;

import java.util.*;

public class TMInterpretor {

    public static final int STATE_STOPPED = 0;
    public static final int STATE_READY = 1;
    public static final int STATE_RUNNING = 2;
    public static final int STATE_PAUSED = 3;

    private List stack = new ArrayList();
    private TMInterpretorStackElement currentStackElement = null;

    private TMTape tape = new TMTape();
    private List machines = null;
    private Map machineStorage = new HashMap();

    private int state = STATE_STOPPED;

    public int stepCount = 0;
    private int maxRunningStep = 0;

    public TMMachine lastMachine = null;
    public TMOperation lastOperation = null;

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public boolean isStopped() {
        return getState() == STATE_STOPPED;
    }

    public boolean isReady() {
        return getState() == STATE_READY;
    }

    public boolean isRunning() {
        return getState() == STATE_RUNNING;
    }

    public boolean isPaused() {
        return getState() == STATE_PAUSED;
    }

    public void setMachines(List machines) {
        this.machines = machines;
    }

    public void resetWithString(String w) {
        stepCount = 0;
        maxRunningStep = Preferences.getInfiniteTMSteps();

        lastMachine = null;
        lastOperation = null;

        machineStorage.clear();
        stack.clear();
        tape.setContent(w);
    }

    public void run(OutputDevice od, TMMachine startMachine, String w) throws TMException {
        resetWithString(w);

        setState(STATE_RUNNING);

        push(startMachine, null);

        runContinue(od);
    }

    public void runContinue(OutputDevice od) throws TMException {
        while(executeStep(od) && (stepCount<=maxRunningStep)) {
            stepCount++;

            TMOperation op = getLazilyNextOperation();
            if(op != null && op.isBreakPoint()) {
                setState(STATE_PAUSED);
                return;
            }
        }
        setState(STATE_STOPPED);
        if(stepCount > maxRunningStep || getCurrentOperation() != null && getCurrentOperation().isHaltedOnInfinite()) {
            Object[] args = { new Integer(maxRunningStep), tape.toString() };
            od.println(Localized.getFormattedString("tmMachineRunInfinite", args));
        } else {
            Object[] args = { new Integer(stepCount), tape.toString() };
            od.println(Localized.getFormattedString("tmMachineHalted", args));
        }
    }

    public void debug(OutputDevice od, TMMachine startMachine, String w) {
        resetWithString(w);
        push(startMachine, null);
        setState(STATE_READY);
    }

    public void debugForward(OutputDevice od) throws TMException {
        if(executeStep(od)) {
            stepCount++;
            setState(STATE_PAUSED);
        } else {
            setState(STATE_STOPPED);
            if(getCurrentOperation() != null && getCurrentOperation().isHaltedOnInfinite()) {
                Object[] args = { new Integer(maxRunningStep), tape.toString() };
                od.println(Localized.getFormattedString("tmMachineRunInfinite", args));
            } else {
                Object[] args = { new Integer(stepCount), tape.toString() };
                od.println(Localized.getFormattedString("tmMachineHalted", args));
            }
        }
    }

    public boolean executeStep(OutputDevice od) throws TMException {
        TMOperation op = getCurrentOperation();
        if(op == null) {
            // No current operation, fetch the start operation from the current machine
            op = getCurrentMachine().getStartOperation();
        } else {
            // A current operation exists, find the first link to the next one depending
            // on the current tape symbol
            String symbol = getTape().readSymbol();
            TMOperationLink link = op.getFirstCorrespondingLink(symbol);
            if(link == null || link.getTargetOperation() == null) {
                // No corresponding link or next operation: we cannot run the current machine
                // so we pop the run stack to see if we can continue with the caller machine (if it exists)
                pop();
                op = null;
            } else {
                // Store the current symbol in any variable declared for the link
                storeVariable(getCurrentMachine(), link.getVariables(), symbol);
                op = link.getTargetOperation();
            }
        }

        if(getCurrentMachine() != null && op != null) {
            lastMachine = getCurrentMachine();
            lastOperation = op;
            currentStackElement.setOperation(op);
            if(op != null) {
                if(op.execute(od, this, getCurrentMachine(), tape) == false)
                    return false;
            }
        }

        return getCurrentMachine() != null;
    }

    public void storeVariable(TMMachine machine, Set variables, String symbol) {
        if(variables == null || variables.size() == 0)
            return;

        Map storageVariables = (Map)machineStorage.get(machine.getName());
        if(storageVariables == null) {
            storageVariables = new HashMap();
            machineStorage.put(machine.getName(), storageVariables);
        }

        Iterator iterator = variables.iterator();
        while(iterator.hasNext()) {
            String variable = (String)iterator.next();
            storageVariables.put(variable.substring(1), symbol);
        }

        //System.out.println("Store symbol "+symbol+" to variables "+variables+" of machine "+machine.getName());
    }

    public String recallVariable(TMMachine machine, String variableName) {
        //System.out.println("Recall variable "+variableName+" from machine "+machine.getName());
        Map storageVariables = (Map)machineStorage.get(machine.getName());
        if(storageVariables != null) {
            String content = (String)storageVariables.get(variableName);
            if(content != null)
                return content;
        }
        return TMMachine.SYMBOL_BLANK;
    }

    public void call(String machineName) throws TMException {
        Iterator iterator = machines.iterator();
        while(iterator.hasNext()) {
            TMMachine machine = (TMMachine)iterator.next();
            if(machine.getName().equals(machineName)) {
                push(machine, null);
                return;
            }
        }
        Object[] args = { machineName };
        throw new TMInterpretorException(Localized.getFormattedString("tmMachineInvokeException", args));
    }

    public void push(TMMachine machine, TMOperation op) {
        currentStackElement = new TMInterpretorStackElement(machine, op);
        stack.add(currentStackElement);
    }

    public void pop() {
        if(stack.size()>0)
            stack.remove(stack.size()-1);

        if(stack.size() == 0)
            currentStackElement = null;
        else
            currentStackElement = (TMInterpretorStackElement)stack.get(stack.size()-1);
    }

    public TMInterpretorStackElement getPreviousStackElement() {
        if(stack.size()>1)
            return (TMInterpretorStackElement)stack.get(stack.size()-2);
        else
            return null;
    }

    public TMMachine getCurrentMachine() {
        if(currentStackElement == null)
            return null;
        else
            return currentStackElement.getMachine();
    }

    public TMOperation getCurrentOperation() {
        if(currentStackElement == null)
            return null;
        else
            return currentStackElement.getOperation();
    }

    public TMMachine getLazilyNextMachine() {
        // Get current operation
        TMOperation op = getCurrentOperation();
        if(op == null) {
            // If no current operation, we are about to execute the start operation
            // of the current machine
            return getCurrentMachine();
        }

        // Get first corresponding link
        TMOperationLink link = op.getFirstCorrespondingLink(getTape().readSymbol());
        if(link != null && link.getTargetOperation() != null) {
            // There is a next operation, so return the current machine
            return getCurrentMachine();
        }

        // There is no next operation: the current machine will be stopped. Look at the return
        // stack element to see if we have been called by another machine. If this is the case,
        // we use the previous stack element to get the previous machine.
        TMInterpretorStackElement element = getPreviousStackElement();
        if(element == null) {
            // No previous element. We are really about to stop ;-)
            return null;
        }

        // A previous element exists, return the corresponding machine (the "caller")
        return element.getMachine();
    }

    public TMOperation getLazilyNextOperation() {
        // First check if we are running a machine
        if(getCurrentMachine() == null)
            return null;

        // Get current operation
        TMOperation op = getCurrentOperation();
        if(op == null) {
            // If no current operation, we are about to execute the start operation
            // of the current machine
            return getCurrentMachine().getStartOperation();
        }

        // Get first corresponding link
        TMOperationLink link = op.getFirstCorrespondingLink(getTape().readSymbol());
        if(link != null && link.getTargetOperation() != null) {
            // There is a next operation, so return it
            return link.getTargetOperation();
        }

        // There is no next operation: the current machine will be stopped. Look at the return
        // stack element to see if we have been called by another machine. If this is the case,
        // we use the previous stack element to get the previous machine and the next operation
        // to be executed.

        TMInterpretorStackElement element = getPreviousStackElement();
        if(element == null) {
            // No previous element. We are really about to stop ;-)
            return null;
        }

        // A previous element exists, find the next operation of the "caller" machine
        TMOperation nop = element.getOperation();
        if(nop == null) {
            // No next element of the caller machine ? Should never get here because we
            // are always supposed to have on the stack the caller operation which is != null
            System.err.println("No return stack element operation!");
            return null;
        }

        // Return the next operation of the "caller" machine
        return nop;
    }

    public TMTape getTape() {
        return tape;
    }

}
