package bruhcollective.itaysonlab.jetibox

import android.app.Application
import com.markodevcic.peko.PermissionRequester
import com.microsoft.xalwrapper.XalApplication
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class JBApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this, "${filesDir.absolutePath}/jbx_data")
        XalApplication.getInstance().Initialize(applicationContext, applicationContext.filesDir.path)
        PermissionRequester.initialize(applicationContext)
    }
}