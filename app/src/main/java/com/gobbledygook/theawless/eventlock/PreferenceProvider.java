package com.gobbledygook.theawless.eventlock;

import com.crossbowffs.remotepreferences.RemotePreferenceProvider;


public class PreferenceProvider extends RemotePreferenceProvider {
    public PreferenceProvider() {
        super(PreferenceConsts.authority, new String[]{PreferenceConsts.preferences});
    }
}
