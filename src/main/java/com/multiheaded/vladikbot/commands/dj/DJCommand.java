package com.multiheaded.vladikbot.commands.dj;

import com.multiheaded.vladikbot.VladikBot;
import com.multiheaded.vladikbot.commands.music.MusicCommand;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;

/**
 * @author Oliver Johnson
 * Changes from original source:
 * - Reformating code
 * @author John Grosh
 */
// TODO Fix help output
abstract class DJCommand extends MusicCommand {
    DJCommand(VladikBot bot) {
        super(bot);
        this.category = new Category("DJ", event ->
        {
            if (event.getAuthor().getId().equals(event.getClient().getOwnerId())) {
                return true;
            }
            if (event.getGuild() == null) {
                return true;
            }
            if (event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                return true;
            }
            Role djRole = bot.getSettings().getDjRole(event.getGuild());
            return djRole != null &&
                    (event.getMember().getRoles().contains(djRole) || djRole.getIdLong() == event.getGuild().getIdLong());
        });
    }
}
