package common.network.responses;

import command.Command;
import common.utility.Commands;

public class ExecuteScriptResponse extends Response {
    public ExecuteScriptResponse(String error) {
        super(Commands.EXECUTE_SCRIPT, error);
    }
}
