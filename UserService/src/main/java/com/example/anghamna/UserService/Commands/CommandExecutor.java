package com.example.anghamna.UserService.Commands;

public class CommandExecutor {
    public boolean runCommand(CommandInterface command) {
        return command.execute();
    }
}
