package com.example.administrator.rocking.connect;

import android.content.Context;

import com.example.administrator.rocking.application.Constant;
import com.example.administrator.rocking.bean.HeartBean;
import com.example.administrator.rocking.bean.WeightBean;

import net.tsz.afinal.FinalDb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class connect {
	Context context;
	public connect (Context context) {
		this.context = context;
	}
		// TODO Auto-generated method stub
	public void con(String data1){
		//保存数据库

		saveData(data1);
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL readURL = new URL("http://192.168.191.1:8080/Rocking/system/elecMenuAction_save.do");
			
			URLConnection connect = readURL.openConnection();

			
			connect.setDoOutput(true);
			connect.setDoInput(true);

			connect.setReadTimeout(3000);
			//BufferedOutputStream
			out = new PrintWriter(connect.getOutputStream());
			out.write("data=" + URLEncoder.encode(data1, "utf-8") + "&username=" + Constant.username);
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
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	public void saveData(String data) {

		System.out.println(data);
		data = data.trim();
		char data_type = data.charAt(0);
		data = data.substring(1, data.length());
		String dataStr = data.substring(data.lastIndexOf(":")+1, data.length());
		switch (data_type) {
			case 'w':
				WeightBean w = new WeightBean();
				w.setData(dataStr);
				w.setUsername(Constant.username);
				w.setData(df.format(new Date()));

				FinalDb.create(context).save(w);
				break;
			case 'B':
				HeartBean b = new HeartBean();
				b.setHdate(dataStr);
				b.setUsername(Constant.username);
				b.setHtime(df.format(new Date()));
				if (context != null)
					FinalDb.create(context).save(b);
				break;

		}
	}

}
