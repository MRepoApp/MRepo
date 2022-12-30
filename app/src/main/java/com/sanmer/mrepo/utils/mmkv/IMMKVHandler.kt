package com.sanmer.mrepo.utils.mmkv

import com.tencent.mmkv.MMKVHandler
import com.tencent.mmkv.MMKVLogLevel
import com.tencent.mmkv.MMKVRecoverStrategic
import timber.log.Timber

class IMMKVHandler : MMKVHandler {
    override fun onMMKVCRCCheckFail(mmapID: String?): MMKVRecoverStrategic {
        return MMKVRecoverStrategic.OnErrorDiscard
    }

    override fun onMMKVFileLengthError(mmapID: String?): MMKVRecoverStrategic {
        return MMKVRecoverStrategic.OnErrorDiscard
    }

    override fun wantLogRedirecting(): Boolean {
        return true
    }

    override fun mmkvLog(
        level: MMKVLogLevel?,
        file: String?,
        line: Int,
        function: String?,
        message: String?
    ) {
        val log = "<$file:$line::$function> $message"
        when(level) {
            MMKVLogLevel.LevelNone -> Timber.v(log)
            MMKVLogLevel.LevelInfo -> Timber.i(log)
            MMKVLogLevel.LevelDebug -> Timber.d(log)
            MMKVLogLevel.LevelWarning -> Timber.w(log)
            MMKVLogLevel.LevelError ->  Timber.e(log)
            else -> Timber.d(log)
        }
    }
}