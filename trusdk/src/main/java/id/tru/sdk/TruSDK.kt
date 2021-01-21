/*
 * MIT License
 * Copyright (C) 2020 4Auth Limited. All rights reserved
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package id.tru.sdk

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import id.tru.sdk.network.Client
import java.io.IOException

/**
 * TruSDK main entry point.
 *
 * Usage example
 * ```
 * TruSDK.initializeSdk(requireContext())
 * private val truSdk = TruSDK.getInstance()
 *
 * truSdk.openCheckUrl(checkUrl)
 * ```
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class TruSDK private constructor(context: Context) {
    private val client = Client(context)

    /**
     * Execute a phone check verification, by performing a network request against the Mobile carrier
     * over mobile data connection.
     *
     * Invokes the request immediately, and blocks until the response can be processed or is in error.
     *
     * Prerequisites:
     * Get the mobile application user's phone number and create a PhoneCheck via the tru.ID API
     * in order to receive a unique `check_url` in the response.
     * Request the `check_url` on the mobile device over the mobile data connection.
     *
     * @param checkUrl The phone check url.
     *
     * @WorkerThread
     * @throws IOException if the request could not be executed due to cancellation, a connectivity
     *     problem or timeout. Because networks can fail during an exchange, it is possible that the
     *     remote server accepted the request before the failure.
     * @throws IllegalStateException when the call has already been executed.
     */
    @Throws(java.io.IOException::class)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun openCheckUrl(@NonNull checkUrl: String) {

        Log.i(TAG, "Triggering check url")
        client.requestSync(checkUrl, method = "GET")
    }

    companion object {
        private const val TAG = "TruSDK"
        private var instance: TruSDK? = null

        @Synchronized
        fun initializeSdk(context: Context): TruSDK {
            var currentInstance = instance
            if (null == currentInstance) {
                currentInstance = TruSDK(context)
            }
            instance = currentInstance
            return currentInstance
        }

        @Synchronized
        fun getInstance(): TruSDK {
            val currentInstance = instance
            checkNotNull(currentInstance) {
                TruSDK::class.java.simpleName +
                        " is not initialized, call initializeSdk(...) first"
            }
            return currentInstance
        }
    }
}