package gov.keralapolice.railmaithri.services

import android.content.Context
import android.net.ConnectivityManager
import java.security.AccessControlContext

object  Network {
    internal fun isAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = cm.activeNetworkInfo
        return activeNetworkInfo?.isConnectedOrConnecting ?: false
    }
}