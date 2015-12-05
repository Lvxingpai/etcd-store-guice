package com.lvxingpai.etcd

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import com.lvxingpai.configuration.Configuration
import scala.concurrent.ExecutionContext
import scala.language.implicitConversions

/**
 * Created by zephyre on 11/18/15.
 */
class EtcdStoreModule(config: Configuration)(implicit executionContext: ExecutionContext) extends AbstractModule {
  override def configure(): Unit = {
    val confProvider = new EtcdConfProvicer(config)
    val serviceProvider = new EtcdServiceProvider(config)

    bind(classOf[Configuration]) annotatedWith Names.named("etcdConf") toProvider confProvider
    bind(classOf[Configuration]) annotatedWith Names.named("etcdService") toProvider serviceProvider

    // Enable variable call-by-name arguments
    // Refer: http://stackoverflow.com/questions/13307418/scala-variable-argument-list-with-call-by-name-possible
    implicit def byname_to_noarg[A](a: => A): (() => A) = () => a

    val defaultProvider = new DefaultProvider(config, confProvider.get, serviceProvider.get)
    bind(classOf[Configuration]) annotatedWith Names.named("etcd") toProvider defaultProvider
  }
}
