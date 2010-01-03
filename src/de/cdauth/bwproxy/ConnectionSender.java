package de.cdauth.bwproxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The thread that sends data from the client to the proxy.
 * @author Candid Dauth
 * @license GPL-3
*/

public class ConnectionSender extends Thread
{
	private Connection m_connection;
	private InputStream m_inputStream;
	private OutputStream m_outputStream;

	public ConnectionSender(Connection a_connection, ThreadGroup a_threadgroup) throws IOException
	{
		super(a_threadgroup, "sender");

		m_connection = a_connection;
		
		m_inputStream = m_connection.getClientSocket().getInputStream();
		m_outputStream = m_connection.getProxySocket().getOutputStream();
	}

	public void run()
	{
		try
		{
			byte[] buffer = new byte[1024];

			int read = 0;
			while((read = m_inputStream.read(buffer)) > -1)
			{
				if(m_connection.canceled())
					break;
				
				//Logger.debug("Sent "+new String(buffer, 0, read));
				m_outputStream.write(buffer, 0, read);
			}
		}
		catch(Exception e)
		{
			Logger.error(""+m_connection.getConnectionNumber()+": Sender aborted.", e);
			m_connection.cancel();
		}
		Logger.debug(""+m_connection.getConnectionNumber()+": Sender closed.");
		// Do not cancel here! The receiver might still get data when the sender has reached its EOF.
	}
}
