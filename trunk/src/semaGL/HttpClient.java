package semaGL;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpClient {


	public  boolean storeStream(String url_, String filename){
		if (url_=="") return false;

		try{
			URL u = new URL(url_);
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			huc.setRequestMethod("GET");
			huc.connect();

			BufferedInputStream bis = new BufferedInputStream( huc.getInputStream() );
			FileOutputStream fos = new FileOutputStream( filename );
			byte[] bytes = new byte[ 8192 ];
			int count = bis.read( bytes );
			while( count != -1 && count <= 8192 ) {
				System.out.print( "-" );
				fos.write( bytes, 0, count );
				count = bis.read( bytes );
			}
			if( count != -1 ) {
				fos.write( bytes, 0, count );
			}
			fos.close();
			bis.close();
			huc.disconnect();
			return true;
			
		} catch (IOException ioe){
			System.err.println("Unable to connect to '" + url_ + "'");
			return false;
		}
	}
	public String getPage(String url){
		int code=0;
//		byte[] contents = null;
		String content = null;
		try  { 
			URL u = new URL(url);
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			huc.setRequestMethod("GET");
			huc.connect();
//			InputStream is = huc.getInputStream (  ) ; 
			InputStreamReader isr = new InputStreamReader( huc.getInputStream(),"UTF8" );
			BufferedReader br = new BufferedReader(isr);
			StringBuffer r = new StringBuffer();
			code = huc.getResponseCode();
			String line;
			if (code>=200&&code<=300&&code!=204&&code!=203)
			{
				while ( (line = br.readLine()) !=null) r.append(line+"\n");
//				int length = is.available();
//				System.out.println(length);
//				contents = new byte[length];
//				is.read(contents);
			}
			content=r.toString();
			br.close();
			huc.disconnect();
		}
		catch  ( IOException e )   {   
			System.out.println ( "Exception\n" + e ) ; 
		}  
//		if (contents!=null) return new String(contents);
		return content;
	}
}
