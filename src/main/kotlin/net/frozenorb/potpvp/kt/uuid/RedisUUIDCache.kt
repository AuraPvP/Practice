package net.frozenorb.potpvp.kt.uuid

import net.frozenorb.potpvp.PotPvPND
import java.util.*

class RedisUUIDCache : UUIDCache() {

    override fun fetchCache(): Map<UUID, String> {
        return PotPvPND.getInstance().redis
            .runRedisCommand { redis -> redis.hgetAll(KEY) }
            .mapKeys { entry -> UUID.fromString(entry.key) }
    }

    override fun updateCache(uuid: UUID, name: String) {
        PotPvPND.getInstance().redis.runRedisCommand { redis -> redis.hset(KEY, uuid.toString(), name) }
    }

    companion object {
        private const val KEY = "Cubed:UUIDCache"
    }

}