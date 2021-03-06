package com.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class Test {

	public static void main(String[] args) {

		try {
			//url
			String url = "http://119.29.36.20/dgutlink/api/device/21/sensor/14/datapoint";
			//post请求
			HttpPost httpPost = new HttpPost(url);
			//添加请求头
			httpPost.addHeader("APIKEY", "6000000620879617");
			//设置请求参数
			//HttpParams params = new BasicHttpParams();
			//params.setIntParameter("value", 123);
			//httpPost.setParams(params);
			//JSONObject paramJson=new JSONObject();
		   // paramJson.put("value","321");
			//httpPost.setEntity(new StringEntity(paramJson.toString(), Charset.forName("UTF-8")));
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("value", "102");
			
			List<NameValuePair> pairs = null;
		    if (params != null && !params.isEmpty()) {
		        pairs = new ArrayList<NameValuePair>(params.size());
		        for (String key : params.keySet()) {
		            pairs.add(new BasicNameValuePair(key, params.get(key).toString()));
		        }
		    }
		    if (pairs != null && pairs.size() > 0) {
		        httpPost.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
		    }
			httpPost.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
		
			//执行请求
			HttpClient client = HttpClients.createDefault();
			HttpResponse response =  client.execute(httpPost);
			//打印结果
			System.out.println(EntityUtils.toString(response.getEntity()));
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
