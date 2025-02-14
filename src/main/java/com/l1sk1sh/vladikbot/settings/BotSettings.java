package com.l1sk1sh.vladikbot.settings;

import com.l1sk1sh.vladikbot.models.AudioRepeatMode;
import com.l1sk1sh.vladikbot.utils.BotUtils;
import com.l1sk1sh.vladikbot.utils.FormatUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

/**
 * @author l1sk1sh
 */
@SuppressWarnings({"CanBeFinal", "FieldMayBeFinal"})
@Getter
public class BotSettings {

    @Getter(AccessLevel.NONE)
    @Setter
    private transient SettingsUpdateListener listener;

    /* Finish all paths with file system separator! */
    private String token = "MY_BOT_TOKEN";                              // Bot token taken from discord developer portal
    private long ownerId = 0L;                                          // Id of the person, who is hosting the bot
    private long maintainerGuildId = 0L;                                // Id of Guild that will be used to maintaining notifs
    private long forceGuildId = 0L;                                     // Id of Guild that will be used for fast commands update (single guild or debug)
    private String workdir = "./app";                                   // Working directory for all files
    private String localTmpFolder = workdir + "/tmp/";                  // Local tmp for workdir
    private String rotationBackupFolder = workdir + "/backup/";         // Local rotation backup folder (that will be stored)
    private String playlistsFolder = workdir + "/playlists/";           // Local folder for playlists to be stored
    private String logsFolder = workdir + "/logs/";                     // Local storage for guild logging
    private String settingsFolder = workdir + "/settings/";             // Settings for guilds
    private String prefix = "~";                                        // Bot prefix
    private String helpWord = "help";                                   // Help word used for help command
    private String successEmoji = "\uD83D\uDC4C";                       // 👌
    private String warningEmoji = "\uD83D\uDD95";                       // 🖕
    private String errorEmoji = "\uD83D\uDCA2";                         // 💢
    private String loadingEmoji = "\uD83E\uDDF6";                       // 🧶
    private String searchingEmoji = "\uD83D\uDD0E";                     // 🔎
    private String activity = "watching Ubisoft conference";            // Current name of the 'activity' being done by bot
    private String onlineStatus = "ONLINE";                             // "online", "idle", "dnd", "invisible", "offline", ""
    private long maxSeconds = 0L;                                       // Maximum song length
    private long aloneTimeUntilStop = 0L;                               // Time until bot leaves vchannel if alone
    private boolean leaveChannel = true;                                // Leave channel if no one is listening
    private boolean songInStatus = false;                               // Show song as status
    private boolean npImages = true;                                    // Display search images
    private AudioRepeatMode repeat = AudioRepeatMode.OFF;               // Current repeat mode
    private boolean autoReply = false;                                  // If to automatically reply to certain phrases
    private Const.MatchingStrategy matchingStrategy = Const.MatchingStrategy.FULL; // How reply rules should be matched
    private double replyChance = 1.0;                                   // Change that bot will reply
    private double audioSkipRatio = 0.55;                               // Voting ratio to skip current song
    private boolean simulateActivity = false;                           // If bot should change statuses by himself
    private boolean sendNews = true;                                    // If bot should update news channel
    private boolean sendMemes = true;                                   // If bot should update memes channel
    private boolean logGuildChanges = false;                            // If bot should log message/avatars etc changes
    private boolean autoTextBackup = true;                              // Automatically create backups of all available chats
    private boolean autoMediaBackup = true;                             // Automatically save media from all available chats
    private int targetHourForAutoTextBackup = 0;                        // Set local TZ hour for text backup to be started
    private int targetHourForAutoMediaBackup = 0;                       // Set local TZ hour for media backup to be started
    private int delayDaysForAutoTextBackup = 0;                         // Set delay in days between text backups
    private int delayDaysForAutoMediaBackup = 0;                        // Set delay in days between text backups
    private String dockerHost = "tcp://localhost:2375";                 // Set custom docker host
    private String jenkinsApiHost = "http://127.0.0.1:8080";            // Set jenkins API host
    private String jenkinsApiUsername = "bot";                          // Set jenkins API username
    private String jenkinsApiPassword = "JENKINS_API_TOKEN";            // Set jenkins API password

    /* Runtime and bot specific internal configs */
    @Setter
    private transient boolean lockedBackup = false;                     // Holds value for running backup
    @Setter
    private transient boolean lockedAutoBackup = false;                 // Holds value for running auto backup
    @Setter
    private transient boolean dockerRunning = false;                    // Holds value for docker status
    private long lastAutoTextBackupTime = 0L;                           // Holds time of last auto text backup
    private long lastAutoMediaBackupTime = 0L;                          // Holds time of last auto media backup

    BotSettings(SettingsUpdateListener listener) {
        this.listener = listener;
    }

    public Activity getActivity() {
        return BotUtils.parseActivity(activity);
    }

    public final OnlineStatus getOnlineStatus() {
        return BotUtils.parseStatus(onlineStatus);
    }

    public final void setRepeat(AudioRepeatMode repeat) {
        this.repeat = repeat;
        listener.onSettingsUpdated();
    }

    public final void setAutoReply(boolean autoReply) {
        this.autoReply = autoReply;
        listener.onSettingsUpdated();
    }

    public void setMatchingStrategy(Const.MatchingStrategy matchingStrategy) {
        this.matchingStrategy = matchingStrategy;
        listener.onSettingsUpdated();
    }

    public void setReplyChance(double replyChance) {
        this.replyChance = replyChance;
        listener.onSettingsUpdated();
    }

    public void setAudioSkipRatio(double skipRatio) {
        this.audioSkipRatio = skipRatio;
        listener.onSettingsUpdated();
    }

    public final void setSimulateActivity(boolean simulateActivity) {
        this.simulateActivity = simulateActivity;
        listener.onSettingsUpdated();
    }

    public void setSendNews(boolean sendNews) {
        this.sendNews = sendNews;
        listener.onSettingsUpdated();
    }

    public void setSendMemes(boolean sendMemes) {
        this.sendMemes = sendMemes;
    }

    public void setLogGuildChanges(boolean logGuildChanges) {
        this.logGuildChanges = logGuildChanges;
        listener.onSettingsUpdated();
    }

    public final void setAutoTextBackup(boolean autoTextBackup) {
        this.autoTextBackup = autoTextBackup;
        listener.onSettingsUpdated();
    }

    public final void setAutoMediaBackup(boolean autoMediaBackup) {
        this.autoMediaBackup = autoMediaBackup;
        listener.onSettingsUpdated();
    }

    public final void setTargetHourForAutoTextBackup(int targetHourForAutoTextBackup) {
        this.targetHourForAutoTextBackup = targetHourForAutoTextBackup;
        listener.onSettingsUpdated();
    }

    public final void setTargetHourForAutoMediaBackup(int targetHourForAutoMediaBackup) {
        this.targetHourForAutoMediaBackup = targetHourForAutoMediaBackup;
        listener.onSettingsUpdated();
    }

    public void setDelayDaysForAutoTextBackup(int delayDaysForAutoTextBackup) {
        this.delayDaysForAutoTextBackup = delayDaysForAutoTextBackup;
        listener.onSettingsUpdated();
    }

    public void setDelayDaysForAutoMediaBackup(int delayDaysForMediaBackup) {
        this.delayDaysForAutoMediaBackup = delayDaysForMediaBackup;
        listener.onSettingsUpdated();
    }

    public void setLastAutoTextBackupTime(long lastAutoTextBackupTime) {
        this.lastAutoTextBackupTime = lastAutoTextBackupTime;
        listener.onSettingsUpdated();
    }

    public void setLastAutoMediaBackupTime(long lastAutoMediaBackupTime) {
        this.lastAutoMediaBackupTime = lastAutoMediaBackupTime;
        listener.onSettingsUpdated();
    }

    public final String getMaxTime() {
        final int maxTimeMultiplier = 1000;
        return FormatUtils.formatTimeTillHours(maxSeconds * maxTimeMultiplier);
    }

    public final boolean isTooLong(AudioTrack track) {
        final float trackDurationDivider = 1000f;
        return (maxSeconds > 0) && (Math.round(track.getDuration() / trackDurationDivider) > maxSeconds);
    }
}