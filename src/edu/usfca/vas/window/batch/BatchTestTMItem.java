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

public class BatchTestTMItem {

    public String file;
    public int machineCount;
    public int stepCount;
    public String tapeContent;
    public String result;
    public boolean failed;

    public BatchTestTMItem(String file) {
        this.file = file;
    }

    public static String getValueName(int col) {
        switch(col) {
            case 0:
                return Localized.getString("tmBatchItemFile");
            case 1:
                return Localized.getString("tmBatchItemMachineCount");
            case 2:
                return Localized.getString("tmBatchItemStepCount");
            case 3:
                return Localized.getString("tmBatchItemTapeContent");
            case 4:
                return Localized.getString("tmBatchItemStatus");
        }
        return "";
    }

    public void setValue(int col, String value) {
        switch(col) {
            case 0:
                file = value;
                break;
            case 1:
                tapeContent = value;
                break;
        }
    }

    public String getValue(int col) {
        switch(col) {
            case 0:
                return file;
            case 1:
                return String.valueOf(machineCount);
            case 2:
                return String.valueOf(stepCount);
            case 3:
                return tapeContent;
            case 4:
                return result;
        }
        return "";
    }
}
