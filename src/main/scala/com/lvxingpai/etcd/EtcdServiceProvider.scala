package com.lvxingpai.etcd

import com.google.inject.Inject
import com.lvxingpai.configuration.Configuration
import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext }

/**
 * Created by zephyre on 11/18/15.
 */
class EtcdServiceProvider @Inject() (config: Configuration)(implicit val executionContext: ExecutionContext) extends EtcdBaseProvider {
  lazy val get: Configuration = {
    val (host, port, schema, auth) = getEtcdParams(config)
    val builder = new EtcdServiceBuilder(host, port, schema, auth)
    val confKeys = getConfKeys(config, "etcdStore.serviceKeys")
    val confFuture = etcdConf(builder, confKeys)
    Await.result(confFuture, Duration.Inf)
  }
}
