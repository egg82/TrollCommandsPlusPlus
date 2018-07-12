package me.egg82.tcpp.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import ninja.egg82.exceptionHandlers.IExceptionHandler;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.utils.ChannelUtil;

public class TrollChannelUtil {
	//vars
	
	//constructor
	public TrollChannelUtil() {
		
	}
	
	//public
	public static void broadcastTroll(String args) {
		if (args == null) {
			throw new IllegalArgumentException("args cannot be null.");
		}
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		
		try {
			out.writeUTF(args);
		} catch (Exception ex) {
			ServiceLocator.getService(IExceptionHandler.class).silentException(ex);
			ex.printStackTrace();
			return;
		}
		
		ChannelUtil.broadcastToServers("Troll", stream.toByteArray());
	}
	
	//private
	
}