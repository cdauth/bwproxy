package de.cdauth.bwproxy;

import java.io.IOException;
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
	
	private InputStream m_inputStream;
	private OutputStream m_outputStream;

	public ConnectionReceiver(Connection a_connection, ThreadGroup a_threadgroup) throws IOException
	{
		super(a_threadgroup, "receiver");

		m_connection = a_connection;
		
		m_inputStream = m_connection.getProxySocket().getInputStream();
		m_outputStream = m_connection.getClientSocket().getOutputStream();
	}

	public void run()
	{
		try
		{
			int used_traffic = 0;
			int max_traffic = Options.getMaxTraffic();
			int read = 0;

			byte[] buffer = new byte[1024];

			Logger.debug("max_traffic is "+max_traffic);
			Logger.debug("buffer length is "+buffer.length);
			Logger.debug("receiveBufferSize is "+m_connection.getProxySocket().getReceiveBufferSize());

			while(true)
			{
				read = m_inputStream.read(buffer);
				if(read == -1)
					throw new EOFException();

				if(m_connection.canceled())
					throw new Exception("Connection.canceled() is true.");

				used_traffic += read;
				//Logger.debug("Received "+new String(buffer, 0, read));
				m_outputStream.write(buffer, 0, read);

				if(used_traffic > max_traffic)
				{
					Main.getLPReceiverThread().add(m_connection);
					break;
				}
			}
		}
		catch(Exception e)
		{
			Logger.error("Receiver aborted.", e);
			m_connection.cancel();
		}
	}
}
