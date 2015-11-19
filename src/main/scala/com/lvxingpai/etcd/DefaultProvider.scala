package com.lvxingpai.etcd

import com.google.inject.Provider
import com.typesafe.config.Config

/**
 * Created by zephyre on 11/19/15.
 */
class DefaultProvider(configFuncs: (() => Config)*) extends Provider[Config] {
  lazy val get: Config = {
    val configList = configFuncs map (_())
    configList reduce ((c1, c2) => c1 withFallback c2)
  }
}

