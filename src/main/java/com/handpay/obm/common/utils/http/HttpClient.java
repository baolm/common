package com.handpay.obm.common.utils.http;



import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpClient
{
	
	private static Logger log=Logger.getLogger(HttpClient.class);
	//���ݵķ�װ���䷽
	public static final int  TRANSMODE_BODY=1;//ֱ������body����
	public static final int  TRANSMODE_FORM=2;//ʹ��(����Form)����(����,����)�ύ��ʽ
	//HTTP��������
	public static final String METHOD_POST="POST";
	public static final String METHOD_PUT="PUT";
	public static final String METHOD_GET="GET";
	public static final String METHOD_DELETE="DELETE";
	//��
	private org.apache.http.client.HttpClient httpClient;
	private HttpRequestBase httpRequest;
	private int status;		 
	private int transMode;	
	private String url;
	private String method;
	
	//�������  ���� ��ʽform�ύ
	private List<NameValuePair> paramsList=new ArrayList<NameValuePair>();
	//����body
	private String body=null; 
	//���ô���
	private HttpHost httpHost = null;
	
	public HttpClient()
	{		
	}	
	public HttpClient(String method)
	{		
		this.method=method;
	}	
	public HttpClient(int transMode)
	{		
		this.transMode=transMode;
	}		
	public HttpClient(int transMode,String method)
	{		
		this.transMode=transMode;
		this.method=method;
	}	
	
	public HttpClient(String url,int transMode,String method)
	{
		this.url=url;
		this.transMode=transMode;
		this.method=method;
	}	
	public void setUrl(String url)
	{
		this.url=url;
	}
	
	public void setTransMode(int transMode)
	{
		this.transMode=transMode;
	}	
	public void setBody(String body)
	{
		this.body=body;
	}
	public void setMethod(String method)
	{
		this.method=method;
	}

	/**
	 * ���� form�����������
	 * @param param
	 */
	public void setNameValuePair(Map<String,String> param)
	{
		if(param==null)
			return;
		Set keySet=param.keySet();
		String key="";		
		for(Iterator<String> it=keySet.iterator();it.hasNext();)
		{
			key=it.next();
			if(key!=null && !key.trim().equals(""))
			{
				paramsList.add(new BasicNameValuePair(key, param.get(key)));
			}
		}		
	}
	/**
	 * �õ���Ӧ״
	 * @return
	 */
	public int getStatus(){
		return this.status;
	}
	/**
	 * ���ʹ�� ��Ӧ
	 * @return
	 * @throws Exception 
	 */
	public InputStream getResponseStream() throws Exception
	{
		try{
			ResponseHandler<InputStream> handler = new ResponseHandler<InputStream>()
			{
				public InputStream handleResponse(HttpResponse response) throws ClientProtocolException, IOException
		            {
						status=response.getStatusLine().getStatusCode();
				        HttpEntity entity = response.getEntity();
				        if (entity != null) 
				        {	
				            return entity.getContent();	            
				        } else {
				            return null;
				        }
		            }
			};		
			Object obj=getResponse(handler);
			if(status==HttpStatus.SC_OK)
			{				
				return (InputStream)obj;
			}else{
				throw new Exception("http status:"+status+", deal with request failed!"); 	
			}				
		}catch(Exception ex)
		{
			throw ex;
		}		
	}
	/**
	 * ���ʹ����Ӧ�ַ�
	 * @return
	 * @throws Exception 
	 */
	public String getResponseString() throws Exception
	{
		try{
			ResponseHandler<String> handler = new ResponseHandler<String>()
			{
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException
		            {
						status=response.getStatusLine().getStatusCode();
				        HttpEntity entity = response.getEntity();			        
				        if (entity != null) 
				        {	
				            return EntityUtils.toString(entity);		            
				        } else {
				            return null;
				        }
		            }
			};		
			Object obj=getResponse(handler);
			if(status==HttpStatus.SC_OK)
			{
				return obj.toString();
			}else{
				throw new Exception("http status:"+status+", deal with request failed! error:"+obj);  	
			}														
		}catch(Exception ex)
		{				
			throw ex;
		}
				
	}
	/**
	 *	���ʹ����Ӧ�ֽ�
	 * @return
	 * @throws Exception 
	 */
	public byte[] getResponseByte() throws Exception
	{
	 try{
		ResponseHandler<byte[]> handler = new ResponseHandler<byte[]>()
		{
			public byte[] handleResponse(HttpResponse response) throws ClientProtocolException, IOException
	            {
					status=response.getStatusLine().getStatusCode();
			        HttpEntity entity = response.getEntity();
			        if (entity != null) 
			        {				 
			            return EntityUtils.toByteArray(entity);		            
			        } else {
			            return null;
			        }
	            }
		};				
		Object obj=getResponse(handler);
		if(status==HttpStatus.SC_OK)
		{
			 return (byte[])obj; 
		}else{
			throw new Exception("http status:"+status+", deal with request failed!"); 
		}														
	}catch(Exception ex)
	{
		throw ex;	
	}		
}
	
	
	/**
	 * �ر�����
	 */
	public void destroy()
	{
		if(httpClient!=null)
			httpClient.getConnectionManager().shutdown();
	}

	/**
	 * ���������в�
	 * ��ͷ
	 * ���ݲ���
	 */
	private void setRequestParam()
	{
		if(url==null || url.trim().equals(""))
		{
			throw new RuntimeException("url is null");
		}
		httpClient=new DefaultHttpClient();
		//�������Ӻ��Ĳ���
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,HttpVersion.HTTP_1_1);		
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,HTTP.UTF_8);
		httpClient.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE,Boolean.FALSE);		
		//���ò���
		 
		StringEntity entity=null;
		try
		{
			
			if(this.transMode==TRANSMODE_BODY)
			{
				if(this.body!=null)										
					entity=new StringEntity(this.body,HTTP.UTF_8);
			}
			else
			{//���� ʹ��(����Form
				entity =new UrlEncodedFormEntity(paramsList,HTTP.UTF_8);
			}
		}
		catch(UnsupportedEncodingException e)
		{
			log.error(e.getMessage(), e); 
			throw new RuntimeException("param UnsupportedEncodingException");
		}
		if(method==null)
			method=METHOD_POST;
		
		if(method.equals(METHOD_PUT))
		{//put������
			httpRequest = new HttpPut(url);
			((HttpEntityEnclosingRequestBase)httpRequest).setEntity(entity);	
		}else if(method.equals(METHOD_GET))
		{//get������
			httpRequest = new HttpGet(url);
		}else if(method.equals(METHOD_DELETE))
		{//delete������
			httpRequest=new HttpDelete(url);			
		}else
		{//Ĭ��ʹ��post����
			httpRequest=new HttpPost(url);
		   ((HttpEntityEnclosingRequestBase)httpRequest).setEntity(entity);
		}						 
		
		//��������ͷ �����������ر�
		httpRequest.setHeader("Connection","close");				
			
	}	
	/**
	 * ��post���� ��������Ӧ��  �ص�����
	 * @param handler
	 * @return
	 */
	private Object getResponse(ResponseHandler handler){		
		setRequestParam();
		Object response=null;
		try
		{
			if(httpHost!=null){
				response= httpClient.execute(httpHost, httpRequest, handler);
			}else{
				response= httpClient.execute(httpRequest, handler);
			}
			 
			return response;
		}
		catch(Exception e)
		{	
			log.error(e.getMessage(), e); 	
			throw new RuntimeException("httpClient send request error:"+e.getMessage()); 			
		}
		finally
		{
			if(httpClient!=null)
				httpClient.getConnectionManager().shutdown();			
		}
	}
	
	public void setProxy(String url, int port){
		httpHost = new HttpHost(url, port);
	}
	
	/*@channelCode Ϊ��������
	 * @domain��Ӧ����������  
	 * @pageType  Ϊҳ������    index ��ҳ    second  Ϊ����ҳ��
	 * @platFormType Ϊƽ̨����     ipadΪipadƽ̨     iphoneΪ��ҳ��ƽ̨     wapΪ���ƽ̨���ݲ�֧�֣�
	 * 
	 */
	public static String getPublisthData(String channelCode,String domain,String pageType,String platFormType,String secondConfigId){
//		if (StringUtils.isBlank(channelCode)) {
//			log.error("�������벻��Ϊ�գ�");
//			throw new IndexConfigServiceException(
//					IndexConfigErrorCode.E_MESSAGE, "�������벻��Ϊ�գ�");
//		}
//		if (StringUtils.isBlank(domain)) {
//			log.error("������������Ϊ�գ�");
//			throw new IndexConfigServiceException(
//					IndexConfigErrorCode.E_MESSAGE, "������������Ϊ�գ�");
//		}
//		if (StringUtils.isBlank(platFormType)) {
//			log.error("ƽ̨���Ͳ���Ϊ�գ�");
//			throw new IndexConfigServiceException(
//					IndexConfigErrorCode.E_MESSAGE, "ƽ̨���Ͳ���Ϊ�գ�");
//		}
		if (domain.indexOf("http://") < 0) {
			domain = "http://" + domain;
		}
		StringBuilder url = new StringBuilder(domain);
		String pattenValue = "";
		if ("second".equals(pageType)) {
//			if (StringUtils.isBlank(secondConfigId)) {
//				log.error("���������ҳ�������ID��");
//				throw new IndexConfigServiceException(
//						IndexConfigErrorCode.E_MESSAGE, "���������ҳ�������ID��");
//			}
//			if (!StringUtils.isNumeric(secondConfigId)) {
//				log.error("����ҳ�������ID����Ϊ���֣�");
//				throw new IndexConfigServiceException(
//						IndexConfigErrorCode.E_MESSAGE, "����ҳ�������ID����Ϊ���֣�");
//			}
			url.append("/wap/puzzleSecondView.do?secondConfigId=" + secondConfigId);
			pattenValue = "id\\=\"(smb|mb)_[0-9]+";
		} else {
			url.append("/wap/puzzleIndexView.do");
			pattenValue = "id\\=\"mb_[0-9]+";
		}
		HttpClient client = new HttpClient();
		client.setUrl(url.toString());
		client.setMethod(HttpClient.METHOD_GET);
		String value = "";
		StringBuilder contentValue = new StringBuilder("");
		try {
			value = client.getResponseString();
			// �õ�ģ���ID
			Pattern pattern = Pattern.compile(pattenValue);
			Matcher matcher = pattern.matcher(value);
			int navFlag=1;
			String navContent="";
			while (matcher.find()) {
				String idStr = matcher.group(0);
				String[] ids = idStr.split("_");
				String idurl = "";
				if ("second".equals(pageType)) {
					if ("id=\"smb".equals(ids[0])) {
						idurl = domain
								+ "/wap/querySecondPublish.do?configTemplateId="
								+ ids[1]
								+ "&configUserId=&configCartNum=";
					} else {
						idurl = domain
								+ "/wap/queryPublish.do?configTemplateId="
								+ ids[1] + "&configUserId=&configCartNum=";
					}
				} else {
					idurl = domain + "/wap/queryPublish.do?configTemplateId="
							+ ids[1] + "&configUserId=&configCartNum=";
				}
				if("id=\"smb".equals(ids[0])){
					navFlag++;
				}
				
				if(navFlag==2){
					Pattern pattern1 = Pattern.compile("<button class=\"button absolute width40 top-title\"><span class=\"title\">.*</span></button>",Pattern.DOTALL);
					Matcher matcher1 = pattern1.matcher(value);
					if(matcher1.find()){
						navContent="<div class=\"module_2nd\"><div class=\"select-content relative\"><button id=\"top-prev\" onclick=\"history.back()\" class=\"button absolute top-prev\" type=\"button\"><i class=\"ico prev\" style=\"background:url(http://pic.99wuxian.com/basic/wap/img/iphone/puzzle/ico/ico_new.png); background-size:250px 250px; background-position: 0 0;\"></i></button>"+matcher1.group(0)+"</div></div>";
					}
					contentValue.append(navContent);
				}
				HttpClient client2 = new HttpClient();
				client2.setUrl(idurl);
				client2.setMethod(HttpClient.METHOD_GET);
				String value2 = client2.getResponseString();
				String padTopStartTxt="<div id=\"puzzle_pad_top_container\" class=\"puz_nav_div\" style=\"position:fixed;left:0;top:0;width:100%;\" >";
				String padContainerEndTxt="</div></div>";
				String padContainerStartTxt="<div class=\"com-container com-relative\"><div class=\"com-user-container com-relative\">";
				if("ipad".equals(platFormType)){
					if(value2.contains("\"puzzle_pad_top\"")){
						if(!contentValue.toString().contains(padTopStartTxt)){
							contentValue.append(padTopStartTxt+value2+"</div>");						
						}
						if(!contentValue.toString().contains(padContainerStartTxt)){
							contentValue.append(padContainerStartTxt);
						}
					}else if(value2.contains("\"puzzle_pad_buttom\"")){
						if(!contentValue.toString().contains(padContainerEndTxt)){
							contentValue.append(padContainerEndTxt+value2);					
						}
					}else{
						contentValue.append(value2);
					}
				}else{
					contentValue.append(value2);
				}
			}

			// �õ��滻��ҳģ�������
			String reg = "";
			if ("second".equals(pageType)) {
				reg = "<div class=\"com-head\">.*</body>";
			} else {
				reg = "<section.*</body>";
			}

			// ��Ҫ��$����ת�崦��
			// value = value.replace("$", "##");
			value = value.replace("$", "a");
//			System.out.println(value);
			Pattern pattern1 = Pattern.compile(reg, Pattern.DOTALL);
			Matcher matcher1 = pattern1.matcher(value);
			StringBuffer sb = new StringBuffer();
			if (matcher1.find()) {
				matcher1.appendReplacement(sb, ""); // �滻��������group()
			}
			if(domain.contains("iicbc.99wuxian.com")){
				sb.append("<script src=\"http://pic.99wuxian.com/basic/wap/js/source/iphone/old/iicbc/icbc_core.js?v=20120907.js\"></script><div id=\"iicbc_m\" class=\"com-head\"><div class=\"nrdh_l\"><font class=\"l_color\">�����̳�</font>");
				sb.append("<a href=\"javascript:void(0);\" class=\"last\" onClick=\"ICBCUtil.returnBack()\"><img src=\"http://pic.99wuxian.com/basic/wap/img/iphone/old/iicbc/back.png\" width=\"55\" height=\"30\"></a></div></div>");
			}
			String lastValue = sb.toString()
			+ "<script src=\"http://pic.99wuxian.com/basic/wap/js/source/plugs/sea/sea.js\" charset=\"utf-8\" ></script>"
			+ contentValue.toString() + "<script>seajs.use([\"http://pic.99wuxian.com/basic/wap/js/source/iphone/puzzle/puzzle.js\"],function(obj){obj.Puzzle.puzzleGetLoginInfo();});seajs.use([\"http://pic.99wuxian.com/basic/wap/js/source/common/seaLazyload\"],function(lazyLoadImg){	if(typeof(lazyLoadImg)===\"function\"){lazyLoadImg();	setInterval(function(){lazyLoadImg();},5000);	}});</script></body></html>";
			return lastValue.toString();
		} catch (Exception e) {
			log.error("������ַʧ�������ԣ�", e);
//			throw new IndexConfigServiceException(
//					IndexConfigErrorCode.E_MESSAGE, "����ʧ�ܻ������ַʧ�������ԣ������ַ�Ƿ����Ҫ��");
		}
		return "";
	}
		
	private static void testCDORequest() throws Exception
	{
		HttpClient httpClient=new HttpClient(TRANSMODE_FORM);
		httpClient.setUrl("http://business.mall.woyo.com/handletrans.woyoJson?strTransName=getGoodsList");		
		Map paramMap=new HashMap();		
		String strJson="{\"nPageIndex\":1,\"nPageSize\":10,\"strScene\":\"buyer\"," +
				"\"lCategoryId\":2017," +
				"\"nCategoryLevel\":2" +
				"}";
				
		
		httpClient.setTransMode(TRANSMODE_FORM);
				
		paramMap.put("$$CDORequest$$",strJson);
		httpClient.setNameValuePair(paramMap);
		//System.out.println(httpClient.getResponseString());						
	}


	public static void  main(String[] args){
		try{
			
//			testCDORequest();
//			search();
//			testLength();
		}catch(Exception ex)
		{
			//log.debug(e.getMessage(), ex); 
		}
	}	

	public static void testLength() throws Exception{
		HttpClient httpClient=new HttpClient(TRANSMODE_BODY,METHOD_GET);
		httpClient.setUrl("http://127.0.0.1:8080/BusinessCenter/handletrans.woyoJson?a0=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a1=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a2=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a3=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a4=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a5=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a6=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a7=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a8=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a9=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a10=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a11=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a12=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a13=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a14=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a15=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a16=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a17=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a18=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a19=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a20=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a21=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a22=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a23=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a24=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a25=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a26=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a27=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a28=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a29=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a30=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a31=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a32=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a33=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a34=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a35=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a36=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899&a37=0123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899");
		//System.out.println(httpClient.getResponseString());

	}
	
	private static void search() throws Exception
	{
		HttpClient httpClient=new HttpClient(TRANSMODE_BODY,METHOD_POST);
		httpClient.setUrl("http://hadoop2:9200/MallGoods/goods/_search");
		String strJson="{\"fields\" : [\"lGoodsId\"],\"query\" :"+						
		" {"+ 
		"\"term\" : { \"lGoodsId\" :31885837205505}"+ 
	    " }" +
	    "}";
		httpClient.setBody(strJson);
		
		//System.out.println(httpClient.getResponseString());			
	}
	 
	
	
}
