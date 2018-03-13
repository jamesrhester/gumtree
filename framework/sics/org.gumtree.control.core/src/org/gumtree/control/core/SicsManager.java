package org.gumtree.control.core;

import java.io.IOException;

import org.gumtree.control.batch.IBatchControl;
import org.gumtree.control.exception.SicsException;
import org.gumtree.control.imp.SicsProxy;
import org.gumtree.control.model.SicsModel;

public class SicsManager {

	private static ISicsProxy sicsProxy;
	private static ISicsModel sicsModel;
	
	public synchronized static ISicsProxy getSicsProxy(String serverAddress, String publisherAddress) {
		if (serverAddress != null) {
			if (sicsProxy != null && sicsProxy.isConnected()) {
				sicsProxy.disconnect();
			}
			sicsProxy.connect(serverAddress, publisherAddress);
		}
		return sicsProxy;
	}
	
	public static ISicsProxy getSicsProxy() {
			return sicsProxy;
	}

	public static ISicsModel getSicsModel() {
			if (sicsModel == null) {
				ISicsChannel channel = sicsProxy.getSicsChannel();
				try {
					String msg = channel.syncSend("getgumtreexml /", null);
					if (msg != null) {
						sicsModel = new SicsModel();
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
		return sicsModel;
	}
	
	public static IBatchControl getBatchControl() {
		return sicsProxy.getBatchControl();
	}

	static {
		sicsProxy = new SicsProxy();
	}
	
	
}
