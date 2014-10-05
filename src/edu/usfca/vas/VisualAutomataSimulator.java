package edu.usfca.vas;

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


import edu.usfca.vas.app.*;
import edu.usfca.vas.data.DataFA;
import edu.usfca.vas.data.DataTM;
import edu.usfca.vas.window.batch.BatchTestTM;
import edu.usfca.vas.window.fa.WindowFA;
import edu.usfca.vas.window.tm.WindowTM;
import edu.usfca.xj.appkit.app.XJApplication;
import edu.usfca.xj.appkit.app.XJApplicationDelegate;
import edu.usfca.xj.appkit.frame.XJPanel;
import edu.usfca.xj.appkit.utils.XJAlert;
import edu.usfca.xj.appkit.update.XJUpdateManager;
import edu.usfca.xj.foundation.XJSystem;

import javax.swing.*;

public class VisualAutomataSimulator extends XJApplicationDelegate {

    public Class appPreferencesPanelClass() {
        return Preferences.class;
    }

    public XJPanel appInstanciateAboutPanel() {
        return new About();
    }

    public boolean appHasPreferencesMenuItem() {
        return true;
    }

    public boolean appShouldQuitAfterLastWindowClosed() {
        return false;
    }

    public Class appPreferencesClass() {
        return VisualAutomataSimulator.class;
    }

    public void appShowHelp() {
        XJAlert.display(null, Localized.getString("waHelpTitle"),
                        Localized.getString("waHelpMessage"));
    }

    public static void main(String[] args) {
        if (args.length == 7 && args[0].equals("-t")) {
            new Test(Integer.parseInt(args[1]), Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]), Integer.parseInt(args[4]),
                    Integer.parseInt(args[5]), Integer.parseInt(args[6]));
            return;
        } else if (args.length == 1 && args[0].equals("-b")) {
            new BatchTestTM();
            return;
        }

        XJApplication.run(new VisualAutomataSimulator(), args);
    }

    public void appDidLaunch(String[] args) {
        XJApplication.setPropertiesPath("edu/usfca/vas/properties/");
        XJApplication.addDocumentType(Document.class, WindowFA.class, DataFA.class, "fa", Localized.getString("documentFADescription"));
        XJApplication.addDocumentType(Document.class, WindowTM.class, DataTM.class, "tm", Localized.getString("documentTMDescription"));

        switch (Preferences.getStartupAction()) {
            case Preferences.STARTUP_DO_NOTHING:
                // Do nothing only on Mac OS (because the menu bar is always visible)
                if(XJSystem.isMacOS())
                    break;
            case Preferences.STARTUP_NEW_FA_DOC:
                XJApplication.shared().newDocumentOfData(DataFA.class);
                break;
            case Preferences.STARTUP_NEW_TM_DOC:
                XJApplication.shared().newDocumentOfData(DataTM.class);
                break;
            case Preferences.STARTUP_OPEN_LAST_DOC:
                if (!XJApplication.shared().openLastUsedDocument())
                    XJApplication.shared().newDocument();
                break;
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                checkForUpdates(true);
            }
        });
    }

    public static void checkForUpdates(boolean automatic) {
        String url;
        if(XJSystem.isMacOS())
            url = Localized.getString("UpdateOSXXMLURL");
        else
            url = Localized.getString("UpdateXMLURL");
        
        XJUpdateManager um = new XJUpdateManager(null, null);
        um.checkForUpdates(Localized.getString("AppVersionShort"),
                           url,
                           System.getProperty("user.home"),
                           automatic);
    }

}
