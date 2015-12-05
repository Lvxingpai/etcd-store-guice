# etcd-store-guice

## Description

With the help of [Guice](https://github.com/google/guice), which is a dependency injection framework, access Configuration from etcd servers.

## Project Dependencies

```sbt
libraryDependencies ++= Seq(
  "com.lvxingpai" %% "0.1.2"
)
```

## Usage

To access the etcd server, some information should be provided in the default configuration:

```hcon
etcdStore: {
  host: "localhost"
  port: 2379
  serviceKeys: [
    "service1",
    {
      key: "service2"
      alias: "service2-with-alias"
    }
  ],
  confKeys: [
    "conf1",
    {
      key: "conf2",
      alias: "conf2-with-alias"
    }
  ]
}
```

Suppose that the information above can be acquired by:

```scala
val configuration = Configuration.load()
```

The dependency injector can be easily created. Therefore backend services and the configuration can be accessed as follows:

```scala
val injector = Guice.createInjector(new EtcdStoreModule(Configuration.load()))
val conf = injector.getInstance(Key.get(classOf[Configuration], Names.named("etcdConf")))
val services = injector.getInstance(Key.get(classOf[Configuration], Names.named("etcdService")))
```
