/*
 *  Copyright (c) 2008 - Tomas Janecek.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package songer.gui;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import javax.swing.JTextArea;

/**
 * Longging handler displaying appending the log to the specified text-area component.
 * @author Tomas Janecek
 */
public class JTextAreaLogHandler extends Handler {
	/** JTextArea where the logs are appended. */
	private JTextArea textArea;

	
	
	/** Constructor - Creates a new logging-handler.
	 *  @param textArea JTextArea where the logs should be appended.
	 */
	public JTextAreaLogHandler(JTextArea textArea) {
		super();
		this.textArea = textArea;
	}

	
	
	/** Override - Imlementation of the Handler publish method.
	 * @see Handler#publish(LogRecord record) */
	@Override
	public void publish(LogRecord record) {
		if (isLoggable(record)) {
			String msg = "-> " + record.getMessage() + "\n";
			textArea.append(msg);
			textArea.setCaretPosition(textArea.getText().length());
		}
	}

	
	
	/** Override - Implementation of the Handler flush method.
	 * @see Handler#flush() */
	@Override
	public void flush() {
	}

	
	
	/** Override - Implementation of the Handle close method.
	 * @see Handler#close() */
	@Override
	public void close() throws SecurityException {
	}
}
