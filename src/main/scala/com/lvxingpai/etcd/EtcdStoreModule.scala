package com.lvxingpai.etcd

import com.google.inject.{ Guice, AbstractModule }
import com.google.inject.name.Names
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext

/**
 * Created by zephyre on 11/18/15.
 */
class EtcdStoreModule(config: Config)(implicit executionContext: ExecutionContext) extends AbstractModule {
  override def configure(): Unit = {
    val confProvider = new EtcdConfProvicer(config)
    val serviceProvider = new EtcdServiceProvider(config)
    bind(classOf[Config]) annotatedWith Names.named("etcdConf") toProvider confProvider
    bind(classOf[Config]) annotatedWith Names.named("etcdService") toProvider serviceProvider
  }
}
