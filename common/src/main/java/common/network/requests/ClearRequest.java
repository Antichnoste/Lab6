package common.network.requests;

import command.Command;
import common.utility.Commands;
import managers.CollectionManager;
import utility.Console;
import utility.ExecutionResponse;

public class ClearRequest extends Request {
    public ClearRequest() {
        super(Commands.CLEAR);
    }
}
