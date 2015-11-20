package com.lvxingpai.etcd

import com.google.inject.Provider
import com.lvxingpai.configuration.Configuration

/**
 * Created by zephyre on 11/19/15.
 */
class DefaultProvider(configFuncs: (() => Configuration)*) extends Provider[Configuration] {
  lazy val get: Configuration = {
    val configList = configFuncs map (_())
    configList reduce ((c1, c2) => c1 ++ c2)
  }
}

