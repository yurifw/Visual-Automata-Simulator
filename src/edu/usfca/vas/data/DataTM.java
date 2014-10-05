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

package edu.usfca.vas.data;

import edu.usfca.vas.window.tm.TapeConfiguration;

import java.util.ArrayList;
import java.util.List;

public class DataTM extends DataAbstract {

    public static final String KEY_TAPE_CONFIGS = "KEY_TAPE_CONFIGS";
    public static final String KEY_DEFAULT_CONFIG = "KEY_DEFAULT_CONFIG";

    public List getTapeConfigurations() {
        List configs = (List)getDataForKey(KEY_TAPE_CONFIGS);
        if(configs == null) {
            configs = new ArrayList();
            setDataForKey(this, KEY_TAPE_CONFIGS, configs);
        }
        return configs;
    }

    public void setDefaultConfiguration(TapeConfiguration configuration) {
        setDataForKey(this, KEY_DEFAULT_CONFIG, configuration);
    }

    public TapeConfiguration getDefaultConfiguration() {
        return (TapeConfiguration)getDataForKey(KEY_DEFAULT_CONFIG);
    }

}
