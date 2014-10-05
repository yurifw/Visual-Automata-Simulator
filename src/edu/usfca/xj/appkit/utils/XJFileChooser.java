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

package edu.usfca.xj.appkit.utils;

import edu.usfca.xj.appkit.app.XJApplication;
import edu.usfca.xj.appkit.frame.XJDialog;
import edu.usfca.xj.foundation.XJUtils;
import edu.usfca.xj.foundation.XJSystem;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XJFileChooser {

    private static XJFileChooser shared = new XJFileChooser();

    private List selectedFilePaths = null;
    private String selectedFilePath = null;

    static {
        if(XJSystem.isMacOS()) {
            // Allow traversal of bundle on Mac OS X
            UIManager.put("JFileChooser.appBundleIsTraversable", "always");
            UIManager.put("JFileChooser.packageIsTraversable", "always");
        }
    }

    public static XJFileChooser shared() {
        return shared;
    }

    public boolean displayOpenDialog(Component parent, boolean multiple) {
        return displayOpenDialog(parent, (List)null, (List)null, false);
    }

    public boolean displayOpenDialog(Component parent, String extension, String description, boolean multiple) {
        List extensions = new ArrayList();
        extensions.add(extension);
        List descriptions = new ArrayList();
        descriptions.add(description);
        return displayOpenDialog(parent, extensions, descriptions, multiple);
    }

    public boolean displayOpenDialog(Component parent, List extensions, List descriptions, boolean multiple) {
        if(parent != null)
            parent = XJDialog.resolveOwner(parent.getParent());

        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(multiple);
        if(extensions == null || extensions.size() == 0)
            chooser.setAcceptAllFileFilterUsed(true);
        else {
            for(int i=0; i<extensions.size(); i++) {
                XJFileFilter ff = XJFileFilter.createFileFilter((String)extensions.get(i),
                        (String)descriptions.get(i));
                chooser.addChoosableFileFilter(ff);
                if(extensions.size() == 1 && i == 0)
                    chooser.setFileFilter(ff);
            }
            if(extensions.size() > 1)
                chooser.setFileFilter(chooser.getAcceptAllFileFilter());
        }

        if(selectedFilePath != null)
            chooser.setCurrentDirectory(new File(selectedFilePath));

        if(chooser.showOpenDialog(parent==null?null:parent) == JFileChooser.APPROVE_OPTION) {
            selectedFilePath = chooser.getSelectedFile().getAbsolutePath();
            selectedFilePaths = filesToList(chooser.getSelectedFiles());
            if(extensions != null && extensions.size() >= 0) {
                FileFilter ff = chooser.getFileFilter();
                if(ff instanceof XJFileFilter) {
                    XJFileFilter filter = (XJFileFilter)ff;
                    if(!selectedFilePath.endsWith("."+filter.getExtension()))
                        selectedFilePath += "."+filter.getExtension();
                }
            }

            return XJApplication.YES;
        } else
            return XJApplication.NO;
    }

    private List filesToList(File[] files) {
        List array = new ArrayList();
        for(int i=0; i<files.length; i++)
            array.add(files[i].getAbsolutePath());
        return array;
    }

    public boolean displaySaveDialog(Component parent, String extension, String extensionDescription, boolean acceptAll) {
        return displaySaveDialog(parent, Collections.singletonList(extension),
                Collections.singletonList(extensionDescription), acceptAll);
    }

    public boolean displaySaveDialog(Component parent, List extensions, List descriptions, boolean acceptAll) {
        if(parent != null)
            parent = XJDialog.resolveOwner(parent.getParent());

        JFileChooser chooser = new JFileChooser();
        if(extensions == null || extensions.size() == 0)
            chooser.setAcceptAllFileFilterUsed(false);
        else {
            chooser.setAcceptAllFileFilterUsed(acceptAll);
            XJFileFilter firstFF = null;
            for(int i=0; i<extensions.size(); i++) {
                XJFileFilter ff = XJFileFilter.createFileFilter((String)extensions.get(i),
                        (String)descriptions.get(i));
                chooser.addChoosableFileFilter(ff);
                if((extensions.size() == 1 || !acceptAll) && i == 0)
                    firstFF = ff;
            }
            if(extensions.size() > 1 && acceptAll)
                chooser.setFileFilter(chooser.getAcceptAllFileFilter());
            else
                chooser.setFileFilter(firstFF);
        }

        if(selectedFilePath != null)
            chooser.setCurrentDirectory(new File(selectedFilePath));

        if(chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            selectedFilePath = chooser.getSelectedFile().getAbsolutePath();
            selectedFilePaths = filesToList(chooser.getSelectedFiles());
            if(extensions != null && extensions.size() >= 0) {
                FileFilter ff = chooser.getFileFilter();
                if(ff instanceof XJFileFilter) {
                    XJFileFilter filter = (XJFileFilter)ff;
                    if(!selectedFilePath.endsWith("."+filter.getExtension()))
                        selectedFilePath += "."+filter.getExtension();
                }
            }
            if(new File(selectedFilePath).exists()) {
                String name = XJUtils.getLastPathComponent(selectedFilePath);
                if(XJAlert.displayAlert(parent, "Warning",
                        "The file '"+name+"' already exists. Do you want to replace it?",
                        "Cancel",
                        "Replace",
                        1) == 0)
                    return XJApplication.NO;
            }
            return XJApplication.YES;
        } else
            return XJApplication.NO;
    }

    public boolean displayChooseDirectory(Component parent) {
        if(parent != null)
            parent = XJDialog.resolveOwner(parent.getParent());

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        boolean result;
        if(chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            selectedFilePath = chooser.getSelectedFile().getAbsolutePath();
            result = XJApplication.YES;
        } else
            result = XJApplication.NO;

        if(parent instanceof JDialog) {
            /** Make sure that if the parent is a modal dialog, it is back to
             * front: by default, Swing doesn't bring back the dialog to front.
             */

            JDialog dialog = (JDialog)parent;
            if(dialog.isModal())
                dialog.toFront();
        }
        return result;
    }

    public String getSelectedFilePath() {
        return selectedFilePath;
    }

    public List getSelectedFilePaths() {
        return selectedFilePaths;
    }
}
