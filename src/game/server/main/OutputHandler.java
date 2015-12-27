package game.server.main;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JTextArea;

public class OutputHandler extends Handler {

	private JTextArea textArea;
	
	public OutputHandler(JTextArea area) {
		textArea = area;
	}

	public void close() throws SecurityException {
		
	}

	public void flush() {
		
	}

	public void publish(LogRecord record) {
		textArea.append(format(record));
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	
	public String format(LogRecord record){
		StringBuilder builder = new StringBuilder();
		Level lvl = record.getLevel();
		
		if(lvl == Level.FINEST){
			builder.append("[FINEST]");
		} else if(lvl == Level.FINE){
			builder.append("[FINE]");
		} else if(lvl == Level.FINER){
			builder.append("[FINER]");
		} else if(lvl == Level.INFO){
			builder.append("[INFO]");
		} else if(lvl == Level.WARNING){
			builder.append("[WARNING]");
		} else if(lvl == Level.SEVERE){
			builder.append("[SEVERE]");
		}
		
		builder.append(record.getMessage());
		builder.append('\n');
		
		Throwable t = record.getThrown();
		
		if(t != null){
			StringWriter writer = new StringWriter();
			t.printStackTrace(new PrintWriter(writer));
			
			builder.append(writer.toString());
		}
		
		return builder.toString();
	}

}
