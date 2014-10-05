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

import edu.usfca.vas.data.DataWrapperAbstract;
import edu.usfca.xj.appkit.frame.XJFrame;
import edu.usfca.xj.appkit.gview.GView;
import edu.usfca.xj.appkit.gview.GViewDelegate;
import edu.usfca.xj.appkit.gview.object.GElement;

import javax.swing.*;

public abstract class WindowMachineAbstract extends JPanel implements GViewDelegate {

    protected WindowAbstract window = null;
    protected DataWrapperAbstract dataWrapper = null;

    protected GView graphicView = null;
    protected XJFrame parent;

    public WindowMachineAbstract(XJFrame parent) {
        this.parent = parent;
    }

    public abstract void init();

    public void setWindow(WindowAbstract window) {
        this.window = window;
    }

    public WindowAbstract getWindow() {
        return window;
    }
    
    public void setDataWrapper(DataWrapperAbstract wrapper) {
        this.dataWrapper = wrapper;
    }

    public DataWrapperAbstract getDataWrapper() {
        return dataWrapper;
    }

    public void setGraphicPanel(GView graphicView) {
        this.graphicView = graphicView;
    }

    public GView getGraphicPanel() {
        return graphicView;
    }

    public void clear() {
        graphicView.repaint();
    }

    public void rebuild() {
        getGraphicPanel().createMagnetics();
    }

    public void displaySettings() {

    }

    // *** Delegate

    public void setMagnetics(int horizontal, int vertical) {
        getDataWrapper().setMagnetics(horizontal, vertical);
        getGraphicPanel().createMagnetics();
    }

    public int getHorizontalMagnetics() {
        return getDataWrapper().getHorizontalMagnetics();
    }

    public int getVerticalMagnetics() {
        return getDataWrapper().getVerticalMagnetics();
    }

    public void changeOccured() {
        window.changeOccured();
    }

    public void contextualHelp(GElement element) {
        window.contextualHelp(element);
    }

    public boolean supportsOverlay() {
        return false;
    }

    public boolean isOverlayVisible() {
        return false;
    }

    public void toggleOverlayVisibility() {
    }
}
