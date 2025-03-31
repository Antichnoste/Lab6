package common.network.requests;

import command.Command;
import common.utility.Commands;
import managers.CollectionManager;
import models.Movie;
import utility.Console;
import utility.ExecutionResponse;

public class FilterContainsNameRequest extends Request {
    public FilterContainsNameRequest() {
        super(Commands.FILTER_CONTAINS_NAME);
    }
}
