package com.lvxingpai.etcd

import com.google.inject.Injector

/**
 * 管理已有的Injector
 *
 * Created by zephyre on 11/18/15.
 */
object InjectorManager {
  private val injectorMap = scala.collection.mutable.Map[String, Injector]()

  def get(name: String): Option[Injector] = injectorMap.get(name)

  def put(name: String, injector: Injector): Unit = injectorMap.put(name, injector)
}
