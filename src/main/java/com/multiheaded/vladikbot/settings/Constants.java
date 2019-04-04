package com.multiheaded.vladikbot.settings;

import net.dv8tion.jda.core.Permission;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Oliver Johnson
 */
public class Constants {
    public final static Permission[] RECOMMENDED_PERMS = new Permission[]
            {Permission.MESSAGE_READ, Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
                    Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES,
                    Permission.MESSAGE_MANAGE, Permission.MESSAGE_EXT_EMOJI,
                    Permission.MANAGE_CHANNEL, Permission.VOICE_CONNECT,
                    Permission.VOICE_SPEAK, Permission.NICKNAME_CHANGE};

    static final String SETTINGS_JSON = "settings.json";
    public static final String TMP_MEDIA_FOLDER = "saved_media";

    public static final Integer DAY_IN_MILLISECONDS = 24 * 60 * 60 * 1000;
    public static final Integer EIGHT_MEGABYTES_IN_BYTES = 8388608;

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";
    public static final String[] SUPPORTED_MEDIA_FORMATS = {".jpg", ".png", ".mp4"};
    public static final String JSON_EXTENSION = ".json";
    public static final String HTML_EXTENSION = ".html";
    public static final String TXT_EXTENSION = ".txt";

    public static final String BACKUP_HTML_DARK = "HtmlDark";
    public static final String BACKUP_PLAIN_TEXT = "PlainText";
    public static final Map<String, String> FORMAT_EXTENSION;
    static {
        FORMAT_EXTENSION = new HashMap<>();
        FORMAT_EXTENSION.put(BACKUP_HTML_DARK, JSON_EXTENSION);
        FORMAT_EXTENSION.put(BACKUP_PLAIN_TEXT, TXT_EXTENSION);
    }

    public final static String PLAY_EMOJI = "\u25B6"; // ▶
    public final static String PAUSE_EMOJI = "\u23F8"; // ⏸
    public final static String STOP_EMOJI = "\u23F9"; // ⏹
    public final static String REPEAT_EMOJI = "\uD83D\uDD01"; // 🔁
    public final static String PROGRESS_EMOJI = "\uD83D\uDD18"; //🔘
    public final static String HEADPHONES_EMOJI = "\uD83C\uDFA7"; // 🎧
    public final static String LOAD_EMOJI = "\uD83D\uDCE5"; // 📥
    public final static String CANCEL_EMOJI = "\uD83D\uDEAB"; // 🚫
    public final static String VOLUME_OFF_EMOJI = "\uD83D\uDD07"; //🔇
    public final static String VOLUME_30_EMOJI = "\uD83D\uDD08"; //🔈
    public final static String VOLUME_70_EMOJI = "\uD83D\uDD09"; //🔉
    public final static String VOLUME_100_EMOJI = "\uD83D\uDD0A"; //🔊

}
