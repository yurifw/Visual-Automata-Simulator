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

package edu.usfca.vas.window.tm;

import edu.usfca.vas.app.Localized;
import edu.usfca.vas.data.DataTM;
import edu.usfca.vas.data.DataWrapperAbstract;
import edu.usfca.vas.data.DataWrapperTM;
import edu.usfca.vas.debug.DebugWrapper;
import edu.usfca.vas.graphics.tm.GElementTMOperation;
import edu.usfca.vas.machine.tm.TMInterpretor;
import edu.usfca.vas.machine.tm.TMMachine;
import edu.usfca.vas.machine.tm.TMOperation;
import edu.usfca.vas.machine.tm.exception.TMException;
import edu.usfca.vas.machine.tm.exception.TMTapeException;
import edu.usfca.vas.window.WindowAbstract;
import edu.usfca.vas.window.WindowMachineAbstract;
import edu.usfca.vas.window.batch.BatchTestTMSettings;
import edu.usfca.xj.appkit.gview.object.GElement;
import edu.usfca.xj.appkit.gview.object.GLink;
import edu.usfca.xj.appkit.menu.XJMainMenuBar;
import edu.usfca.xj.appkit.menu.XJMenu;
import edu.usfca.xj.appkit.menu.XJMenuItem;
import edu.usfca.xj.appkit.utils.XJAlert;
import edu.usfca.xj.foundation.XJUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WindowTM extends WindowAbstract {

    private static final int MI_ERASE_TAPE = 100;
    private static final int MI_REMEMBER_CONFIGURATION = 101;
    private static final int MI_EDIT_CONFIGURATIONS = 102;

    private static final int MI_BATCH_TEST = 103;
    private static final int MI_SHOW_TRANSITIONS = 104;

    private static final int MI_FAVORITE_CONTENTS = 1000;

    private JPanel machineComponent;
    private JTabbedPane tabbedComponent;
    private TapePanel tapeComponent;

    private XJMenu menuTape;
    private XJMenu menuTools;

    private XJMenuItem menuItemEditConfiguration;

    private TMInterpretor interpretor = new TMInterpretor();

    private BatchTestTMSettings batchTestSettings;

    public void awakeConcrete() {
        tabbedComponent = new JTabbedPane();
        tabbedComponent.setMinimumSize(new Dimension(0, 400));
        tabbedComponent.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                currentWindowMachineIndex = tabbedComponent.getSelectedIndex();
                getAutomataDocument().getAutomataData().setCurrentWrapperIndex(currentWindowMachineIndex);
                getMainMenuBar().refresh();
                changeOccured();
            }
        });

        tapeComponent = new TapePanel();
        tapeComponent.setTape(interpretor.getTape());

        JScrollPane scrollPane = new JScrollPane(tapeComponent);
        scrollPane.setMinimumSize(new Dimension(640, TapePanel.TAPE_HEIGHT+30));
        scrollPane.setPreferredSize(new Dimension(640, TapePanel.TAPE_HEIGHT+30));
        scrollPane.setWheelScrollingEnabled(true);

        JPanel component = new JPanel(new BorderLayout());
        component.add(tabbedComponent, BorderLayout.CENTER);
        component.add(scrollPane, BorderLayout.SOUTH);

        machineComponent = component;

        if(windowMachines.size() == 0)
            createNewMachine(null);

    }

    public void documentWillWriteData() {
        getData().setDefaultConfiguration(new TapeConfiguration(tapeComponent.getTape()));
    }

    public void documentDidReadData() {
        rebuild();
        TapeConfiguration conf = getData().getDefaultConfiguration();
        if(conf != null) {
            try {
                conf.apply(tapeComponent.getTape());
            } catch (TMTapeException e) {
                XJAlert.display(getJavaContainer(), Localized.getString("error"), Localized.getString("tmApplyTapeConfErr")+e.message);
            }
        }
    }

    public JComponent getMachineComponent() {
        return machineComponent;
    }

    public DataTM getData() {
        return (DataTM)getDocumentData();
    }

    public List getTapeConfigurations() {
        if(getData() == null)
            return null;
        else
            return getData().getTapeConfigurations();
    }

    public void customizeMenuBarForMachine(XJMainMenuBar menubar) {

        menuTape = new XJMenu();
        menuTape.setTitle(Localized.getString("tmMenuTape"));
        menuTape.addItem(new XJMenuItem(Localized.getString("tmMITapeErase"), KeyEvent.VK_E, MI_ERASE_TAPE, this));
        menuTape.addSeparator();
        menuTape.addItem(new XJMenuItem(Localized.getString("tmMITapeStoreConfig"), KeyEvent.VK_B, MI_REMEMBER_CONFIGURATION, this));
        menuTape.addItem(menuItemEditConfiguration = new XJMenuItem(Localized.getString("tmMITapeEditConfig"), MI_EDIT_CONFIGURATIONS, this));

        buildMenuTapeItems();

        menuTools = new XJMenu();
        menuTools.setTitle(Localized.getString("tmMenuTools"));
        menuTools.addItem(new XJMenuItem(Localized.getString("tmMIToolsBatchTest"), MI_BATCH_TEST, this));
        menuTools.addSeparator();
        menuTools.addItem(new XJMenuItem(Localized.getString("tmMIToolsShowTransitions"), KeyEvent.VK_T, MI_SHOW_TRANSITIONS, this));

        menubar.addCustomMenu(menuTape);
        menubar.addCustomMenu(menuTools);
    }

    public void buildMenuTapeItems() {
        if(getTapeConfigurations() != null && getTapeConfigurations().size()>0) {
            int i = 0;
            menuTape.addSeparator();
            Iterator iterator = getTapeConfigurations().iterator();
            while(iterator.hasNext()) {
                TapeConfiguration config = (TapeConfiguration)iterator.next();
                menuTape.addItem(new XJMenuItem(config.content, MI_FAVORITE_CONTENTS+i++, this));
            }
        }
    }

    public void refreshMenuMachine() {
        getMainMenuBar().refreshMenuState(menuTape);
    }

    public void rebuildMenuMachine() {
        for(int i=menuTape.getItemCount()-1; i>0; i--) {
            XJMenuItem item = menuTape.getItemAtIndex(i);
            if(item == menuItemEditConfiguration)
                break;
            menuTape.removeItem(i);
        }

        buildMenuTapeItems();
    }

    public TMInterpretor getInterpretor() {
        return interpretor;
    }

    public GElementTMOperation getGElement(TMMachine machine, TMOperation operation) {
        WindowMachineTM wm = getWindowMachineTMContainingTMMachine(machine);
        if(wm == null)
            return null;

        DataWrapperTM dw = wm.getDataWrapperTM();
        return dw.getGraphicMachine().getGraphicOperation(operation);
    }

    public GLink getGLink(TMMachine machine, GElementTMOperation op1, GElementTMOperation op2) {
        WindowMachineTM wm = getWindowMachineTMContainingTMMachine(machine);
        if(wm == null)
            return null;

        DataWrapperTM dw = wm.getDataWrapperTM();
        return dw.getGraphicMachine().getGraphicOperationLink(op1, op2);
    }

    public int getIndexOfMachineWindowTMContainingTMMachine(TMMachine machine) {
        return windowMachines.indexOf(getWindowMachineTMContainingTMMachine(machine));
    }

    public WindowMachineTM getWindowMachineTMContainingTMMachine(TMMachine machine) {
        Iterator iterator = windowMachines.iterator();
        while(iterator.hasNext()) {
            WindowMachineTM wm = (WindowMachineTM)iterator.next();
            if(wm.getDataWrapperTM().getTMMachine() == machine)
                return wm;
        }
        return null;
    }

    public boolean alreadyExistsMachineWithName(String name, WindowMachineTM exclude) {
        Iterator iterator = windowMachines.iterator();
        while(iterator.hasNext()) {
            WindowMachineTM wm = (WindowMachineTM)iterator.next();
            if(wm != exclude && wm.getTMMachine().getName().equals(name))
                return true;
        }
        return false;
    }

    public List getAllTMMachine() {
        List machines = new ArrayList();
        Iterator iterator = windowMachines.iterator();
        while(iterator.hasNext()) {
            WindowMachineTM wm = (WindowMachineTM)iterator.next();
            machines.add(wm.getDataWrapperTM().getTMMachine());
        }
        return machines;
    }

    public void setWindowMachineTitle(WindowMachineAbstract wm, String title) {
        tabbedComponent.setTitleAt(windowMachines.indexOf(wm), title);
    }

    public String getWindowMachineTitle(WindowMachineAbstract wm) {
        return tabbedComponent.getTitleAt(windowMachines.indexOf(wm));
    }

    public void refactorRenameMachine(String oldName, String newName) {
        Iterator iterator = windowMachines.iterator();
        while(iterator.hasNext()) {
            WindowMachineTM wm = (WindowMachineTM)iterator.next();
            wm.machineDidRename(oldName, newName);
        }
    }

    public void handleMenuEvent(XJMenu menu, XJMenuItem item) {
        super.handleMenuEvent(menu, item);
        if(menu != menuTape && menu != menuTools)
            return;

        switch(item.getTag()) {
            case MI_ERASE_TAPE:
                tapeComponent.clear();
                break;

            case MI_REMEMBER_CONFIGURATION:
                getTapeConfigurations().add(new TapeConfiguration(tapeComponent.getTape()));
                changeOccured();
                rebuildMenuMachine();
                break;

            case MI_BATCH_TEST:
                if(batchTestSettings == null)
                    batchTestSettings = new BatchTestTMSettings(this);
                batchTestSettings.runModal();
                break;

            case MI_SHOW_TRANSITIONS:
                XJAlert.display(getJavaContainer(), Localized.getString("tmTransitionsTitle"), getCurrentWindowMachineTM().getDataWrapperTM().getTMMachine().dumpTransitions());
                break;

            case MI_EDIT_CONFIGURATIONS:
                TapeFavoriteContentsEditor dialog = new TapeFavoriteContentsEditor(getJavaContainer());
                dialog.setFavorites(getTapeConfigurations());
                dialog.runModal();
                changeOccured();
                rebuildMenuMachine();
                break;

            default:
                if(item.getTag()>=MI_FAVORITE_CONTENTS) {
                    TapeConfiguration conf = (TapeConfiguration)getTapeConfigurations().get(item.getTag()-MI_FAVORITE_CONTENTS);
                    try {
                        conf.apply(tapeComponent.getTape());
                    } catch (TMTapeException e) {
                        XJAlert.display(getJavaContainer(), Localized.getString("error"), Localized.getString("tmApplyTapeConfErr")+e.message);
                    }
                    changeOccured();
                    tapeComponent.repaint();
                }
        }
    }

    public void createNewMachine(DataWrapperAbstract data) {
        if(data == null) {
            data = new DataWrapperTM();
            data.setName(Localized.getString("machine")+" "+(windowMachines.size()+1));
            getAutomataDocument().getAutomataData().addWrapper(data);
        }

        WindowMachineTM wm = new WindowMachineTM(this);
        wm.setWindow(this);
        wm.setDataWrapper(data);
        wm.init();
        wm.getGraphicPanel().createMagnetics();

        windowMachines.add(wm);
        tabbedComponent.addTab(data.getName(), wm);
        tabbedComponent.setSelectedComponent(wm);
    }

    public void duplicateCurrentMachine() {
        try {
            DataWrapperAbstract data = getAutomataDocument().getAutomataData().getDataWrapperAtIndex(currentWindowMachineIndex);
            DataWrapperAbstract copy = (DataWrapperAbstract)XJUtils.clone(data);
            getAutomataDocument().getAutomataData().addWrapper(copy);
            createNewMachine(copy);
        } catch(Exception e) {
            XJAlert.display(getJavaContainer(), Localized.getString("tmDuplicateMachineTitle"), Localized.getString("tmDuplicateMachineMessage")+"\n"+e);
        }
    }

    public void removeAllMachine() {
        windowMachines.clear();
        tabbedComponent.removeAll();
    }

    public void closeCurrentMachine() {
        getAutomataDocument().getAutomataData().removeDataWrapperAtIndex(currentWindowMachineIndex);

        windowMachines.remove(currentWindowMachineIndex);
        tabbedComponent.removeTabAt(currentWindowMachineIndex);

        getMainMenuBar().refresh();
        changeOccured();
    }

    public boolean canCloseWindowMachine() {
        return windowMachines.size()>1;
    }

    public void selectWindowMachineAtIndex(int index) {
        if(index<0 || index>=windowMachines.size())
            return;

        tabbedComponent.setSelectedIndex(index);
    }

    public WindowMachineTM getCurrentWindowMachineTM() {
        return (WindowMachineTM)getCurrentWindowMachineAbstract();
    }

    public WindowMachineAbstract getCurrentWindowMachineAbstract() {
        if(currentWindowMachineIndex>-1 && windowMachines.size()>currentWindowMachineIndex)
            return (WindowMachineAbstract)windowMachines.get(currentWindowMachineIndex);
        else
            return null;
    }

    public boolean isMachineStopped() {
        return interpretor.isStopped();
    }

    public boolean isMachineReady() {
        return interpretor.isReady();
    }

    public boolean isMachineRunning() {
        return interpretor.isRunning();
    }

    public boolean isMachinePaused() {
        return interpretor.isPaused();
    }

    public void run() {
        WindowMachineTM wm = getCurrentWindowMachineTM();
        if(!check(wm.getTMMachine()))
            return;

        interpretor.setMachines(getAllTMMachine());
        try {
            interpretor.run(getStandardOutputDevice(), wm.getTMMachine(), tapeComponent.getContent());
        } catch (TMException e) {
            XJAlert.display(getJavaContainer(), Localized.getString("error"), Localized.getString("tmMachineRunException")+e.message);
        }
        updateExecutionComponents();
        if(interpretor.isPaused())
            displayDebuggerInfo();
        else
            displayLastExecutedStep();
    }

    public void runContinue() {
        interpretor.setMachines(getAllTMMachine());
        try {
            interpretor.runContinue(getStandardOutputDevice());
        } catch (TMException e) {
            XJAlert.display(getJavaContainer(), Localized.getString("error"), Localized.getString("tmMachineRunException")+e.message);
        }
        updateExecutionComponents();
        if(interpretor.isPaused())
            displayDebuggerInfo();
        else
            displayLastExecutedStep();
    }

    public void debug() {
        WindowMachineTM wm = getCurrentWindowMachineTM();
        if(!check(wm.getTMMachine()))
            return;

        getDebugger().reset();
        interpretor.setMachines(getAllTMMachine());
        interpretor.debug(getStandardOutputDevice(), wm.getTMMachine(), tapeComponent.getContent());

        updateExecutionComponents();
        displayDebuggerInfo();
    }

    public boolean check(TMMachine machine) {
        if(machine.getStartOperation() == null) {
            XJAlert.display(getJavaContainer(), Localized.getString("tmMachineNoStartOpTitle"), Localized.getString("tmMachineNoStartOpMessage"));
            return false;
        } else
            return true;
    }

    public boolean debugStepForward() {
        if(interpretor.isStopped())
            return false;

        try {
            interpretor.debugForward(getStandardOutputDevice());
        } catch (TMException e) {
            XJAlert.display(getJavaContainer(), Localized.getString("error"), Localized.getString("tmMachineRunStepException")+e.message);
        }

        int index = getIndexOfMachineWindowTMContainingTMMachine(interpretor.getCurrentMachine());
        if(index >= 0)
            selectWindowMachineAtIndex(index);
        updateExecutionComponents();
        displayDebuggerInfo();

        TMOperation op = interpretor.getLazilyNextOperation();
        if(op == null)
            return false;
        else
            return !op.isBreakPoint();
    }

    public void updateExecutionComponents() {
        refreshMenuRun();
        tapeComponent.repaint();
        getCurrentWindowMachineTM().getGraphicPanel().repaint();
    }

    public void displayLastExecutedStep() {
        DebugWrapper w1 = new DebugWrapper();
        GElementTMOperation op1 = getGElement(interpretor.lastMachine, interpretor.lastOperation);
        w1.addElement(op1);

        getDebugger().reset();
        getDebugger().addHistory(w1);
        getDebugger().setHistoryPenSize(2);

        w1.setColor(Color.green);

        getCurrentWindowMachineAbstract().getGraphicPanel().repaint();
        tapeComponent.repaint();
    }

    public void displayDebuggerInfo() {
        DebugWrapper w1 = new DebugWrapper();
        GElementTMOperation op1 = getGElement(interpretor.getCurrentMachine(), interpretor.getCurrentOperation());
        w1.addElement(op1);

        DebugWrapper w2 = new DebugWrapper();
        GElementTMOperation op2 = getGElement(interpretor.getLazilyNextMachine(), interpretor.getLazilyNextOperation());
        w2.addElement(op2);
        w2.addElement(getGLink(interpretor.getCurrentMachine(), op1, op2));

        getDebugger().reset();
        getDebugger().addHistory(w1);
        getDebugger().addHistory(w2);
        getDebugger().setHistoryPenSize(2);

        w1.setColor(Color.green);
        w2.setColor(Color.orange);

        getCurrentWindowMachineAbstract().getGraphicPanel().repaint();
        tapeComponent.repaint();
    }

    public void contextualHelp(GElement element) {
        if(element == null) {
            setHelpString(Localized.getString("tmHelpNoElement"));
        } else if(element instanceof GElementTMOperation) {
            if(element.isSelected())
                setHelpString(Localized.getString("tmHelpOpSelected"));
            else
                setHelpString(Localized.getString("tmHelpOpDeselected"));
        } else if(element instanceof GLink) {
            setHelpString(Localized.getString("tmHelpLink"));
        }
    }

}
