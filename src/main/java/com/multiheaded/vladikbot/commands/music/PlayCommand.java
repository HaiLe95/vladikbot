package com.multiheaded.vladikbot.commands.music;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.ButtonMenu;
import com.multiheaded.vladikbot.VladikBot;
import com.multiheaded.vladikbot.audio.AudioHandler;
import com.multiheaded.vladikbot.audio.QueuedTrack;
import com.multiheaded.vladikbot.models.playlist.PlaylistLoader.Playlist;
import com.multiheaded.vladikbot.settings.Constants;
import com.multiheaded.vladikbot.settings.Settings;
import com.multiheaded.vladikbot.settings.SettingsManager;
import com.multiheaded.vladikbot.utils.FormatUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.concurrent.TimeUnit;

/**
 * @author Oliver Johnson
 * Changes from original source:
 * - Reformating code
 * @author John Grosh
 */
public class PlayCommand extends MusicCommand {

    private final Settings settings;

    public PlayCommand(VladikBot bot) {
        super(bot);
        this.name = "play";
        this.arguments = "<title|URL|subcommand>";
        this.help = "plays the provided song";
        this.beListening = true;
        this.bePlaying = false;
        this.children = new Command[]{new PlaylistCommand(bot)};
        settings = SettingsManager.getInstance().getSettings();

    }

    @Override
    public void doCommand(CommandEvent event) {
        if (event.getArgs().isEmpty() && event.getMessage().getAttachments().isEmpty()) {
            AudioHandler audioHandler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            if (audioHandler.getPlayer().getPlayingTrack() != null && audioHandler.getPlayer().isPaused()) {
                boolean isDJ = event.getMember().hasPermission(Permission.MANAGE_SERVER);
                if (!isDJ)
                    isDJ = event.isOwner();
                Role dj = settings.getDjRole(event.getGuild());
                if (!isDJ && dj != null)
                    isDJ = event.getMember().getRoles().contains(dj);
                if (!isDJ)
                    event.replyError("Only DJs can unpause the player!");
                else {
                    audioHandler.getPlayer().setPaused(false);
                    event.replySuccess("Resumed **" + audioHandler.getPlayer().getPlayingTrack().getInfo().title + "**.");
                }
                return;
            }
            StringBuilder builder = new StringBuilder(event.getClient().getWarning() + " Play Commands:\n");
            builder.append("\n`").append(event.getClient().getPrefix()).append(name)
                    .append(" <song title>` - plays the first result from Youtube");
            builder.append("\n`").append(event.getClient().getPrefix()).append(name)
                    .append(" <URL>` - plays the provided song, playlist, or stream");
            for (Command cmd : children)
                builder.append("\n`").append(event.getClient().getPrefix()).append(name)
                        .append(" ").append(cmd.getName()).append(" ")
                        .append(cmd.getArguments()).append("` - ").append(cmd.getHelp());
            event.reply(builder.toString());
            return;
        }
        String args = event.getArgs().startsWith("<") && event.getArgs().endsWith(">")
                ? event.getArgs().substring(1, event.getArgs().length() - 1)
                : event.getArgs().isEmpty() ? event.getMessage().getAttachments().get(0).getUrl() : event.getArgs();
        event.reply(settings.getLoadingEmoji() + " Loading... `[" + args + "]`", m -> bot.getPlayerManager().loadItemOrdered(
                event.getGuild(), args, new ResultHandler(m, event, false)));
    }

    private class ResultHandler implements AudioLoadResultHandler {
        private final Message message;
        private final CommandEvent event;
        private final boolean ytsearch;

        private ResultHandler(Message message, CommandEvent event, boolean ytsearch) {
            this.message = message;
            this.event = event;
            this.ytsearch = ytsearch;
        }

        private void loadSingle(AudioTrack track, AudioPlaylist playlist) {
            if (settings.isTooLong(track)) {
                message.editMessage(FormatUtil.filter(event.getClient().getWarning()
                        + " This track (**" + track.getInfo().title + "**) is longer than the allowed maximum: `"
                        + FormatUtil.formatTime(track.getDuration()) + "` > `"
                        + FormatUtil.formatTime(settings.getMaxSeconds() * 1000) + "`")).queue();
                return;
            }
            AudioHandler audioHandler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            int pos = audioHandler.addTrack(new QueuedTrack(track, event.getAuthor())) + 1;
            String addMsg = FormatUtil.filter(event.getClient().getSuccess()
                    + " Added **" + track.getInfo().title
                    + "** (`" + FormatUtil.formatTime(track.getDuration()) + "`) "
                    + (pos == 0 ? "to begin playing" : " to the queue at position " + pos));
            if (playlist == null
                    || !event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ADD_REACTION)) {
                message.editMessage(addMsg).queue();
            } else {
                new ButtonMenu.Builder()
                        .setText(addMsg + "\n" + event.getClient().getWarning()
                                + " This track has a playlist of **" + playlist.getTracks().size()
                                + "** tracks attached. Select " + Constants.LOAD_EMOJI + " to load playlist.")
                        .setChoices(Constants.LOAD_EMOJI, Constants.CANCEL_EMOJI)
                        .setEventWaiter(bot.getWaiter())
                        .setTimeout(30, TimeUnit.SECONDS)
                        .setAction(re ->
                        {
                            if (re.getName().equals(Constants.LOAD_EMOJI)) {
                                message.editMessage(addMsg + "\n" + event.getClient().getSuccess()
                                        + " Loaded **" + loadPlaylist(playlist, track) + "** additional tracks!").queue();
                            } else {
                                message.editMessage(addMsg).queue();
                            }
                        }).setFinalAction(m ->
                {
                    try {
                        m.clearReactions().queue();
                    } catch (PermissionException ignore) {
                    }
                }).build().display(message);
            }
        }

        private int loadPlaylist(AudioPlaylist playlist, AudioTrack exclude) {
            int[] count = {0};
            playlist.getTracks().forEach((track) -> {
                if (!settings.isTooLong(track) && !track.equals(exclude)) {
                    AudioHandler audioHandler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
                    audioHandler.addTrack(new QueuedTrack(track, event.getAuthor()));
                    count[0]++;
                }
            });
            return count[0];
        }

        @Override
        public void trackLoaded(AudioTrack track) {
            loadSingle(track, null);
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            if (playlist.getTracks().size() == 1 || playlist.isSearchResult()) {
                AudioTrack single = playlist.getSelectedTrack() == null
                        ? playlist.getTracks().get(0) : playlist.getSelectedTrack();
                loadSingle(single, null);
            } else if (playlist.getSelectedTrack() != null) {
                AudioTrack single = playlist.getSelectedTrack();
                loadSingle(single, playlist);
            } else {
                int count = loadPlaylist(playlist, null);
                if (count == 0) {
                    message.editMessage(FormatUtil.filter(event.getClient().getWarning()
                            + " All entries in this playlist " +
                            (playlist.getName() == null ? "" : "(**" + playlist.getName()
                                    + "**) ") + "were longer than the allowed maximum (`"
                            + settings.getMaxTime() + "`)")).queue();
                } else {
                    message.editMessage(FormatUtil.filter(event.getClient().getSuccess() + " Found "
                            + (playlist.getName() == null ? "a playlist" : "playlist **"
                            + playlist.getName() + "**") + " with `"
                            + playlist.getTracks().size() + "` entries; added to the queue!"
                            + (count < playlist.getTracks().size() ? "\n" + event.getClient().getWarning()
                            + " Tracks longer than the allowed maximum (`"
                            + settings.getMaxTime() + "`) have been omitted." : ""))).queue();
                }
            }
        }

        @Override
        public void noMatches() {
            if (ytsearch)
                message.editMessage(FormatUtil.filter(event.getClient().getWarning()
                        + " No results found for `" + event.getArgs() + "`.")).queue();
            else
                bot.getPlayerManager().loadItemOrdered(event.getGuild(), "ytsearch:"
                        + event.getArgs(), new ResultHandler(message, event, true));
        }

        @Override
        public void loadFailed(FriendlyException throwable) {
            if (throwable.severity == Severity.COMMON)
                message.editMessage(event.getClient().getError()
                        + " Error loading: " + throwable.getMessage()).queue();
            else
                message.editMessage(event.getClient().getError()
                        + " Error loading track.").queue();
        }
    }

    protected class PlaylistCommand extends MusicCommand {
        PlaylistCommand(VladikBot bot) {
            super(bot);
            this.name = "playlist";
            this.aliases = new String[]{"pl"};
            this.arguments = "<name>";
            this.help = "plays the provided playlist";
            this.beListening = true;
            this.bePlaying = false;
        }

        @Override
        public void doCommand(CommandEvent event) {
            if (event.getArgs().isEmpty()) {
                event.reply(event.getClient().getError() + " Please include a playlist name.");
                return;
            }

            Playlist playlist = bot.getPlaylistLoader().getPlaylist(event.getArgs());
            if (playlist == null) {
                event.replyError("I could not find `" + event.getArgs() + ".txt` in the Playlists folder.");
                return;
            }

            event.getChannel().sendMessage(settings.getLoadingEmoji() + " Loading playlist **"
                    + event.getArgs() + "**... (" + playlist.getItems().size() + " items)").queue(m ->
            {
                AudioHandler audioHandler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
                playlist.loadTracks(bot.getPlayerManager(),
                        (audioTrack) -> audioHandler.addTrack(new QueuedTrack(audioTrack, event.getAuthor())), () -> {
                    StringBuilder builder = new StringBuilder(playlist.getTracks().isEmpty()
                            ? event.getClient().getWarning() + " No tracks were loaded!"
                            : event.getClient().getSuccess() + " Loaded **" + playlist.getTracks().size() + "** tracks!");
                    if (!playlist.getErrors().isEmpty())
                        builder.append("\nThe following tracks failed to load:");
                    playlist.getErrors().forEach(err -> builder.append("\n`[").append(err.getIndex() + 1).append("]` **")
                            .append(err.getItem()).append("**: ").append(err.getReason()));
                    String str = builder.toString();
                    if (str.length() > 2000)
                        str = str.substring(0, 1994) + " (...)";
                    m.editMessage(FormatUtil.filter(str)).queue();
                });
            });
        }
    }
}
