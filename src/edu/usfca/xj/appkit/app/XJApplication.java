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

package edu.usfca.xj.appkit.app;

import edu.usfca.xj.appkit.document.XJData;
import edu.usfca.xj.appkit.document.XJDocument;
import edu.usfca.xj.appkit.document.XJDocumentType;
import edu.usfca.xj.appkit.frame.XJFrame;
import edu.usfca.xj.appkit.frame.XJFrameDelegate;
import edu.usfca.xj.appkit.frame.XJPanel;
import edu.usfca.xj.appkit.frame.XJWindow;
import edu.usfca.xj.appkit.menu.XJMainMenuBar;
import edu.usfca.xj.appkit.utils.XJAlert;
import edu.usfca.xj.appkit.utils.XJAlertInput;
import edu.usfca.xj.appkit.utils.XJFileChooser;
import edu.usfca.xj.appkit.utils.XJLocalizable;
import edu.usfca.xj.foundation.XJObject;
import edu.usfca.xj.foundation.XJSystem;
import edu.usfca.xj.foundation.timer.XJScheduledTimer;
import edu.usfca.xj.foundation.timer.XJScheduledTimerDelegate;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

public class XJApplication extends XJObject implements XJApplicationInterface, XJFrameDelegate {

    public static final boolean YES = true;
    public static final boolean NO = false;

    public static final String XJ_PREFS_RECENT_FILES = "XJ_PREFS_RECENT_FILES";

    protected static XJApplicationInterface shared = null;
    protected static XJApplicationDelegate delegate = null;

    protected static List scheduledTimers = new ArrayList();
    protected static final long SCHEDULED_TIMER_MINUTES = 1;

    protected static List documents = new ArrayList();
    protected static List windows = new ArrayList();

    public static final int MAX_RECENT_FILES = 10;
    protected static List recentFiles = null;

    protected static int documentAbsoluteCount = 0;
    protected static int documentAbsPositionCount = 0;
    protected static final int DOCUMENT_OFFSET_PIXELS = 20;

    protected static List documentType = new ArrayList();
    protected static String propertiesPath = "";

    protected static boolean startingUp = true;
    protected static String[] launchArguments = null;

    protected static String appName = "";

    protected XJPreferences userPrefs = null;

    protected XJFrame about = null;
    protected XJPanel prefs = null;

    protected static XJScheduledTimerDelegate autoSaveTimer = null;

    public static synchronized void setShared(XJApplicationInterface shared) {
        XJApplication.shared = shared;
    }

    public static synchronized XJApplicationInterface shared() {
        if(shared == null) {
            if(XJSystem.isMacOS()) {
                try {
                    shared = (XJApplication)Class.forName("edu.usfca.xj.appkit.app.MacOS.XJApplicationMacOS").newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("XJApplication: cannot instanciate the MacOS application ("+e+")");
                }
            }

            if(shared == null)
                shared = new XJApplication();
            
            ((XJApplication)shared).startup();
        }
        return shared;
    }

    public static String getAppVersionShort() {
        return delegate.appVersionShort();
    }

    public static String getAppVersionLong() {
        return delegate.appVersionLong();
    }

    public static XJApplicationDelegate getAppDelegate() {
        return delegate;
    }

    public static Container getActiveContainer() {
        Frame[] frame = Frame.getFrames();
        for (int i = 0; i < frame.length; i++) {
            Frame f = frame[i];
            if(f.isActive() && f.isVisible()) {
                if(f.getSize().width != 0 && f.getSize().height != 0)
                    return f;
            }
        }
        return null;
    }

    public XJApplication() {
        userPrefs = new XJPreferences(getPreferencesClass());
        recentFiles = userPrefs.getList(XJ_PREFS_RECENT_FILES);
        if(recentFiles == null)
            recentFiles = new ArrayList();
        XJMainMenuBar.refreshAllRecentFilesMenu();
    }

    public static void run(XJApplicationDelegate delegate, String[] args, String applicationName) {
        XJApplication.appName = applicationName;

        if(XJSystem.isMacOS())
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", applicationName);

        run(delegate, args);
    }

    public static void run(XJApplicationDelegate delegate, String[] args) {
        setDelegate(delegate);
        launchArguments = args;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                XJApplication.delegate.appDidLaunch(launchArguments);
                new java.util.Timer().schedule(new ScheduledTimer(), 1000, 1000*60*SCHEDULED_TIMER_MINUTES);
                XJApplication.startingUp = false;
            }
        });        
    }

    protected void startup() {
        if(hasPreferencesMenuItem())
            addPreferencesMenuItem();
        else
            removePreferencesMenuItem();
    }

    protected void shutdown() {
        userPrefs.setList(XJ_PREFS_RECENT_FILES, recentFiles);
        System.exit(0);
    }

    public static void setDelegate(XJApplicationDelegate delegate) {
        XJApplication.delegate = delegate;
    }

    public static void addScheduledTimer(XJScheduledTimerDelegate delegate, long minutes, boolean scheduleAtStartup) {
        scheduledTimers.add(new XJScheduledTimer(delegate, minutes, scheduleAtStartup));
    }

    public static void removeScheduledTimer(XJScheduledTimerDelegate delegate) {
        for(int index = scheduledTimers.size()-1; index >= 0; index--) {
            XJScheduledTimer timer = (XJScheduledTimer) scheduledTimers.get(index);
            if(timer.getDelegate() == delegate) {
                scheduledTimers.remove(timer);
            }
        }
    }

    protected static class ScheduledTimer extends TimerTask {

        protected boolean startup = true;

        public void run() {
            for (Iterator iterator = scheduledTimers.iterator(); iterator.hasNext();) {
                XJScheduledTimer timer = (XJScheduledTimer) iterator.next();
                timer.fire(startup, SCHEDULED_TIMER_MINUTES);
            }

            startup = false;
        }
    }

    public static void setPropertiesPath(String path) {
        propertiesPath = path;
    }

    public static String getPropertiesPath() {
        return propertiesPath;
    }

    public XJPreferences getPreferences() {
        return userPrefs;
    }

    public void displayAbout() {
        boolean awake = (about == null);

        if(about == null)
            about = delegate.appInstanciateAboutPanel();

        if(about == null)
            about = new XJAboutBox();

        if(awake) {
            about.setDelegate(this);
            about.awake();
        }
        about.setVisible(true);
    }

    public void displayPrefs() {
        if(prefs == null) {
            try {
                prefs = (XJPanel)delegate.appPreferencesPanelClass().newInstance();
                prefs.setDelegate(this);
                prefs.awake();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Cannot instanciate the Preferences panel: "+e);
                return;
            }
            prefs.center();
        }
        prefs.setVisible(true);
    }

    public void displayHelp() {
        delegate.appShowHelp();
    }

    public void frameDidClose(XJFrame frame) {
        if(frame == prefs)
            prefs = null;
        if(frame == about)
            about = null;
    }

    public void addRecentFile(String file) {
        if(recentFiles.contains(file))
            recentFiles.remove(file);

        if(recentFiles.size() > MAX_RECENT_FILES)
            recentFiles.remove(recentFiles.size()-1);

        recentFiles.add(0, file);
        XJMainMenuBar.refreshAllRecentFilesMenu();
    }

    public void removeRecentFile(String file) {
        recentFiles.remove(file);
        XJMainMenuBar.refreshAllRecentFilesMenu();
    }

    public void clearRecentFiles() {
        recentFiles.clear();
        XJMainMenuBar.refreshAllRecentFilesMenu();
    }

    public List recentFiles() {
        return recentFiles;
    }

    // *** Menu

    protected void addPreferencesMenuItem() {
    }

    protected void removePreferencesMenuItem() {
    }

    public void refreshMainMenuBar() {
        XJMainMenuBar.refreshAllMenuBars();
    }

    // *** XJDocument

    public static void addDocumentType(Class documentClass, Class windowClass, Class dataClass, String ext, String description) {
        documentType.add(new XJDocumentType(documentClass, windowClass, dataClass, ext, description));
    }

    public XJDocumentType getDocumentTypeForPath(String path) {
        Iterator iterator = documentType.iterator();
        while(iterator.hasNext()) {
            XJDocumentType doc = (XJDocumentType)iterator.next();
            if(path.endsWith("."+doc.getExtension()))
                return doc;
        }
        return null;
    }

    public XJDocumentType getDocumentTypeForDataClass(Class dc) {
        Iterator iterator = documentType.iterator();
        while(iterator.hasNext()) {
            XJDocumentType doc = (XJDocumentType)iterator.next();
            if(doc.getDataClass().equals(dc))
                return doc;
        }
        return null;
    }

    public List getDocumentExtensions() {
        List ext = new ArrayList();
        Iterator iterator = documentType.iterator();
        while(iterator.hasNext()) {
            XJDocumentType doc = (XJDocumentType)iterator.next();
            ext.add(doc.getExtension());
        }
        return ext;
    }

    public List getDocumentDescriptions() {
        List descr = new ArrayList();
        Iterator iterator = documentType.iterator();
        while(iterator.hasNext()) {
            XJDocumentType doc = (XJDocumentType)iterator.next();
            descr.add(doc.getDescriptionString());
        }
        return descr;
    }

    public List getDocuments() {
        return documents;
    }

    public XJDocumentType askForDocumentType() {
        if(documentType.size() == 1)
            return (XJDocumentType)documentType.get(0);

        int index = XJAlertInput.showInputDialog(null, XJLocalizable.getXJString("AppNewDocTitle"),
                XJLocalizable.getXJString("AppNewDocMessage"), getDocumentDescriptions(), getDocumentDescriptions().get(0));
        if(index == -1)
            return null;
        else
            return (XJDocumentType)documentType.get(index);
    }

    public XJDocument newDocument(boolean visible, XJDocumentType docType) {
        if(documentType.size() == 0) {
            XJAlert.display(null, XJLocalizable.getXJString("AppNewDocErrTitle"), XJLocalizable.getXJString("AppNewDocErrMessage"));
            return null;
        }

        if(docType == null) {
            docType = askForDocumentType();
            if(docType == null)
                return null;
        }

        XJDocument document;
        try {
            document = (XJDocument)docType.getDocumentClass().newInstance();
            document.setDocumentData((XJData)docType.getDataClass().newInstance());

            XJWindow window = (XJWindow)docType.getWindowClass().newInstance();
            document.setWindow(window);
            window.awake();

            document.awake();
            document.setDocumentFileType(docType.getExtension(), docType.getDescriptionString());

            if(supportsPersistence())
                document.setTitle(XJLocalizable.getXJString("AppUntitled")+" "+documentAbsoluteCount);
            else
                document.setTitle(documentAbsoluteCount > 0 ?appName+" "+documentAbsoluteCount:appName);

            documentAbsoluteCount++;

            document.getWindow().offsetPosition(documentAbsPositionCount*DOCUMENT_OFFSET_PIXELS,
                                                documentAbsPositionCount*DOCUMENT_OFFSET_PIXELS);

            if(document.getWindow().isCompletelyOnScreen())
                documentAbsPositionCount++;
            else
                documentAbsPositionCount = 0;

        } catch(Exception e) {
            e.printStackTrace();
            XJAlert.display(null, XJLocalizable.getXJString("AppNewDocError"), e.toString());
            return null;
        }

        if(visible)
            document.showWindow();

        return document;
    }

    public XJDocument newDocumentOfData(Class dataClass) {
        return newDocument(true, getDocumentTypeForDataClass(dataClass));
    }

    public XJDocument newDocument() {
        return newDocument(true, null);
    }

    public boolean openDocument(String file) {
        if(file == null)
            return false;

        XJDocument document = getDocumentForPath(file);
        if(document != null) {
            document.getWindow().bringToFront();
            return true;
        } else {
            document = newDocument(false, getDocumentTypeForPath(file));
            if(document == null)
                return false;
            else if(document.performLoad(file)) {
                addRecentFile(file);
                document.showWindow();
                closeFirstCreatedWindowIfNonDirty();
                return true;
            } else {
                document.performClose(true);
                return false;
            }
        }
    }

    public boolean openDocument() {
        if(!XJFileChooser.shared().displayOpenDialog(null, getDocumentExtensions(), getDocumentDescriptions(), false))
            return false;

        return openDocument(XJFileChooser.shared().getSelectedFilePath());
    }

    public boolean openLastUsedDocument() {
        if(recentFiles.isEmpty())
            return false;

        String file = (String)recentFiles.get(0);
        while(!new File(file).exists()) {
            recentFiles.remove(0);
            if(recentFiles.isEmpty())
                return false;

            file = (String)recentFiles.get(0);
        }

        if(openDocument(file))
            return true;

        removeRecentFile(file);
        return false;
    }

    public void addDocument(XJDocument document) {
        documents.add(document);
    }

    public void removeDocument(XJDocument document) {
        documents.remove(document);
    }

    public void closeFirstCreatedWindowIfNonDirty() {
        Iterator iterator = documents.iterator();
        while(iterator.hasNext()) {
            XJDocument document = (XJDocument)iterator.next();
            if(document.isFirstDocument() && !document.isDirty() && document.getDocumentPath() == null) {
                document.performClose();
                break;
            }
        }
    }

    public XJDocument getActiveDocument() {
        Iterator iterator = documents.iterator();
        while(iterator.hasNext()) {
            XJDocument document = (XJDocument)iterator.next();
            // Note:
            // A document may have no window associated. For example, ANTLRWorks projects
            // contains a document for each file: these documents don't have a window associated because
            // they are part of the global project's window (which is referenced by
            // the project's document itself)
            if(document.getWindow() != null && document.getWindow().isActive())
                return document;
        }
        return null;
    }

    public XJDocument getDocumentForPath(String path) {
        Iterator iterator = documents.iterator();
        while(iterator.hasNext()) {
            XJDocument document = (XJDocument)iterator.next();
            String docPath = document.getDocumentPath();
            if(docPath != null && docPath.equals(path))
                return document;
        }
        return null;
    }

    public XJWindow getActiveWindow() {
        Iterator iterator = windows.iterator();
        while(iterator.hasNext()) {
            XJWindow window = (XJWindow)iterator.next();
            if(window.isActive())
                return window;
        }
        return null;
    }

    // ** XJWindow

    public void addWindow(XJWindow window) {
        windows.add(window);
    }

    public int getNumberOfNonAuxiliaryWindows() {
        int count = 0;
        for (Iterator iterator = windows.iterator(); iterator.hasNext();) {
            XJWindow window = (XJWindow) iterator.next();
            if(!window.isAuxiliaryWindow())
                count++;
        }
        return count;
    }

    public void removeWindow(XJWindow window) {
        windows.remove(window);
        refreshMainMenuBar();
        if(getNumberOfNonAuxiliaryWindows() == 0 && !startingUp) {
            if(!XJSystem.isMacOS() || shouldQuitAfterLastWindowClosed())
                performQuit();
        }
    }

    public List getWindows() {
        return windows;
    }

    // *** Auto-save feature

    public static void setAutoSave(boolean enabled, int delayInMinutes) {
        if(autoSaveTimer == null)
            autoSaveTimer = new AutoSaveTimer();

        removeScheduledTimer(autoSaveTimer);

        if(enabled)
            addScheduledTimer(autoSaveTimer, delayInMinutes, false);
    }

    public static class AutoSaveTimer implements XJScheduledTimerDelegate {
        public void scheduledTimerFired(boolean startup) {
            for(Iterator iter = documents.iterator(); iter.hasNext();) {
                XJDocument document = (XJDocument)iter.next();
                if(document.isDirty() && document.getDocumentPath() != null) {
                    document.performSave(false);
                }
            }
        }
    }
    
    // *** Events

    public void performPreferences() {
        displayPrefs();
    }

    public void performQuit() {
        delegate.appWillTerminate();
        Iterator iterator = documents.iterator();
        while(iterator.hasNext()) {
            XJDocument document = (XJDocument)iterator.next();
            if(document.performClose())
                iterator = documents.iterator();
            else
                return;
        }
        shutdown();
    }

    // Properties

    public boolean supportsPersistence() {
        if(delegate == null)
            return true;
        else
            return delegate.supportsPersistence();
    }

    public boolean hasPreferencesMenuItem() {
        if(delegate == null)
            return true;
        else
            return delegate.appHasPreferencesMenuItem();
    }

    public boolean shouldQuitAfterLastWindowClosed() {
        if(delegate == null)
            return false;
        else
            return delegate.appShouldQuitAfterLastWindowClosed();
    }

    public Class getPreferencesClass() {
        if(delegate == null)
            return XJApplication.class;
        else
            return delegate.appPreferencesClass();
    }

}
