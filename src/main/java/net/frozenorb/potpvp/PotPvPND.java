package net.frozenorb.potpvp;

import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mongodb.MongoClientURI;
import com.qrakn.morpheus.Morpheus;
import net.frozenorb.potpvp.divisions.DivisionHandler;
import net.frozenorb.potpvp.hologram.HologramHandler;
import net.frozenorb.potpvp.kittype.HealingMethod;
import net.frozenorb.potpvp.kittype.menu.HealingMethodParameterType;
import net.frozenorb.potpvp.kt.menu.ButtonListeners;
import net.frozenorb.potpvp.kt.protocol.InventoryAdapter;
import net.frozenorb.potpvp.kt.protocol.LagCheck;
import net.frozenorb.potpvp.kt.protocol.PingAdapter;
import net.frozenorb.potpvp.kt.tab.TabAdapter;
import net.frozenorb.potpvp.kt.uuid.RedisUUIDCache;
import net.frozenorb.potpvp.kt.redis.RedisCredentials;
import net.frozenorb.potpvp.kt.command.CommandHandler;
import net.frozenorb.potpvp.kt.nametag.NametagEngine;
import net.frozenorb.potpvp.kt.redis.Redis;
import net.frozenorb.potpvp.kt.scoreboard.ScoreboardEngine;
import net.frozenorb.potpvp.kt.tab.TabEngine;
import net.frozenorb.potpvp.kt.util.serialization.*;
import net.frozenorb.potpvp.kt.uuid.UUIDCache;
import net.frozenorb.potpvp.kt.visibility.VisibilityEngine;
import net.frozenorb.potpvp.pvpclasses.PvPClassHandler;
import net.frozenorb.potpvp.util.config.BasicConfigurationFile;
import net.frozenorb.potpvp.util.event.HalfHourEvent;
import net.frozenorb.potpvp.util.event.HourEvent;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.TypeAdapter;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonReader;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import lombok.Getter;
import net.frozenorb.potpvp.arena.ArenaHandler;
import net.frozenorb.potpvp.duel.DuelHandler;
import net.frozenorb.potpvp.elo.EloHandler;
import net.frozenorb.potpvp.follow.FollowHandler;
import net.frozenorb.potpvp.kit.KitHandler;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kittype.KitTypeJsonAdapter;
import net.frozenorb.potpvp.kittype.KitTypeParameterType;
import net.frozenorb.potpvp.listener.BasicPreventionListener;
import net.frozenorb.potpvp.listener.BowHealthListener;
import net.frozenorb.potpvp.listener.ChatFormatListener;
import net.frozenorb.potpvp.listener.ChatToggleListener;
import net.frozenorb.potpvp.listener.NightModeListener;
import net.frozenorb.potpvp.listener.PearlCooldownListener;
import net.frozenorb.potpvp.listener.RankedMatchQualificationListener;
import net.frozenorb.potpvp.listener.TabCompleteListener;
import net.frozenorb.potpvp.lobby.LobbyHandler;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.nametag.PotPvPNametagProvider;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.postmatchinv.PostMatchInvHandler;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.potpvp.rematch.RematchHandler;
import net.frozenorb.potpvp.scoreboard.PotPvPScoreboardConfiguration;
import net.frozenorb.potpvp.setting.SettingHandler;
import net.frozenorb.potpvp.statistics.StatisticsHandler;
import net.frozenorb.potpvp.tab.PotPvPLayoutProvider;

@Getter
public final class PotPvPND extends JavaPlugin {

    public static Chat chat = null;
    private static PotPvPND instance;


    @Getter
    private static Gson gson = new GsonBuilder()
        .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
        .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
        .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
        .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
        .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
        .registerTypeHierarchyAdapter(KitType.class, new KitTypeJsonAdapter()) // custom KitType serializer
        .serializeNulls()
        .create();

    public static Gson plainGson = new GsonBuilder()
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
            .serializeNulls()
            .create();

    //Mongo
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    //Configs
    public BasicConfigurationFile mainConfig;
    public BasicConfigurationFile divisionsConfig;
    public BasicConfigurationFile hologramsConfig;

    //Main Handlers
    private SettingHandler settingHandler;
    private DuelHandler duelHandler;
    private KitHandler kitHandler;
    private LobbyHandler lobbyHandler;
    private ArenaHandler arenaHandler;
    private MatchHandler matchHandler;
    private PartyHandler partyHandler;
    private QueueHandler queueHandler;
    private RematchHandler rematchHandler;
    private PostMatchInvHandler postMatchInvHandler;
    private FollowHandler followHandler;
    private HologramHandler hologramHandler;
    private EloHandler eloHandler;
    private DivisionHandler divisionHandler;
    private PvPClassHandler pvpClassHandler;
    //From qLib
    public Redis redis;
    public CommandHandler commandHandler;
    public ScoreboardEngine scoreboardEngine;
    public TabEngine tabEngine;
    public NametagEngine nametagEngine;
    public VisibilityEngine visibilityEngine;
    public UUIDCache uuidCache;

    private final ChatColor dominantColor = ChatColor.GOLD;
    private final PotPvPCache cache = new PotPvPCache();

    @Override
    public void onEnable() {
        instance = this;

        mainConfig = new BasicConfigurationFile(this, "config");
        divisionsConfig = new BasicConfigurationFile(this, "divisions");
        hologramsConfig = new BasicConfigurationFile(this, "holograms");

        saveDefaultConfig();

        setupRedis();
        setupMongo();

        uuidCache = new RedisUUIDCache();
        uuidCache.load();
        getServer().getPluginManager().registerEvents(uuidCache, this);

        commandHandler = new CommandHandler();
        commandHandler.load();
        commandHandler.registerAll(this);
        commandHandler.registerParameterType(KitType.class, new KitTypeParameterType());
        commandHandler.registerParameterType(HealingMethod.class, new HealingMethodParameterType());

        scoreboardEngine = new ScoreboardEngine();
        scoreboardEngine.load();
        scoreboardEngine.setConfiguration(PotPvPScoreboardConfiguration.create());

        tabEngine = new TabEngine();
        tabEngine.load();
        tabEngine.setLayoutProvider(new PotPvPLayoutProvider());

        nametagEngine = new NametagEngine();
        nametagEngine.load();
        nametagEngine.registerProvider(new PotPvPNametagProvider());

        visibilityEngine = new VisibilityEngine();
        visibilityEngine.load();

        PingAdapter pingAdapter = new PingAdapter();

        ProtocolLibrary.getProtocolManager().addPacketListener(pingAdapter);
        ProtocolLibrary.getProtocolManager().addPacketListener(new InventoryAdapter());
        ProtocolLibrary.getProtocolManager().addPacketListener(new TabAdapter());

        getServer().getPluginManager().registerEvents(pingAdapter, this);

        new LagCheck().runTaskTimerAsynchronously(this, 100L, 100L);

        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setTime(6_000L);
        }

        settingHandler = new SettingHandler();
        duelHandler = new DuelHandler();
        kitHandler = new KitHandler();

        divisionHandler = new DivisionHandler();
        hologramHandler = new HologramHandler();
        hologramHandler.loadHolograms();

        lobbyHandler = new LobbyHandler();
        arenaHandler = new ArenaHandler();
        matchHandler = new MatchHandler();
        partyHandler = new PartyHandler();
        queueHandler = new QueueHandler();
        rematchHandler = new RematchHandler();
        postMatchInvHandler = new PostMatchInvHandler();
        followHandler = new FollowHandler();
        eloHandler = new EloHandler();
        pvpClassHandler = new PvPClassHandler();

        setupChat();

        getServer().getPluginManager().registerEvents(new BasicPreventionListener(), this);
        getServer().getPluginManager().registerEvents(new BowHealthListener(), this);
        getServer().getPluginManager().registerEvents(new ChatFormatListener(), this);
        getServer().getPluginManager().registerEvents(new ChatToggleListener(), this);
        getServer().getPluginManager().registerEvents(new NightModeListener(), this);
        getServer().getPluginManager().registerEvents(new PearlCooldownListener(), this);
        getServer().getPluginManager().registerEvents(new RankedMatchQualificationListener(), this);
        getServer().getPluginManager().registerEvents(new TabCompleteListener(), this);
        getServer().getPluginManager().registerEvents(new StatisticsHandler(), this);

        // menu api
        getServer().getPluginManager().registerEvents(new ButtonListeners(), this);

        setupHourEvents();

        getServer().getScheduler().runTaskTimerAsynchronously(this, cache, 20L, 20L);

        new Morpheus(this);
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    @Override
    public void onDisable() {
        for (Match match : this.matchHandler.getHostedMatches()) {
            if (match.getKitType().isBuildingAllowed()) match.getArena().restore();
        }

        hologramHandler.saveHolograms();

        try {
            arenaHandler.saveSchematics();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String playerName : PvPClassHandler.getEquippedKits().keySet()) {
            PvPClassHandler.getEquippedKits().get(playerName).remove(getServer().getPlayerExact(playerName));
        }

        instance = null;
    }

    private void setupHourEvents() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor((new ThreadFactoryBuilder()).setNameFormat("qLib - Hour Event Thread").setDaemon(true).build());
        int minOfHour = Calendar.getInstance().get(Calendar.MINUTE);
        int minToHour = 60 - minOfHour;
        int minToHalfHour = (minToHour >= 30) ? minToHour : (30 - minOfHour);

        executor.scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask(this, () -> Bukkit.getServer().getPluginManager().callEvent(new HourEvent(Date.from(Instant.now()).getHours()))), minToHour, 60L, TimeUnit.MINUTES);

        executor.scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask(this, () -> {
            Date dateNow = Date.from(Instant.now());
            Bukkit.getServer().getPluginManager().callEvent(new HalfHourEvent(dateNow.getHours(), dateNow.getMinutes()));
        }), minToHalfHour, 30L, TimeUnit.MINUTES);
    }

    private void setupRedis() {

        this.redis = new Redis();

        final RedisCredentials.Builder localBuilder = new RedisCredentials.Builder().host(mainConfig.getString("Redis.Host"))
                .port(mainConfig
                        .getInteger("Redis.Port"));

        if (mainConfig.getBoolean("Redis.Authentication.Enabled")) {
            localBuilder.password(mainConfig.getString("Redis.Authentication.Password"));
        }

        final RedisCredentials.Builder backboneBuilder = new RedisCredentials.Builder().host(mainConfig.getString("Redis.Host"))
                .port(mainConfig
                        .getInteger("Redis.Port"));

        if (mainConfig.getBoolean("Redis.Authentication.Enabled")) {
            backboneBuilder.password(mainConfig.getString("Redis.Authentication.Password"));
        }
        this.redis.load(localBuilder.build(), backboneBuilder.build());
    }

    private void setupMongo() {
        this.mongoClient = new MongoClient(new MongoClientURI(mainConfig.getString("Mongo.URI")));
        final String databaseId = mainConfig.getString("Mongo.Database");
        this.mongoDatabase = this.mongoClient.getDatabase(databaseId);
    }


    public ArenaHandler getArenaHandler() {
        return arenaHandler;
    }

    public static PotPvPND getInstance() {
        return instance;
    }
}