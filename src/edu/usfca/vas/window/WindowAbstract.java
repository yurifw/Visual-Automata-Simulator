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

package edu.usfca.vas.window;

import edu.usfca.vas.app.Document;
import edu.usfca.vas.app.Localized;
import edu.usfca.vas.app.Preferences;
import edu.usfca.vas.data.DataWrapperAbstract;
import edu.usfca.vas.debug.Debugger;
import edu.usfca.vas.graphics.IconManager;
import edu.usfca.vas.graphics.device.Console;
import edu.usfca.vas.graphics.device.OutputDevice;
import edu.usfca.vas.VisualAutomataSimulator;
import edu.usfca.xj.appkit.frame.XJWindow;
import edu.usfca.xj.appkit.gview.object.GElement;
import edu.usfca.xj.appkit.menu.XJMainMenuBar;
import edu.usfca.xj.appkit.menu.XJMenu;
import edu.usfca.xj.appkit.menu.XJMenuItem;
import edu.usfca.xj.appkit.menu.XJMenuItemDelegate;
import edu.usfca.xj.appkit.utils.XJAlert;
import edu.usfca.xj.appkit.utils.XJFileChooser;
import edu.usfca.xj.foundation.XJUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class WindowAbstract extends XJWindow implements XJMenuItemDelegate {

    // ** Menu constants

    private static final int MI_MACHINE_NEW = 0;
    private static final int MI_MACHINE_CLOSE = 1;
    private static final int MI_MACHINE_DUPLICATE = 2;
    private static final int MI_MACHINE_SETTINGS = 3;
    private static final int MI_COPY_BITMAP_IMAGE = 4;
    private static final int MI_SAVE_BITMAP_IMAGE = 5;
    private static final int MI_SAVE_EPS = 6;
    private static final int MI_SHOW_MAGNETICS = 7;
    private static final int MI_CENTER = 8;

    protected static final int MI_RUN = 10;
    protected static final int MI_CONTINUE = 11;
    protected static final int MI_DEBUG = 12;
    protected static final int MI_DEBUG_PROCEED = 13;
    protected static final int MI_DEBUG_FORWARD = 14;
    protected static final int MI_SHOW_CONSOLE = 15;
    protected static final int MI_SHOW_OVERLAY = 16;

    protected static final int MI_CHECK_FOR_UPDATES = 20;

    // ** Menu

    // Don't affect null value here because it may overwrite a non-null value assigned by
    // customizeMenuBar() which is called by the superclass before the constructor of this subclass

    protected XJMenu menuRun;

    // ** Graphics components

    private JPanel mainComponent;           // Used when no console view is displayed
    private JSplitPane mainSplitComponent;  // Used to display the console view is displayed

    private JLabel stateLabel;
    private JLabel helpLabel;

    private boolean showConsole = false;
    private Console console = new Console();
    private Debugger debugger = new Debugger();
    private Timer debugTimer = null;

    private boolean handleChangeOccured = true;

    protected List windowMachines = new ArrayList();
    protected int currentWindowMachineIndex = 0;

    public WindowAbstract() {
        setSize(1000, 700);
    }

    public void awake() {
        super.awake();

        handleChangeOccured = false;

        awakeConcrete();

        mainComponent = new JPanel(new BorderLayout());
        mainComponent.add(getMachineComponent(), BorderLayout.CENTER);

        mainSplitComponent = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitComponent.setContinuousLayout(true);

        getContentPane().add(mainComponent, BorderLayout.CENTER);
        getContentPane().add(createToolbar(), BorderLayout.SOUTH);

        rebuild();

        contextualHelp(null);

        handleChangeOccured = true;
    }

    public abstract void awakeConcrete();

    public void documentWillWriteData() {

    }

    public void documentDidReadData() {
        rebuild();        
    }

    public abstract JComponent getMachineComponent();

    public Document getAutomataDocument() {
        return (Document)getDocument();
    }

    public OutputDevice getStandardOutputDevice() {
        return console;
    }

    public Debugger getDebugger() {
        return debugger;
    }

    public void customizeHelpMenu(XJMenu menu) {
        menu.addSeparator();
        menu.addItem(new XJMenuItem(Localized.getString("waCheckForUpdates"), MI_CHECK_FOR_UPDATES, this));
    }

    public void customizeMenuBar(XJMainMenuBar menubar) {

        // *** Machine menu

        XJMenu menu = new XJMenu();
        menu.setTitle(Localized.getString("waMenuMachine"));
        menu.addItem(new XJMenuItem(Localized.getString("waMIMachineNew"), KeyEvent.VK_N, XJMenuItem.getKeyModifier() | InputEvent.ALT_MASK, MI_MACHINE_NEW, this));
        menu.addItem(new XJMenuItem(Localized.getString("waMIMachineDuplicate"), MI_MACHINE_DUPLICATE, this));
        menu.addItem(new XJMenuItem(Localized.getString("waMIMachineClose"), KeyEvent.VK_W, XJMenuItem.getKeyModifier() | InputEvent.ALT_MASK, MI_MACHINE_CLOSE, this));
        menu.addSeparator();
        menu.addItem(new XJMenuItem(Localized.getString("waMIMachineShowMagnet"), KeyEvent.VK_M, MI_SHOW_MAGNETICS, this));
        menu.addItem(new XJMenuItem(Localized.getString("waMIMachineShowConsole"), IconManager.getIcon(IconManager.ICON_CONSOLE), KeyEvent.VK_J, MI_SHOW_CONSOLE, this));
        menu.addItem(new XJMenuItem(Localized.getString("waMIMachineShowOverlay"), KeyEvent.VK_O, XJMenuItem.getKeyModifier() | InputEvent.ALT_MASK, MI_SHOW_OVERLAY, this));
        menu.addItem(new XJMenuItem(Localized.getString("waMIMachineCenterAll"), MI_CENTER, this));
        menu.addSeparator();
        menu.addItem(new XJMenuItem(Localized.getString("waMIMachineCopyImage"), MI_COPY_BITMAP_IMAGE, this));
        menu.addItem(new XJMenuItem(Localized.getString("waMIMachineSaveImage"), MI_SAVE_BITMAP_IMAGE, this));
        menu.addItem(new XJMenuItem(Localized.getString("waMIMachineSaveEPS"), MI_SAVE_EPS, this));
        menu.addSeparator();
        menu.addItem(new XJMenuItem(Localized.getString("waMIMachineSettings"), KeyEvent.VK_SEMICOLON, MI_MACHINE_SETTINGS, this));

        menubar.addCustomMenu(menu);

        // *** Run

        menuRun = new XJMenu();
        menuRun.setTitle(Localized.getString("waMenuRun"));
        menuRun.addItem(new XJMenuItem(Localized.getString("waMIRunRun"), IconManager.getIcon(IconManager.ICON_RUN), KeyEvent.VK_R, MI_RUN, this));
        menuRun.addItem(new XJMenuItem(Localized.getString("waMIRunContinue"), KeyEvent.VK_C, MI_CONTINUE, this));
        menuRun.addSeparator();
        menuRun.addItem(new XJMenuItem(Localized.getString("waMIRunDebug"), IconManager.getIcon(IconManager.ICON_DEBUG), KeyEvent.VK_D, MI_DEBUG, this));
        menuRun.addItem(new XJMenuItem(Localized.getString("waMIRunDebugProceed"), IconManager.getIcon(IconManager.ICON_DEBUG_PROCEED), KeyEvent.VK_P, MI_DEBUG_PROCEED, this));
        menuRun.addItem(new XJMenuItem(Localized.getString("waMIRunDebugOneStep"), IconManager.getIcon(IconManager.ICON_DEBUG_ONE_STEP), KeyEvent.VK_F, MI_DEBUG_FORWARD, this));

        menubar.addCustomMenu(menuRun);

        // *** FA or TM menu (implemented by concrete subclass)

        customizeMenuBarForMachine(menubar);
    }


    public abstract void customizeMenuBarForMachine(XJMainMenuBar menubar);

    public JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        Box box = new Box(BoxLayout.X_AXIS);

        stateLabel = new JLabel();
        stateLabel.setFont(new Font(stateLabel.getFont().getName(), Font.PLAIN, 10));
        box.add(stateLabel);

        box.add(Box.createHorizontalGlue());

        helpLabel = new JLabel("");
        helpLabel.setFont(new Font(helpLabel.getFont().getName(), Font.PLAIN, 10));
        box.add(helpLabel);
        box.add(Box.createHorizontalGlue());

        toolbar.add(box);

        return toolbar;
    }

    // *** Public methods

    public abstract void setWindowMachineTitle(WindowMachineAbstract wm, String title);
    public abstract String getWindowMachineTitle(WindowMachineAbstract wm);

    public void clear() {
        if(getCurrentWindowMachineAbstract() != null)
            getCurrentWindowMachineAbstract().clear();
    }

    public void rebuildWindowMachines() {
        Iterator iterator = windowMachines.iterator();
        while(iterator.hasNext()) {
            WindowMachineAbstract wma = (WindowMachineAbstract)iterator.next();
            wma.rebuild();
        }
    }

    public void rebuild() {
        rebuildWindowMachines();
        rebuildMenuMachine();

        refreshMenuRun();

        getMainMenuBar().refresh();
    }

    public void refreshMenuRun() {
        getMainMenuBar().refreshMenuState(menuRun);
    }

    public abstract void refreshMenuMachine();
    public abstract void rebuildMenuMachine();

    public void menuItemState(XJMenuItem item) {
        super.menuItemState(item);
        switch(item.getTag()) {
            case MI_MACHINE_CLOSE:
                item.setEnabled(canCloseWindowMachine());
                break;

            case MI_SHOW_MAGNETICS:
                if(getCurrentWindowMachineAbstract() != null && getCurrentWindowMachineAbstract().getGraphicPanel().isMagneticsVisible())
                    item.setTitle(Localized.getString("waMIMachineHideMagnet"));
                else
                    item.setTitle(Localized.getString("waMIMachineShowMagnet"));
                break;

            case MI_CONTINUE:
                item.setEnabled(isMachinePaused());
                break;

            case MI_DEBUG_PROCEED:
                boolean proceed = false;
                if(isMachineReady()) {
                    proceed = true;
                } else if(isMachineRunning()) {
                    proceed = false;
                } else if(isMachinePaused()) {
                    proceed = !(debugTimer != null && debugTimer.isRunning());
                }

                if(proceed) {
                    item.setTitle(Localized.getString("waMIRunDebugProceed"));
                    item.setIcon(IconManager.getIcon(IconManager.ICON_DEBUG_PROCEED));
                } else {
                    item.setTitle(Localized.getString("waMIRunDebugPause"));
                    item.setIcon(IconManager.getIcon(IconManager.ICON_DEBUG_PAUSE));
                }

                item.setEnabled(!isMachineStopped());
                break;

            case MI_DEBUG_FORWARD:
                item.setEnabled(isMachineReady() || isMachinePaused());
                break;

            case MI_SHOW_CONSOLE:
                if(getCurrentWindowMachineAbstract() == null)
                    item.setEnabled(false);
                else {
                    item.setEnabled(true);
                    if(showConsole)
                        item.setTitle(Localized.getString("waMIMachineHideConsole"));
                    else
                        item.setTitle(Localized.getString("waMIMachineShowConsole"));
                }
                break;

            case MI_SHOW_OVERLAY:
                if(getCurrentWindowMachineAbstract() == null)
                    item.setEnabled(false);
                else {
                    item.setEnabled(getCurrentWindowMachineAbstract().supportsOverlay());
                    if(getCurrentWindowMachineAbstract().isOverlayVisible())
                        item.setTitle(Localized.getString("waMIMachineHideOverlay"));
                    else
                        item.setTitle(Localized.getString("waMIMachineShowOverlay"));
                }
                break;
        }
    }

    // *** Events

    public void handleMenuEvent(XJMenu menu, XJMenuItem item) {
        super.handleMenuEvent(menu, item);
        switch(item.getTag()) {
            case MI_MACHINE_NEW:
                createNewMachine(null);
                break;

            case MI_MACHINE_CLOSE:
                if(Preferences.getAlertOnCloseMachine()) {
                    if(XJAlert.displayAlertYESNO(getJavaContainer(), Localized.getString("waCloseMachineTitle"),
                            Localized.getString("waCloseMachineMessage")) == XJAlert.YES)
                        closeCurrentMachine();
                } else
                    closeCurrentMachine();
                break;

            case MI_MACHINE_DUPLICATE:
                duplicateCurrentMachine();
                break;

            case MI_MACHINE_SETTINGS:
                getCurrentWindowMachineAbstract().displaySettings();
                break;

            case MI_RUN:
                run();
                break;

            case MI_CONTINUE:
                runContinue();
                break;

            case MI_DEBUG:
                debug();
                break;

            case MI_DEBUG_PROCEED:
                debugProceed();
                break;

            case MI_DEBUG_FORWARD:
                debugStepForward();
                break;

            case MI_SHOW_CONSOLE:
                toggleConsoleVisibility();
                getMainMenuBar().refresh();
                break;

            case MI_SHOW_OVERLAY:
                getCurrentWindowMachineAbstract().toggleOverlayVisibility();
                getMainMenuBar().refresh();
                break;

            case MI_COPY_BITMAP_IMAGE:
                copyAsBitmapImage();
                break;

            case MI_SAVE_BITMAP_IMAGE:
                saveAsBitmapImage();
                break;

            case MI_SAVE_EPS:
                saveAsEPS();
                break;

            case MI_SHOW_MAGNETICS:
                getCurrentWindowMachineAbstract().getGraphicPanel().toggleShowMagnetics();
                getMainMenuBar().refresh();
                break;

            case MI_CENTER:
                getCurrentWindowMachineAbstract().getGraphicPanel().centerAll();
                getCurrentWindowMachineAbstract().getGraphicPanel().repaint();
                break;

            case MI_CHECK_FOR_UPDATES:
                VisualAutomataSimulator.checkForUpdates(false);
                break;
        }
    }

    public abstract void createNewMachine(DataWrapperAbstract data);
    public abstract void duplicateCurrentMachine();
    public abstract void removeAllMachine();
    public abstract boolean canCloseWindowMachine();
    public abstract void closeCurrentMachine();

    public abstract void selectWindowMachineAtIndex(int index);
    public abstract WindowMachineAbstract getCurrentWindowMachineAbstract();

    public void copyAsBitmapImage() {
        Image image = getCurrentWindowMachineAbstract().getGraphicPanel().getImage();
        ImageSelection imageSelection = new ImageSelection(image);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        toolkit.getSystemClipboard().setContents(imageSelection, null);
    }

    // Code borrowed from:
    // http://www.devx.com/Java/Article/22326/1954?pf=true

    // Inner class is used to hold an image while on the clipboard.
    public static class ImageSelection implements Transferable
    {
        // the Image object which will be housed by the ImageSelection
        private Image image;

        public ImageSelection(Image image) {
            this.image = image;
        }

        // Returns the supported flavors of our implementation
        public DataFlavor[] getTransferDataFlavors()
        {
            return new DataFlavor[] {DataFlavor.imageFlavor};
        }

        // Returns true if flavor is supported
        public boolean isDataFlavorSupported(DataFlavor flavor)
        {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        // Returns Image object housed by Transferable object
        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException,IOException
        {
            if (!DataFlavor.imageFlavor.equals(flavor))
            {
                throw new UnsupportedFlavorException(flavor);
            }
            // else return the payload
            return image;
        }
    }

    public void saveAsBitmapImage() {
        Image image = getCurrentWindowMachineAbstract().getGraphicPanel().getImage();

        List extensions = new ArrayList();
        for (int i = 0; i < ImageIO.getWriterFormatNames().length; i++) {
            String ext = ImageIO.getWriterFormatNames()[i].toLowerCase();
            if(!extensions.contains(ext))
                extensions.add(ext);
        }

        if(XJFileChooser.shared().displaySaveDialog(getJavaContainer(), extensions, extensions, false)) {
            String file = XJFileChooser.shared().getSelectedFilePath();
            try {
                ImageIO.write((BufferedImage)image, file.substring(file.lastIndexOf(".")+1), new File(file));
            } catch (IOException e) {
                XJAlert.display(getJavaContainer(), "Export Error", "Image \""+file+"\" cannot be saved due to an error:\n"+e);
            }
        }
    }

    public void saveAsEPS() {
        if(XJFileChooser.shared().displaySaveDialog(getJavaContainer(), "eps", "eps", false)) {
            String file = XJFileChooser.shared().getSelectedFilePath();
            String eps = getCurrentWindowMachineAbstract().getGraphicPanel().getEPS();
            try {
                XJUtils.writeStringToFile(eps, file);
            } catch (IOException e) {
                e.printStackTrace();
                XJAlert.display(getJavaContainer(), "Export Error", "Cannot export current machine to EPS file:\n"+file+"\n"+e);
            }
        }
    }

    public abstract boolean isMachineStopped();
    public abstract boolean isMachineReady();
    public abstract boolean isMachineRunning();
    public abstract boolean isMachinePaused();

    public void toggleConsoleVisibility() {
        showConsole = !showConsole;
        if(showConsole) {
            mainComponent.remove(getMachineComponent());

            mainSplitComponent.add(getMachineComponent(), JSplitPane.TOP);
            mainSplitComponent.add(console.getComponent(), JSplitPane.BOTTOM);

            mainComponent.add(mainSplitComponent);
        } else {
            mainSplitComponent.remove(getMachineComponent());
            mainSplitComponent.remove(console.getComponent());

            mainComponent.remove(mainSplitComponent);
            mainComponent.add(getMachineComponent());

            //JSplitPane p = (JSplitPane)getMachineComponent();
            //p.resetToPreferredSizes();
        }
        mainComponent.updateUI();
    }

    public abstract void run();
    public abstract void runContinue();
    public abstract void debug();

    public void debugProceed() {
        if(debugTimer == null) {
            debugTimer = new Timer(500, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(debugStepForward() == false)
                        debugTimer.stop();
                    refreshMenuRun();
                }
            });
        }

        if(debugTimer.isRunning())
            debugTimer.stop();
        else
            debugTimer.start();

        refreshMenuRun();
    }

    public abstract boolean debugStepForward();

    public abstract void updateExecutionComponents();
    public abstract void displayDebuggerInfo();

    // *** Delegate methods

    public void setStateString(String s) {
        stateLabel.setText(s);
    }

    public void setHelpString(String s) {
        helpLabel.setText(s);
    }

    public abstract void contextualHelp(GElement element);

    public void changeOccured() {
        if(handleChangeOccured)
            getDocument().changeDone();
    }

}
