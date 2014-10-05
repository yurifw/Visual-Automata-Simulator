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

package edu.usfca.vas.window.batch;

import edu.usfca.vas.app.Localized;
import edu.usfca.vas.data.DataTM;
import edu.usfca.vas.data.DataWrapperTM;
import edu.usfca.vas.graphics.device.Console;
import edu.usfca.vas.machine.tm.TMInterpretor;
import edu.usfca.vas.machine.tm.TMMachine;
import edu.usfca.vas.machine.tm.TMTape;
import edu.usfca.vas.machine.tm.exception.TMException;
import edu.usfca.xj.appkit.frame.XJDialog;
import edu.usfca.xj.appkit.utils.XJAlert;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BatchTestTM {

    public static final int IGNORE = -1;

    protected DataTM data = new DataTM();
    protected TMInterpretor interpretor = new TMInterpretor();
    protected Console console = new Console();

    protected List testItems = null;

    protected String runMachineName = null;
    protected int runMachineAtIndex = IGNORE;

    protected String initialTapeContent = null;
    protected int initalTapeHeadPosition = 0;

    protected String testTapeContent = null;
    protected int testTapeHeadPosition = IGNORE;

    protected String resultTapeContent = null;
    protected int resultTapeHeadPosition = IGNORE;

    protected XJDialog parent;

    public BatchTestTM() {
    }

    public BatchTestTM(XJDialog parent) {
        this.parent = parent;
    }

    public void setTestItems(List testItems) {
        this.testItems = testItems;
    }

    public void setRunMachineName(String name) {
        this.runMachineName = name;
    }

    public void setRunMachineAtIndex(int index) {
        this.runMachineAtIndex = index;
    }

    public void setInitialTapeContent(String s) {
        this.initialTapeContent = s;
    }

    public void setInitialTapeHeadPosition(int position) {
        this.initalTapeHeadPosition = position;
    }

    public void setTestTapeContent(String text) {
        this.testTapeContent = text;
    }

    public void setTestTapeHeadPosition(int position) {
        this.testTapeHeadPosition = position;
    }

    public void runTests() {
        Iterator iterator = testItems.iterator();
        while(iterator.hasNext()) {
            BatchTestTMItem item = (BatchTestTMItem)iterator.next();
            if(runTest(item.file)) {
                item.failed = false;
                item.result = "Ã";

                item.machineCount = data.getWrappers().size();
                item.stepCount = interpretor.stepCount;

                if(!TMTape.isContentEquals(testTapeContent, resultTapeContent)) {
                    item.result= Localized.getString("tmBatchTapeContentError");
                    item.failed = true;
                }
                if(testTapeHeadPosition != resultTapeHeadPosition && testTapeHeadPosition != IGNORE) {
                    item.result = Localized.getString("tmBatchTapeHeadPositionError");
                    item.failed = true;
                }
                item.tapeContent = interpretor.getTape().toString();
            }
        }
    }

    public boolean runTest(String file) {
        if(!load(file))
            return false;

        TMMachine machine = null;
        if(runMachineAtIndex == IGNORE) {
            Iterator iterator = data.getWrappers().iterator();
            while(iterator.hasNext()) {
                DataWrapperTM wrapper = (DataWrapperTM)iterator.next();
                TMMachine candidate = wrapper.getTMMachine();
                if(candidate.getName().equals(runMachineName)) {
                    machine = candidate;
                    break;
                }
            }
        } else {
            List wrappers = data.getWrappers();
            if(runMachineAtIndex<wrappers.size()) {
                DataWrapperTM wrapper = (DataWrapperTM)wrappers.get(runMachineAtIndex);
                machine = wrapper.getTMMachine();
            }
        }

        if(machine == null) {
            String t;

            if(runMachineAtIndex == IGNORE) {
                Object[] args = { runMachineName, file };
                t = Localized.getFormattedString("tmBatchFindMachineError1", args);
            } else {
                Object[] args = { new Integer(runMachineAtIndex), file };
                t = Localized.getFormattedString("tmBatchFindMachineError2", args);
            }

            XJAlert.display(parent.getJavaComponent(), Localized.getString("error"), t);
            return false;
        }

        if(machine.getStartOperation() == null) {
            Object[] args = { file };
            XJAlert.display(parent.getJavaComponent(), Localized.getString("error"), Localized.getFormattedString("tmBatchNoStartOp", args));
            return false;
        }

        interpretor.setMachines(getAllTMMachine());
        try {
            interpretor.getTape().setPosition(initalTapeHeadPosition);
            interpretor.run(console, machine, initialTapeContent);

            // Continue to run the machine if the interpretor is paused
            // because we don't want here to stop on a breakpoint
            while(interpretor.isPaused()) {
                interpretor.runContinue(console);
            }
        } catch (TMException e) {
            XJAlert.display(parent.getJavaComponent(), Localized.getString("error"), e.message);
            return false;
        }

        resultTapeContent = interpretor.getTape().getContent();
        resultTapeHeadPosition = interpretor.getTape().getPosition();

        return true;
    }

    public boolean load(String file) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            data.readData(ois);
            ois.close();
        } catch(Exception e) {
            XJAlert.display(parent.getJavaComponent(), Localized.getString("error"), e.toString());
            return false;
        }

        return true;
    }

    public List getAllTMMachine() {
        List machines = new ArrayList();
        Iterator iterator = data.getWrappers().iterator();
        while(iterator.hasNext()) {
            DataWrapperTM wrapper = (DataWrapperTM)iterator.next();
            machines.add(wrapper.getTMMachine());
        }
        return machines;
    }


}
