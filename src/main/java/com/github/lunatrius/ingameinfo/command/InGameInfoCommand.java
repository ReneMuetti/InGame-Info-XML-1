package com.github.lunatrius.ingameinfo.command;

import com.github.lunatrius.core.handler.DelayedGuiDisplayTicker;
import com.github.lunatrius.ingameinfo.InGameInfoCore;
import com.github.lunatrius.ingameinfo.client.gui.GuiModConfig;
import com.github.lunatrius.ingameinfo.client.gui.GuiTags;
import com.github.lunatrius.ingameinfo.handler.ConfigurationHandler;
import com.github.lunatrius.ingameinfo.handler.Ticker;
import com.github.lunatrius.ingameinfo.reference.Names;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class InGameInfoCommand extends CommandBase {
    public static final InGameInfoCommand INSTANCE = new InGameInfoCommand();

    private final InGameInfoCore core = InGameInfoCore.INSTANCE;

    private InGameInfoCommand() {}

    @Override
    public String getName() {
        return Names.Command.NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return Names.Command.Message.USAGE;
    }

    @Override
    public boolean canCommandSenderUse(ICommandSender sender) {
        return true;
    }

    @Override
    public List tabComplete(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Names.Command.RELOAD, Names.Command.LOAD, Names.Command.SAVE, Names.Command.ENABLE, Names.Command.DISABLE, Names.Command.TAGLIST, Names.Command.CONFIG);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase(Names.Command.LOAD)) {
                return getListOfStringsMatchingLastWord(args, getFilenames());
            } else if (args[0].equalsIgnoreCase(Names.Command.SAVE)) {
                return CommandBase.getListOfStringsMatchingLastWord(args, Names.Files.FILE_XML, Names.Files.FILE_JSON, Names.Files.FILE_TXT);
            }
        }

        return null;
    }

    private List<String> getFilenames() {
        File[] files = this.core.getConfigDirectory().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(Names.Files.NAME) && (name.endsWith(Names.Files.EXT_XML) || name.endsWith(Names.Files.EXT_JSON) || name.endsWith(Names.Files.EXT_TXT));
            }
        });

        List<String> filenames = new ArrayList<String>();
        for (File file : files) {
            filenames.add(file.getName());
        }

        return filenames;
    }

    @Override
    public void execute(ICommandSender commandSender, String[] args) throws CommandException {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase(Names.Command.RELOAD)) {
                commandSender.addChatMessage(new ChatComponentTranslation(Names.Command.Message.RELOAD));
                ConfigurationHandler.reload();
                final boolean success = this.core.reloadConfig();
                commandSender.addChatMessage(new ChatComponentTranslation(success ? Names.Command.Message.SUCCESS : Names.Command.Message.FAILURE));
                return;
            } else if (args[0].equalsIgnoreCase(Names.Command.LOAD)) {
                commandSender.addChatMessage(new ChatComponentTranslation(Names.Command.Message.LOAD, args[1]));
                final boolean success = this.core.loadConfig(args[1]);
                commandSender.addChatMessage(new ChatComponentTranslation(success ? Names.Command.Message.SUCCESS : Names.Command.Message.FAILURE));
                if (success) {
                    ConfigurationHandler.setConfigName(args[1]);
                    ConfigurationHandler.save();
                }
                return;
            } else if (args[0].equalsIgnoreCase(Names.Command.SAVE)) {
                commandSender.addChatMessage(new ChatComponentTranslation(Names.Command.Message.SAVE, args[1]));
                final boolean success = this.core.saveConfig(args[1]);
                commandSender.addChatMessage(new ChatComponentTranslation(success ? Names.Command.Message.SUCCESS : Names.Command.Message.FAILURE));
                return;
            } else if (args[0].equalsIgnoreCase(Names.Command.ENABLE)) {
                commandSender.addChatMessage(new ChatComponentTranslation(Names.Command.Message.ENABLE));
                Ticker.enabled = true;
                return;
            } else if (args[0].equalsIgnoreCase(Names.Command.DISABLE)) {
                commandSender.addChatMessage(new ChatComponentTranslation(Names.Command.Message.DISABLE));
                Ticker.enabled = false;
                return;
            } else if (args[0].equalsIgnoreCase(Names.Command.TAGLIST)) {
                DelayedGuiDisplayTicker.create(new GuiTags(), 10);
                return;
            } else if (args[0].equalsIgnoreCase(Names.Command.CONFIG)) {
                DelayedGuiDisplayTicker.create(new GuiModConfig(null), 0);
                return;
            }
        }

        throw new WrongUsageException(getUsage(commandSender));
    }

    @Override
    public int compareTo(Object obj) {
        return super.compareTo(obj);
    }
}
