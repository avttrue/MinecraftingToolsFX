package mcru.MinecraftingTools.Helpers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * формат сообщений в системном логе
 */
public class MyLogFormatter extends SimpleFormatter
{
    
    @Override
    public final String format(LogRecord record)
    {
        java.util.Formatter formatter = new java.util.Formatter();
        
        formatter.format("[%1s] [%05d] [%3$tY.%3$tm.%3$te %3$tH.%3$tM.%3$tS.%tL] [%s.%s] %s",
                         record.getLevel().getName(),
                         record.getThreadID(),
                         record.getMillis(),
                         record.getSourceClassName(),
                         record.getSourceMethodName(),
                         record.getMessage());
        
        Throwable thrown = record.getThrown();
        
        if (thrown != null)
        {
            StringWriter sw = new StringWriter();
            thrown.printStackTrace(new PrintWriter(sw));
            formatter.format(" : %s", sw.toString());
        }
        else
            formatter.format("%n");
        
        return formatter.toString();
    }
}