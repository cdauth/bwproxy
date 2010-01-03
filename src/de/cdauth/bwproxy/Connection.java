package de.cdauth.bwproxy;

import java.io.IOException;
import java.net.Socket;

/**
 * Represents a connection from the client to the proxy. Starts a ConnectionReceiver and ConnectionSender thread and provides them with
 * methods to communicate to each other.
 * @author Candid Dauth
 * @license GPL-3
*/

public class Connection
{
	protected Socket m_client;
	protected Socket m_proxy;

	protected ConnectionSender m_sender;
	protected ConnectionReceiver m_receiver;

	volatile protected boolean m_canceled = false;
	
	volatile static private long sm_connectionNumber = 1;
	private long m_connectionNumber;

	/**
	 * Initialises the new client thread.
	 * @param a_client_socket The client socket returned by java.net.ServerSocket.accept().
	 * @param a_threadgroup The thread group for all client connections.
	*/

	public Connection(Socket a_client_socket, ThreadGroup a_threadgroup) throws IOException
	{
		m_connectionNumber = sm_connectionNumber++;

		m_client = a_client_socket;

		m_proxy = new Socket();
		m_proxy.connect(Options.getProxy());

		Logger.debug(""+m_connectionNumber+": Connected to server "+m_proxy.getInetAddress()+" on port "+m_proxy.getPort()+".");

		m_sender = new ConnectionSender(this, a_threadgroup);
		m_receiver = new ConnectionReceiver(this, a_threadgroup);
		m_sender.start();
		m_receiver.start();
	}

	@Override
	public int hashCode()
	{
		return m_client.hashCode();
	}


	public Socket getClientSocket()
	{
		return m_client;
	}

	public Socket getProxySocket()
	{
		return m_proxy;
	}
	
	public long getConnectionNumber()
	{
		return m_connectionNumber;
	}

	/**
	 * @see cancel()
	*/

	public boolean canceled()
	{
		if(!m_canceled && !m_client.isConnected())
			cancel();
		return m_canceled;
	}

	/**
	 * Closes the proxy and client sockets and sets the return value of canceled() to true. The ConnectionReceiver and ConnectionSender
	 * threads check that value continuously and stop themselves if it is true.
	*/

	public void cancel()
	{
		m_canceled = true;

		try
		{
			m_client.close();
		}
		catch(Exception e)
		{
			Logger.debug(""+m_connectionNumber+": Closing client socket failed.", e);
		}
		try
		{
			m_proxy.close();
		}
		catch(Exception e)
		{
			Logger.debug(""+m_connectionNumber+": Closing proxy socket failed.", e);
		}
		
		// Prevent circular references
		m_sender = null;
		m_receiver = null;
	}
}
