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

package edu.usfca.vas.app;

import edu.usfca.vas.data.DataAbstract;
import edu.usfca.vas.data.DataWrapperAbstract;
import edu.usfca.vas.window.WindowAbstract;
import edu.usfca.xj.appkit.document.XJDocument;

import java.util.Iterator;

public class Document extends XJDocument {

    public Document() {
    }

    public void awake() {
        getAutomataWindow().setStateString(Localized.getString("automataReady"));
    }

    public WindowAbstract getAutomataWindow() {
        return (WindowAbstract)getWindow();
    }

    public DataAbstract getAutomataData() {
        return (DataAbstract)getDocumentData();
    }

    // *** XJDocument methods

    public void documentWillWriteData() {
        getAutomataWindow().documentWillWriteData();
    }

    public void documentWillReadData() {
        getAutomataData().clear();
        getAutomataWindow().clear();
    }

    public void documentDidReadData() {
        int lastSelectedIndex = getAutomataData().getCurrentWrapperIndex();

        getAutomataWindow().removeAllMachine();

        Iterator iterator = getAutomataData().getWrappers().iterator();
        while(iterator.hasNext()) {
            getAutomataWindow().createNewMachine((DataWrapperAbstract)iterator.next());
        }

        getAutomataWindow().selectWindowMachineAtIndex(lastSelectedIndex);

        getAutomataWindow().documentDidReadData();
    }
}
