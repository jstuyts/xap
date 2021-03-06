package org.gigaspaces.cli;

import com.gigaspaces.logger.Constants;
import com.gigaspaces.logger.GSLogConfigLoader;
import org.gigaspaces.cli.commands.XapVersionProvider;
import org.jini.rio.boot.BootUtil;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(
        versionProvider = XapVersionProvider.class,
        sortOptions = false,
        //headerHeading = "",
        //header = "<header goes here>",
        synopsisHeading = "Usage: ",
        descriptionHeading = "%nDescription: ",
        //description = "<description goes here>",
        parameterListHeading = "%nParameters:%n",
        optionListHeading = "%nOptions:%n")
public abstract class CliCommand implements Callable<Object> {

    protected static Logger LOGGER;

    @Option(names = {"--help"}, usageHelp = true, description = "Show the help information for this command")
    boolean usageHelpRequested;

    @Override
    public Object call() throws Exception {
        beforeExecute();
        try {
            execute();
        } catch (Exception e) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                final String stackTrace = BootUtil.getStackTrace(e);
                LOGGER.log(Level.FINEST, "Execution of [" + this.getClass()+"] threw an exception.\nStack trace: "+stackTrace, e);
            } else if(LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Execution of [" + this.getClass()+"] threw an exception.", e);
            }
            throw e;
        }
        return null;
    }

    public CliCommand(){
        GSLogConfigLoader.getLoader("cli");
        LOGGER = Logger.getLogger(Constants.LOGGER_CLI);
    }

    protected void beforeExecute() {
    }

    protected abstract void execute() throws Exception;

    protected void underConstruction() {
        Command command = this.getClass().getAnnotation(Command.class);
        System.out.println("Command " + command.name() + " is under construction");
    }
}
