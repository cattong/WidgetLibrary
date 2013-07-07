package com.shejiaomao.core.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * This socket factory will create SSL socket that accepts self signed
 * certificate
 */
final class LibSSLSocketFactory implements LayeredSocketFactory {

	private static LibSSLSocketFactory DEFAULT_FACTORY;
	
	static {
		TrustManager trustAllCerts =
		        new X509TrustManager() {
		            public X509Certificate[] getAcceptedIssuers() {return null; }
		            public void checkClientTrusted(X509Certificate[] certs, String authType) {}
		            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
		        };
        try {
        	SSLContext sslcontext = SSLContext.getInstance(SSLSocketFactory.TLS);
			sslcontext.init(null, new TrustManager[] {trustAllCerts}, null);
			DEFAULT_FACTORY = new LibSSLSocketFactory(sslcontext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private SSLContext sslcontext = null;

	private LibSSLSocketFactory(SSLContext sslcontext) {
		this.sslcontext = sslcontext;
	}

    /**
     * Gets an singleton instance of the SSLProtocolSocketFactory.
     * @return a SSLProtocolSocketFactory
     */
    public static LibSSLSocketFactory getSocketFactory() {    	
        return DEFAULT_FACTORY;
    }

	@Override
	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {
		return sslcontext.getSocketFactory().createSocket(socket, host,
				port, autoClose);
	}
	
	@Override
	public Socket connectSocket(Socket sock, String host, int port,
			InetAddress localAddress, int localPort, HttpParams params)
			throws IOException, UnknownHostException, ConnectTimeoutException {
		int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
		int soTimeout = HttpConnectionParams.getSoTimeout(params);

		InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
		SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket());

		if ((localAddress != null) || (localPort > 0)) {
			// we need to bind explicitly
			if (localPort < 0) {
				localPort = 0; // indicates "any"
			}
			InetSocketAddress isa = new InetSocketAddress(localAddress,
					localPort);
			sslsock.bind(isa);
		}

		sslsock.connect(remoteAddress, connTimeout);
		sslsock.setSoTimeout(soTimeout);
		
		return sslsock;
	}

	public Socket createSocket() throws IOException {
		return sslcontext.getSocketFactory().createSocket();
	}

	public boolean isSecure(Socket socket) throws IllegalArgumentException {
		return true;
	}

	public boolean equals(Object obj) {
		return ((obj != null) && obj.getClass().equals(
				LibSSLSocketFactory.class));
	}

	public int hashCode() {
		return LibSSLSocketFactory.class.hashCode();
	}

}
