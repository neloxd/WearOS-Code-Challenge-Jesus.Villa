package com.jesusvilla.test.base.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class ConnectionUtil(var mContext: Context) : LifecycleObserver {

    companion object NetworkType {

        /**
         * Indicates this network uses a Cellular transport.
         */
        const val TRANSPORT_CELLULAR = 0

        /**
         * Indicates this network uses a Wi-Fi transport.
         */
        const val TRANSPORT_WIFI = 1
    }

    private var mConnectivityMgr: ConnectivityManager? = null
    private var mIsConnected = false
    private var mConnectionMonitor: ConnectionMonitor? = null

    /**
     * Indicates there is no available network.
     */

    fun interface ConnectionStateListener {
        fun onAvailable(isAvailable: Boolean)
    }

    init {
        mConnectivityMgr =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        (mContext as AppCompatActivity?)!!.lifecycle.addObserver(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mConnectionMonitor = ConnectionMonitor()
            val networkRequest =
                NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()
            mConnectivityMgr!!.registerNetworkCallback(networkRequest, mConnectionMonitor!!)
        }
    }

    /**
     * Returns true if connected to the internet, and false otherwise
     *
     * NetworkInfo is deprecated in API 29
     * https://developer.android.com/reference/android/net/NetworkInfo
     *
     * getActiveNetworkInfo() is deprecated in API 29
     * https://developer.android.com/reference/android/net/ConnectivityManager#getActiveNetworkInfo()
     *
     * getNetworkInfo(int) is deprecated as of API 23
     * https://developer.android.com/reference/android/net/ConnectivityManager#getNetworkInfo(int)
     */
    @Suppress("NestedBlockDepth")
    fun isOnline(): Boolean {
        mIsConnected = false
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            var activeNetwork: NetworkInfo? = null
            if (mConnectivityMgr != null) {
                activeNetwork = mConnectivityMgr!!.activeNetworkInfo
            }
            mIsConnected = activeNetwork != null
        } else {
            val allNetworks = mConnectivityMgr!!.allNetworks
            for (network in allNetworks) {
                val networkCapabilities = mConnectivityMgr!!.getNetworkCapabilities(network)
                if (networkCapabilities != null) {
                    val hasInternetCapability =
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    val isValidatedCapability =
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    val hasWifiTransport =
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    val hasCellularTransport =
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    val hasEthernetTransport =
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    if (hasInternetCapability &&
                        isValidatedCapability &&
                        (
                            hasWifiTransport ||
                                hasCellularTransport ||
                                hasEthernetTransport
                            )
                    ) {
                        mIsConnected = true
                    }
                }
            }
        }
        return mIsConnected
    }

    /**
     * Returns:
     *
     * NO_NETWORK_AVAILABLE >>> when you're offline
     * TRANSPORT_CELLULAR >> When Cellular is the active network
     * TRANSPORT_WIFI >> When Wi-Fi is the Active network
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun getAvailableNetworksCount(): Int {
        var count = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val allNetworks = mConnectivityMgr!!.allNetworks // added in API 21 (Lollipop)
            for (network in allNetworks) {
                val networkCapabilities = mConnectivityMgr!!.getNetworkCapabilities(network)
                if (networkCapabilities != null && (
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    )
                ) {
                    count++
                }
            }
        }
        return count
    }

    fun getAvailableNetworks(): List<Int> {
        val activeNetworks: MutableList<Int> = ArrayList()
        val allNetworks: Array<Network> =
            mConnectivityMgr!!.allNetworks // added in API 21 (Lollipop)
        for (network in allNetworks) {
            val networkCapabilities = mConnectivityMgr!!.getNetworkCapabilities(network)
            if (networkCapabilities != null) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) activeNetworks.add(
                    TRANSPORT_WIFI
                )
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) activeNetworks.add(
                    TRANSPORT_CELLULAR
                )
            }
        }
        return activeNetworks
    }

    fun onInternetStateListener(listener: ConnectionStateListener) {
        mConnectionMonitor!!.setOnConnectionStateListener(listener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        (mContext as AppCompatActivity?)!!.lifecycle.removeObserver(this)
        if (mConnectionMonitor != null) mConnectivityMgr!!.unregisterNetworkCallback(
            mConnectionMonitor!!
        )
    }

    inner class NetworkStateReceiver(var mListener: ConnectionStateListener) : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.extras != null) {
                val activeNetworkInfo: NetworkInfo? =
                    mConnectivityMgr?.getActiveNetworkInfo() // deprecated in API 29

                /*
                 * activeNetworkInfo.getState() deprecated in API 28
                 * NetworkInfo.State.CONNECTED deprecated in API 29
                 * */if (!mIsConnected &&
                    activeNetworkInfo != null &&
                    activeNetworkInfo.state == NetworkInfo.State.CONNECTED
                ) {
                    mIsConnected = true
                    mListener.onAvailable(true)
                } else if (intent.getBooleanExtra(
                        ConnectivityManager.EXTRA_NO_CONNECTIVITY, java.lang.Boolean.FALSE
                    ) && !isOnline()
                ) {
                    mListener.onAvailable(false)
                    mIsConnected = false
                }
            }
        }
    }

    inner class ConnectionMonitor : ConnectivityManager.NetworkCallback() {
        private var mConnectionStateListener: ConnectionStateListener? = null
        fun setOnConnectionStateListener(connectionStateListener: ConnectionStateListener?) {
            mConnectionStateListener = connectionStateListener
        }

        override fun onAvailable(network: Network) {
            if (mIsConnected) return
            if (mConnectionStateListener != null) {
                mConnectionStateListener!!.onAvailable(true)
                mIsConnected = true
            }
        }

        override fun onLost(network: Network) {
            if (getAvailableNetworksCount() == 0) {
                mConnectionStateListener?.onAvailable(false)
                mIsConnected = false
            }
        }
    }
}
