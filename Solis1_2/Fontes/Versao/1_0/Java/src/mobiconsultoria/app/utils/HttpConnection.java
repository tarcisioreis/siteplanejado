package mobiconsultoria.app.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import mobiconsultoria.app.beans.DadosBean;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpConnection {
	public static String getSetDataWeb(DadosBean dadosBean){
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(dadosBean.getTxUrl());
		String retorno = "";
		
		try{
			ArrayList<NameValuePair> valores = new ArrayList<NameValuePair>();
			valores.add(new BasicNameValuePair("method", dadosBean.getTxMethod()));
			valores.add(new BasicNameValuePair("nome", dadosBean.getTxNome()));
			valores.add(new BasicNameValuePair("email", dadosBean.getTxEmail()));
			valores.add(new BasicNameValuePair("telefone", dadosBean.getTxTelefone()));
			
			valores.add(new BasicNameValuePair("img-mime", dadosBean.getImage().getMime()));
			valores.add(new BasicNameValuePair("img-image", dadosBean.getImage().getBitmapBase64()));
						
			httpPost.setEntity(new UrlEncodedFormEntity(valores));
			HttpResponse resposta = httpClient.execute(httpPost);
			retorno = EntityUtils.toString(resposta.getEntity());
		}
		catch (UnsupportedEncodingException e) { e.printStackTrace(); }
		catch (ClientProtocolException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }
		
		return(retorno);
	}
}
