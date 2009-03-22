package de.cdauth.bwproxy;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.EOFException;

/**
 * For each normal-priority connection a ConnectionReceiver and a ConnectionSender thread is started. This thread receives data via the TCP
 * connection from the proxy and sends it to the client socket. If the bandwidth limit is excessed, this thread is stopped and the LowPriorityReceiver
 * does the following download.
 * @author Candid Dauth
 * @license GPL-3
*/

public class ConnectionReceiver extends Thread
{
	private Connection m_connection;

	public ConnectionReceiver(Connection a_connection, ThreadGroup a_threadgroup)
	{
		super(a_threadgroup, "receiver "+a_number);

		m_connection = a_connection;
	}

	public void run()
	{
		try
		{
			InputStream input_stream = m_connection.getProxySocket().getInputStream();
			OutputStream output_stream = m_connection.getClientSocket().getOutputStream();

			int used_traffic = 0;
			int max_traffic = Options.getMaxTraffic();
			int read = 0;

			byte[] buffer = new byte[1024];

			Logger.debug("max_traffic is "+max_traffic);
			Logger.debug("buffer length is "+buffer.length);

			while(true)
			{
				read = input_stream.read(buffer);
				if(read == -1)
					throw new EOFException();

				if(m_connection.canceled())
					throw new Exception("Connection.canceled() is true.");

				if(read > 0)
				{
					used_traffic += read;
					//Logger.debug("Received "+new String(buffer, 0, read));
					output_stream.write(buffer, 0, read);
				}

				if(used_traffic > max_traffic)
				{
					Main.getLPReceiverThread().add(m_connection);
					break;
				}

				if(read < 1)
				{
					try { sleep(10); } catch(Exception e) { }
				}
			}
		}
		catch(Exception e)
		{
			Logger.error("Receiver "+m_number+" aborted.", e);
			m_connection.cancel();
		}
	}
}