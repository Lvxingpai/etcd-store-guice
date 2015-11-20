package com.lvxingpai.etcd

import com.google.inject.Provider
import com.lvxingpai.configuration.Configuration
import com.typesafe.config.ConfigValueType

import scala.collection.JavaConversions._
import scala.concurrent.{ ExecutionContext, Future }

/**
 * Created by zephyre on 11/18/15.
 */
abstract class EtcdBaseProvider extends Provider[Configuration] {
  /**
   * 获得etcd服务器的连接信息
   * @param config 本地配置
   * @return
   */
  protected def getEtcdParams(config: Configuration): (String, Int, String, Option[EtcdAuth]) = {
    val ret = for {
      host <- config getString "etcdStore.host" orElse Some("localhost")
      port <- config getInt "etcdStore.port" orElse Some(2379)
      schema <- config getString "etcdStore.schema" orElse Some("http")
    } yield {
      val userOpt = config getString "etcdStore.user"
      val passwordOpt = config getString "etcdStore.password"
      // etcd服務器的連接加密設置
      val auth = for {
        user <- userOpt
        password <- passwordOpt
      } yield {
        EtcdAuth(user, password)
      }
      (host, port, schema, auth)
    }
    ret.get
  }

  /**
   * 通過讀取本地配置的內容, 獲得etcd builder所需的key列表
   * @return
   */
  protected def getConfKeys(config: Configuration, path: String): Seq[(String, String)] = {
    // 獲得所有的鍵
    val confKeys = config getList path map (_.toSeq) getOrElse Seq()

    confKeys map (confValue => {
      confValue.valueType() match {
        case ConfigValueType.STRING =>
          val key = confValue.unwrapped().toString
          key -> key
        case ConfigValueType.OBJECT =>
          val tmp = confValue atKey "root"
          val key = tmp getString "root.key"
          val alias = tmp getString "root.alias"
          key -> alias
        case _ =>
          null
      }
    }) filter (_ != null)
  }

  /**
   * 从etcd获得builder的信息
   * @return
   */
  protected def etcdConf(builder: EtcdBaseBuilder, confKeys: Seq[(String, String)])(implicit executionContext: ExecutionContext): Future[Configuration] = {
    val finalBuilder = confKeys.foldLeft(builder)((builder, keyPair) => builder.addKeysWithAliases(keyPair))
    finalBuilder.build()
  }
}
