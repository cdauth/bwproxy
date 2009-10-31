package de.cdauth.bwproxy;

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

	protected boolean m_canceled = false;

	/**
	 * Initialises the new client thread.
	 * @param a_client_socket The client socket returned by java.net.ServerSocket.accept().
	 * @param a_threadgroup The thread group for all client connections.
	 * @author Candid Dauth
	*/

	public Connection(Socket a_client_socket, ThreadGroup a_threadgroup) throws java.io.IOException
	{
		m_client = a_client_socket;

		m_proxy = new Socket();
		m_proxy.connect(Options.getProxy());

		Logger.debug("Connected to server "+m_proxy.getInetAddress()+" on port "+m_proxy.getPort()+".");

		m_sender = new ConnectionSender(this, a_threadgroup);
		m_receiver = new ConnectionReceiver(this, a_threadgroup);
		m_sender.start();
		m_receiver.start();
	}

	public Socket getClientSocket()
	{
		return m_client;
	}

	public Socket getProxySocket()
	{
		return m_proxy;
	}

	/**
	 * @see cancel()
	*/

	public boolean canceled()
	{
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
			Logger.debug("Closing client socket failed.", e);
		}
		try
		{
			m_proxy.close();
		}
		catch(Exception e)
		{
			Logger.debug("Closing proxy socket failed.", e);
		}
	}
}
