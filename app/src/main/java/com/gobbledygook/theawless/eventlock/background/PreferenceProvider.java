package com.gobbledygook.theawless.eventlock.background;

import com.crossbowffs.remotepreferences.RemotePreferenceProvider;
import com.gobbledygook.theawless.eventlock.helper.PreferenceConstants;


public class PreferenceProvider extends RemotePreferenceProvider {
    public PreferenceProvider() {
        super(PreferenceConstants.authority, new String[]{PreferenceConstants.preferences});
    }
}
