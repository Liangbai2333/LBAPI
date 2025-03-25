package site.liangbai.lbapi.serverbridge


// TODO 名字，每个服务器注册后通知同一channel有新服务器加入，用于后续实现同步处理
data class RedisConfig(
    val host: String,
    val port: Int,
    val password: String? = null,
    val auth: String? = null,
    val prefix: String
)
