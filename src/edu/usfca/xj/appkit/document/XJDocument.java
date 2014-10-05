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

package edu.usfca.xj.appkit.document;

import edu.usfca.xj.appkit.app.XJApplication;
import edu.usfca.xj.appkit.frame.XJWindow;
import edu.usfca.xj.appkit.utils.XJAlert;
import edu.usfca.xj.appkit.utils.XJFileChooser;
import edu.usfca.xj.appkit.utils.XJLocalizable;
import edu.usfca.xj.foundation.XJObject;
import edu.usfca.xj.foundation.XJUtils;

import java.awt.*;
import java.io.*;

public class XJDocument extends XJObject {

    protected XJData documentData = null;
    protected XJWindow documentWindow = null;
    protected String documentTitle = XJLocalizable.getXJString("DocUntitled");
    protected String documentPath = null;

    protected String documentFileExt = null;
    protected String documentFileExtDescription = null;

    protected boolean dirty = false;
    protected boolean firstDocument = false;
    protected boolean writing = false;

    protected Component javaContainer = null;

    protected static int absoluteCounter = 0;

    public XJDocument() {
        XJApplication.shared().addDocument(this);
        this.firstDocument = absoluteCounter == 0;
        absoluteCounter++;
    }

    // *** Public methods

    public boolean isFirstDocument() {
        return firstDocument;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void changeDone() {
        if(dirty)
            return;

        dirty = true;
        if(documentWindow != null)
            documentWindow.setDirty();
    }

    public void changeReset() {
        dirty = false;
        if(documentWindow != null)
            documentWindow.resetDirty();
    }

    public void setTitle(String title) {
        documentTitle = title;
        if(documentWindow != null)
            documentWindow.setTitle(documentTitle);
    }

    public void setWindow(XJWindow window) {
        documentWindow = window;
        if(documentWindow != null) {
            documentWindow.setDocument(this);
            documentWindow.setTitle(documentTitle);
        }
    }

    public XJWindow getWindow() {
        return documentWindow;
    }

    public void setJavaContainer(Component container) {
        this.javaContainer = container;
    }

    public Component getJavaContainer() {
        if(javaContainer == null)
            return getWindow() == null ? null : getWindow().getJavaContainer();
        else
            return javaContainer;
    }

    public void showWindow() {
        if(documentWindow != null)
            documentWindow.show();
    }

    public void setDocumentData(XJData data) {
        this.documentData = data;
        if(documentData != null) {
            documentData.addObserver(this);
        }
    }

    public XJData getDocumentData() {
        return documentData;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public String getDocumentName() {
        return XJUtils.getLastPathComponent(documentPath);      
    }

    public void setDocumentFileType(String ext, String description) {
        documentFileExt = ext;
        documentFileExtDescription = description;
    }

    public void observeValueForKey(Object sender, String key, Object object) {
        if(!writing)
            changeDone();
    }

    /** Automatically handle external modification detection
     *
     */

    protected long lastModifiedOnDisk = 0;

    public long getDateOfModificationOnDisk() {
        if(getDocumentPath() == null)
            return 0;

        File f = null;
        try {
            f = new File(getDocumentPath());
        } catch(Exception e) {
            // ignore excepton
        }

        if(f == null)
            return 0;
        else
            return f.lastModified();
    }

    public void synchronizeLastModifiedDate() {
        lastModifiedOnDisk = getDateOfModificationOnDisk();
    }

    public boolean isModifiedOnDisk() {
        return lastModifiedOnDisk != getDateOfModificationOnDisk();
    }

    /** General methods
     *
     */

    private boolean performLoad_() {
        if(!dirty)
            return XJApplication.YES;


        int r = XJAlert.displayAlertYESNOCANCEL(getJavaContainer(),
                                                XJLocalizable.getXJString("DocLoad"),
                                                XJLocalizable.getStringFormat("DocSaveChanges",
                                                documentTitle));
        switch(r) {
            case XJAlert.YES:
                return performSave(false);

            case XJAlert.NO:
                return XJApplication.YES;

            case XJAlert.CANCEL:
                return XJApplication.NO;
        }

        return XJApplication.YES;
    }

    public boolean performLoad(String file) {
        documentPath = file;
        try {
            readDocument(documentPath);
        } catch(Exception e) {
            e.printStackTrace();
            XJAlert.display(getJavaContainer(), XJLocalizable.getXJString("DocError"), XJLocalizable.getXJString("DocLoadError")+" "+e.toString());
            return XJApplication.NO;
        }

        setTitle(documentPath);
        changeReset();

        return XJApplication.YES;
    }

    public boolean performLoad() {
        if(!performLoad_())
            return XJApplication.NO;

        if(!XJFileChooser.shared().displayOpenDialog(getJavaContainer(), documentFileExt, documentFileExtDescription, false))
            return XJApplication.NO;

        String path = XJFileChooser.shared().getSelectedFilePath();
        XJDocument document = XJApplication.shared().getDocumentForPath(path);
        if(document != null && document != this) {
            XJAlert.display(getJavaContainer(), XJLocalizable.getXJString("DocError"), XJLocalizable.getXJString("DocLoadExists"));
            return XJApplication.NO;
        } else {
            XJApplication.shared().addRecentFile(path);
            return performLoad(XJFileChooser.shared().getSelectedFilePath());
        }
    }

    public boolean reload() {
        try {
            readDocument(documentPath);
        } catch(Exception e) {
            XJAlert.display(getJavaContainer(), XJLocalizable.getXJString("DocError"), XJLocalizable.getXJString("DocLoadError")+" "+e.toString());
            return XJApplication.NO;
        }
        return XJApplication.YES;
    }

    public boolean performAutoSave() {
        if(getDocumentPath() != null && isDirty())
            return performSave(false);
        else
            return true;
    }

    public boolean performSave(boolean saveAs) {
        if(documentPath == null || saveAs) {
            if(!XJFileChooser.shared().displaySaveDialog(getJavaContainer(), documentFileExt, documentFileExtDescription, true))
                return XJApplication.NO;

            documentPath = XJFileChooser.shared().getSelectedFilePath();
            XJApplication.shared().addRecentFile(documentPath);
        }

        try {
            writeDocument(documentPath);
        } catch(Exception e) {
            e.printStackTrace();
            XJAlert.display(getJavaContainer(), XJLocalizable.getXJString("DocError"), XJLocalizable.getXJString("DocSaveError")+" "+e.toString());
            return XJApplication.NO;
        }

        setTitle(documentPath);        
        changeReset();
        return XJApplication.YES;
    }

    protected boolean performClose_() {
        if(!dirty || !XJApplication.shared().supportsPersistence())
            return XJApplication.YES;

        if(documentWindow != null)
            documentWindow.bringToFront();

        int r = XJAlert.displayAlertYESNOCANCEL(getJavaContainer(), XJLocalizable.getXJString("DocCloseTitle"), XJLocalizable.getStringFormat("DocCloseMessage", documentTitle));
        switch(r) {
            case XJAlert.YES:
                return performSave(false);

            case XJAlert.NO:
                return XJApplication.YES;

            case XJAlert.CANCEL:
                return XJApplication.NO;
        }

        return XJApplication.YES;
    }

    public boolean performClose(boolean force) {
        boolean r = force?XJApplication.YES:performClose_();
        if(r) {
            XJApplication.shared().removeDocument(this);
            if(documentWindow != null)
                documentWindow.close();
        }
        return r;
    }

    public boolean performClose() {
        return performClose(false);
    }

    private void beginWrite() {
        writing = true;
    }

    private void endWrite() {
        writing = false;
    }

    private void writeDocument(String file) throws IOException {
        beginWrite();
        try {
            documentWillWriteData();
            documentData.setFile(file);
            switch(documentData.dataType()) {
                case XJData.DATA_INPUTSTREAM: {
                    OutputStream os = new FileOutputStream(file);
                    documentData.writeData(os);
                    os.close();
                    break;
                }

                case XJData.DATA_OBJECTINPUTSTREAM: {
                    OutputStream os = new FileOutputStream(file);
                    documentData.writeData(new ObjectOutputStream(os));
                    os.close();
                    break;
                }

                case XJData.DATA_PLAINTEXT:
                    documentData.writeData();
                    break;

                case XJData.DATA_XML:
                    documentData.writeData();
                    break;
            }
            synchronizeLastModifiedDate();
        } finally {
            endWrite();            
        }
    }

    private void readDocument(String file) throws IOException, ClassNotFoundException {
        documentWillReadData();
        documentData.setFile(file);
        switch(documentData.dataType()) {
            case XJData.DATA_INPUTSTREAM: {
                InputStream is = new FileInputStream(file);
                documentData.readData(is);
                is.close();
                break;
            }

            case XJData.DATA_OBJECTINPUTSTREAM: {
                InputStream is = new FileInputStream(file);
                documentData.readData(new ObjectInputStream(is));
                is.close();
                break;
            }

            case XJData.DATA_PLAINTEXT:
                documentData.readData();
                break;

            case XJData.DATA_XML:
                documentData.readData();
                break;
        }
        documentDidReadData();
        synchronizeLastModifiedDate();        
    }

    // Subclasses only

    public void documentWillWriteData() {

    }

    public void documentWillReadData() {

    }

    public void documentDidReadData() {
        
    }

}
