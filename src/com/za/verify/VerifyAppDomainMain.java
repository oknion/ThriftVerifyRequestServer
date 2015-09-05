package com.za.verify;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;

public class VerifyAppDomainMain {
	static VerifyAppDomainHandler handler;
	static VerifyAppDomainService.Processor processor;

	public static void main(String[] args) {
		try {
			BasicConfigurator.configure();

			Properties prop = new Properties();
			FileInputStream in = new FileInputStream("prop.properties");
			prop.load(in);
			in.close();
			handler = new VerifyAppDomainHandler();
			processor = new VerifyAppDomainService.Processor(handler);
			TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(
					Integer.parseInt(prop.getProperty("port")));
			TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
			tArgs.transportFactory(new TFramedTransport.Factory());
			tArgs.protocolFactory(new TBinaryProtocol.Factory());
			tArgs.processor(processor);
			tArgs.selectorThreads(4);
			tArgs.workerThreads(4);
			TServer server = new TThreadedSelectorServer(tArgs);
			System.out.println("Starting the TThreadedSelectorServer server...");
			server.serve();

		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
