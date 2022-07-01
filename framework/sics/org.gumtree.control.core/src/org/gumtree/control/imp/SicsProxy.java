/**
 * 
 */
package org.gumtree.control.imp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gumtree.control.batch.BatchControl;
import org.gumtree.control.batch.IBatchControl;
import org.gumtree.control.core.ISicsChannel;
import org.gumtree.control.core.ISicsConnectionContext;
import org.gumtree.control.core.ISicsModel;
import org.gumtree.control.core.ISicsProxy;
import org.gumtree.control.core.ServerStatus;
import org.gumtree.control.events.ISicsCallback;
import org.gumtree.control.events.ISicsMessageListener;
import org.gumtree.control.events.ISicsProxyListener;
import org.gumtree.control.exception.SicsCommunicationException;
import org.gumtree.control.exception.SicsException;
import org.gumtree.control.model.SicsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nxi
 *
 */
public class SicsProxy implements ISicsProxy {

	private static Logger logger = LoggerFactory.getLogger(SicsProxy.class);
	private String serverAddress;
	private String publisherAddress;
	private ISicsChannel channel;
	private ServerStatus serverStatus;
	private IBatchControl batchControl;
	private boolean isInterrupted;
	private ISicsModel sicsModel;
	private List<ISicsProxyListener> proxyListeners;
	private List<ISicsMessageListener> messageListeners;
	private ISicsConnectionContext connectionContext;
	
	public SicsProxy() {
		serverStatus = ServerStatus.UNKNOWN;
		proxyListeners = new ArrayList<ISicsProxyListener>();
		messageListeners = new ArrayList<ISicsMessageListener>();
		batchControl = new BatchControl(this);
		connectionContext = new SicsConnectionContext();
	}
	
	/* (non-Javadoc)
	 * @see org.gumtree.control.core.ISicsProxy#connect()
	 */
	@Override
	public boolean connect(String serverAddress, String publisherAddress) throws SicsException {
		if (serverAddress != null && !serverAddress.equals(this.serverAddress)) {
			this.serverAddress = serverAddress;
			this.publisherAddress = publisherAddress;
			connectionContext.setServerAddress(serverAddress);
			connectionContext.setPublisherAddress(publisherAddress);
			channel = new SicsChannel(this);
			try {
				channel.connect(serverAddress, publisherAddress);
			} catch (Exception e) {
				return false;
			}
			try {
				String s = channel.syncSend("status", null);
				if (s.contains("=")) {
					s = s.split("=")[1].trim();
				}
				serverStatus = ServerStatus.parseStatus(s);
			} catch (SicsException e) {
				e.printStackTrace();
				throw e;
			}
//			try {
//				batchStatus = BatchStatus.parseStatus(channel.send("exe info", null));
//			} catch (SicsException e) {
//			}
			getGumtreeXML();
			fireConnectionEvent(true);
		} else {
			return reconnect();
		}
		return true;
	}

	public boolean reconnect() throws SicsException {
		if (channel != null && channel.isConnected()) {
			channel.disconnect();
		} 
		channel = new SicsChannel(this);
		try {
			channel.connect(serverAddress, publisherAddress);
		} catch (Exception e) {
			return false;
		}
//		try {
		serverStatus = ServerStatus.parseStatus(channel.syncSend("status", null));
//		} catch (SicsException e) {
//			
//		}
//		try {
//			batchStatus = BatchStatus.parseStatus(channel.send("exe info", null));
//		} catch (SicsException e) {
//		}
		fireConnectionEvent(true);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.gumtree.control.core.ISicsProxy#disconnect()
	 */
	@Override
	public void disconnect() {
		if (channel != null) {
			channel.disconnect();
			fireConnectionEvent(false);
		}
	}

	/* (non-Javadoc)
	 * @see org.gumtree.control.core.ISicsProxy#isConnected()
	 */
	@Override
	public boolean isConnected() {
		return channel != null && channel.isConnected();
	}

	@Override
	public String syncRun(String command) throws SicsException {
		return syncRun(command, null);
	}
	
	/* (non-Javadoc)
	 * @see org.gumtree.control.core.ISicsProxy#send(java.lang.String, org.gumtree.control.core.ISicsCallback, java.lang.String)
	 */
	@Override
	public String syncRun(String command, ISicsCallback callback) throws SicsException {
		if (channel != null && channel.isConnected()) {
			return channel.syncSend(command, callback);
		} else {
			throw new SicsCommunicationException("not connected");
		}
	}

	@Override
	public void asyncRun(String command, ISicsCallback callback) throws SicsException {
		if (channel != null && channel.isConnected()) {
			channel.asyncSend(command, callback);
		} else {
			throw new SicsCommunicationException("not connected");
		}
	}
	
	@Override
	public ISicsChannel getSicsChannel() {
		return channel;
	}

	@Override
	public ServerStatus getServerStatus() {
		return serverStatus;
	}

	@Override
	public void setServerStatus(ServerStatus status) {
		serverStatus = status;
		fireStatusEvent(status);
	}
	
	@Override
	public boolean multiDrive(Map<String, Number> devices) throws SicsException {
		if (devices.size() > 0) {
			String command = "drive";
			for (String key : devices.keySet()) {
				Number value = devices.get(key);
				command += " " + key + " " + value;
			}
			syncRun(command, null);
		}
		return false;
	}
	
	@Override
	public void interrupt() {
		try {
			asyncRun("INT1712 3", null);
			labelInterruptFlag();
		} catch (SicsException e) {
		}
	}
	
	@Override
	public boolean isInterrupted() {
		return isInterrupted;
	}
	
	@Override
	public void labelInterruptFlag() {
		isInterrupted = true;
		fireInterruptEvent(isInterrupted);
	}
	
	@Override
	public void addProxyListener(ISicsProxyListener listener) {
		proxyListeners.add(listener);
	}
	
	@Override
	public void removeProxyListener(ISicsProxyListener listener) {
		proxyListeners.remove(listener);
	}
	
	private void fireInterruptEvent(boolean isInterrupted) {
		for (ISicsProxyListener listener : proxyListeners) {
			listener.interrupt(isInterrupted);
		}
	}
	
	private void fireConnectionEvent(boolean isConnected) {
		if (isConnected) {
			for (ISicsProxyListener listener : proxyListeners) {
				try {
					listener.connect();
				} catch (Exception e) {
					logger.error("failed fire connecting event", e);
				}
			}
		} else {
			for (ISicsProxyListener listener : proxyListeners) {
				try {
					listener.disconnect();
				} catch (Exception e) {
					logger.error("failed fire disconnecting event", e);
				}
			}
		}
	}
	
	private void fireModelUpdatedEvent() {
		for (ISicsProxyListener listener : proxyListeners) {
			try {
				listener.modelUpdated();
			} catch (Exception e) {
				logger.error("failed fire model updating event", e);
			}
		}
		System.err.println("fire model updated");
	}

	private void fireStatusEvent(ServerStatus status) {
		for (ISicsProxyListener listener : proxyListeners) {
			try {
				listener.setStatus(status);
			} catch (Exception e) {
				logger.error("failed fire status changing event", e);
			}
		}
	}
	
	@Override
	public void clearInterruptFlag() {
		isInterrupted = false;
//		fireInterruptEvent(isInterrupted);
	}

	@Override
	public IBatchControl getBatchControl() {
		return batchControl;
	}

	public void addMessageListener(ISicsMessageListener listener) {
		messageListeners.add(listener);
	}
	
	public void removeMessageListener(ISicsMessageListener listener) {
		messageListeners.remove(listener);
	}
	
	public void fireMessageEvent(String message) {
		for (ISicsMessageListener listener : messageListeners) {
			try {
				listener.messageReceived(message);
			} catch (Exception e) {
				logger.error("failed fire message event", e);
			}
		}
	}
	
	public synchronized void getGumtreeXML() {
		if (sicsModel == null) {
			try {
				String msg = channel.syncSend("getgumtreexml /", null);
				if (msg != null) {
					int idx = msg.indexOf("<");
					msg = msg.substring(idx);
	//				try {
	//		            Files.write(Paths.get("C:\\Gumtree\\docs\\GumtreeXML\\new.xml"), msg.getBytes("UTF-8"));
	//		        } catch (IOException e) {
	//		            e.printStackTrace();
	//		        }
					sicsModel = new SicsModel(this);
					sicsModel.loadFromString(msg);
				}
			} catch (SicsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void updateGumtreeXML() {
		try {
			String msg = channel.syncSend("getgumtreexml /", null);
			if (msg != null) {
				int idx = msg.indexOf("<");
				msg = msg.substring(idx);
//				try {
//		            Files.write(Paths.get("C:\\Gumtree\\docs\\GumtreeXML\\new.xml"), msg.getBytes("UTF-8"));
//		        } catch (IOException e) {
//		            e.printStackTrace();
//		        }
				sicsModel = new SicsModel(this);
				sicsModel.loadFromString(msg);
			}
		} catch (SicsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fireModelUpdatedEvent();
	}
	@Override
	public synchronized ISicsModel getSicsModel() {
		return sicsModel;
	}

	@Override
	public ISicsConnectionContext getConnectionContext() {
		return connectionContext;
	}
}
