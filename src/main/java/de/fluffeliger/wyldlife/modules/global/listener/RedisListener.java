package de.fluffeliger.wyldlife.modules.global.listener;

import io.lettuce.core.pubsub.RedisPubSubListener;
import org.bukkit.Bukkit;

public class RedisListener implements RedisPubSubListener<String, String> {

    @Override
    public void message(String s, String s2) {
        Bukkit.getLogger().warning("Got " + s + " on channel" + s2);
    }

    @Override
    public void message(String s, String k1, String s2) {

    }

    @Override
    public void subscribed(String s, long l) {

    }

    @Override
    public void psubscribed(String s, long l) {

    }

    @Override
    public void unsubscribed(String s, long l) {

    }

    @Override
    public void punsubscribed(String s, long l) {

    }
}
