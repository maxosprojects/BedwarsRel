package io.github.bedwarsrevolution.commands;

import java.util.List;
import org.bukkit.command.CommandSender;

public interface ICommand {

  boolean execute(CommandSender sender, List<String> args);

  String[] getArguments();

  String getCommand();

  String getDescription();

  String getName();

  String getPermission();

  boolean hasPermission(CommandSender sender);

}
