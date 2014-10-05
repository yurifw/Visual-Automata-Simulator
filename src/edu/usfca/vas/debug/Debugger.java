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

package edu.usfca.vas.debug;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Debugger {

    private java.util.List historyElements = new ArrayList();

    public void reset() {
        setHistoryColor(null);
        setHistoryPenSize(1);

        historyElements.clear();
    }

    public void addHistory(DebugWrapper wrapper) {
        historyElements.add(wrapper);
    }

    public void setHistoryColor(Color color) {
        Iterator iterator = historyElements.iterator();
        while(iterator.hasNext()) {
            DebugWrapper element = (DebugWrapper)iterator.next();
            element.setColor(color);
        }
    }

    public void setHistoryPenSize(int size) {
        Iterator iterator = historyElements.iterator();
        while(iterator.hasNext()) {
            DebugWrapper element = (DebugWrapper)iterator.next();
            element.setPenSize(size);
        }
    }

    public void setHistoryGradientColor(Color color, Color startColor) {
        double i = 1.0/(historyElements.size()-1);
        double f = 0.0;
        Iterator iterator = historyElements.iterator();
        while(iterator.hasNext()) {
            DebugWrapper element = (DebugWrapper)iterator.next();
            Color cc = new Color((int)(color.getRed()*f), (int)(color.getGreen()*f), (int)(color.getBlue()*f));
            if(f == 0)
                element.setColor(color);
            else
                element.setColor(cc);
            f += i;
        }
    }
}
