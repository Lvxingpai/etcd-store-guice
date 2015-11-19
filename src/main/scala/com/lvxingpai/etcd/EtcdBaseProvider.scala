package com.lvxingpai.etcd

import com.google.inject.Provider
import com.typesafe.config.{ Config, ConfigException, ConfigValueType }

import scala.collection.JavaConversions._
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

/**
 * Created by zephyre on 11/18/15.
 */
abstract class EtcdBaseProvider extends Provider[Config] {
  /**
   * 获得etcd服务器的连接信息
   * @param config 本地配置
   * @return
   */
  protected def getEtcdParams(config: Config): (String, Int, String, Option[EtcdAuth]) = {
    val ret = for {
      host <- Try(config getString "etcdStore.host") recover {
        case _: ConfigException.Missing => "localhost"
      }
      port <- Try(config getInt "etcdStore.port") recover {
        case _: ConfigException.Missing => 2379
      }
      schema <- Try(config getString "etcdStore.schema") recover {
        case _: ConfigException.Missing => "http"
      }
      userOpt <- Try(config getString "etcdStore.user") map Option.apply recover {
        case _: ConfigException.Missing => None
      }
      passwordOpt <- Try(config getString "etcdStore.password") map Option.apply recover {
        case _: ConfigException.Missing => None
      }
    } yield {
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
  protected def getConfKeys(config: Config, path: String): Seq[(String, String)] = {
    // 獲得所有的鍵
    val confKeys = (Try(config getList path) map (_.toSeq) recover {
      case _: ConfigException.Missing => Seq()
    }).get

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
  protected def etcdConf(builder: EtcdBaseBuilder, confKeys: Seq[(String, String)])(implicit executionContext: ExecutionContext): Future[Config] = {
    val finalBuilder = confKeys.foldLeft(builder)((builder, keyPair) => builder.addKeysWithAliases(keyPair))
    finalBuilder.build()
  }
}
