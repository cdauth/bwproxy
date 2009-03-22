package de.cdauth.bwproxy;

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
	private int m_number;

	public ConnectionSender(Connection a_connection, ThreadGroup a_threadgroup, int a_number)
	{
		super(a_threadgroup, "sender "+a_number);

		m_connection = a_connection;
		m_number = a_number;
	}

	public void run()
	{
		try
		{
			byte[] buffer = new byte[1024];
			InputStream input_stream = m_connection.getClientSocket().getInputStream();
			OutputStream output_stream = m_connection.getProxySocket().getOutputStream();

			int read = 0;
			while((read = input_stream.read(buffer, 0, 1024)) > -1)
			{
				if(m_connection.canceled())
					break;
				if(read < 1)
				{
					try { sleep(10); } catch(Exception e) { }
				}
				else
				{
					Logger.debug("Sent "+new String(buffer, 0, read));
					output_stream.write(buffer, 0, read);
				}
			}
		}
		catch(Exception e)
		{
			Logger.error("Sender "+m_number+" aborted.", e);
		}
		m_connection.cancel();
	}
}