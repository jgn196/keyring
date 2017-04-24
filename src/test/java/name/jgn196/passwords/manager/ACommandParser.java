package name.jgn196.passwords.manager;

import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

public class ACommandParser {

    private static final CommandParser COMMAND_PARSER = new CommandParser(new TestConsole());

    @Test
    public void buildsGetCommand() {

        assertThat(COMMAND_PARSER.parse("get"), instanceOf(GetCommand.class));
    }

    @Test
    public void buildsListCommand() {

        assertThat(COMMAND_PARSER.parse("list"), instanceOf(ListCommand.class));
    }

    @Test
    public void buildsPutCommand() {

        assertThat(COMMAND_PARSER.parse("put"), instanceOf(PutCommand.class));
    }

    @Test
    public void buildsRemoveCommand() {

        assertThat(COMMAND_PARSER.parse("remove"), instanceOf(RemoveCommand.class));
    }

    @Test
    public void defaultsToBuildingHelpCommand() {

        assertThat(COMMAND_PARSER.parse(), instanceOf(HelpCommand.class));
        assertThat(COMMAND_PARSER.parse("not_a_command"), instanceOf(HelpCommand.class));
    }
}
