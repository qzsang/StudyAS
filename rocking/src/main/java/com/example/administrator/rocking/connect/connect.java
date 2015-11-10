package com.example.administrator.rocking.connect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class connect {


		// TODO Auto-generated method stub
	public void con(String data1){
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL readURL = new URL("http://192.168.191.1:8080/test/connect_list");
			
			URLConnection connect = readURL.openConnection();

			
			connect.setDoOutput(true);
			connect.setDoInput(true);

			connect.setReadTimeout(3000);
			//BufferedOutputStream
			out = new PrintWriter(connect.getOutputStream());
			out.write("data="+URLEncoder.encode(data1, "utf-8"));
			out.flush();
			
			in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			
			String line = null;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}

}
