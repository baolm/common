package com.handpay.obm.common.utils;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * Created by liliu on 2015/8/4.
 */
public class FileUtil {
	private static final Logger log=LoggerFactory.getLogger(FileUtil.class);
	private static final String defaultCharset="GBK";

    public static void writeData(File f,List<String[]> data,boolean isAppend,String charset) throws Exception{

    	if(data==null || data.size()==0)
    		return ;
    	checkFile(f);
        BufferedOutputStream bw2=null;
        try {
            bw2=new BufferedOutputStream(new FileOutputStream(f,isAppend));
        
            for(String l:lines(data)){
            	
                bw2.write(l.getBytes(Charset.forName(getFileCharset(charset,defaultCharset))));
                bw2.write("\r\n".getBytes());
            }
            bw2.flush();
       }finally {
           try {
	            if(bw2!=null)
	                bw2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static String getFileCharset(String charset,String defaultCharset){
    	if(StringUtils.isEmpty(charset))
    		return defaultCharset;
    	return charset;
    }
    
    //д��������
    public static void writeData(File f,String data,boolean isAppend,String charset) throws Exception{
	  	if(StringUtils.isEmpty(data))
	  		return ;
	  	checkFile(f);
	      BufferedOutputStream bw2=null;
	      try {
	          bw2=new BufferedOutputStream(new FileOutputStream(f,isAppend));
	          bw2.write(data.getBytes(Charset.forName(getFileCharset(charset,defaultCharset))));
	          bw2.flush();
	      }finally {
	          try {
	        	  if(bw2!=null)
	        		  bw2.close();
	          } catch (IOException e) {
	              e.printStackTrace();
	          }
	      }
  }

    public static void appendData(File f,List<String[]> data,String charset) throws Exception{
        if(f.exists()){
            writeData(f, data,true,charset);
        }else{
            throw new FileNotFoundException(f.getPath()+" does not exist!");
        }
    }
    
    public static void writeWithTransfer(File f,String data,String charset) throws Exception{
    	writeWithTransferAppend(f,data,charset,false);
    }
    
    public static void writeWithTransferAppend(File f,String data,String charset,boolean isAppend) throws Exception{
    	
    	if(StringUtils.isEmpty(data))
	  		return ;
    	if(isAppend){
	        if(f.exists()){
	        	writeWithTransferAppend(f, data.getBytes(charset));
	        }else{
	            throw new FileNotFoundException(f.getPath()+" does not exist!");
	        }
    	}else{
    	  	checkFile(f);
    	  	writeWithTransferAppend(f, data.getBytes(charset));
    	}
    }
    
    
    public static void writeWithTransferAppend(File file, byte[] data) throws IOException {
         
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        FileChannel toFileChannel = raf.getChannel();
         
       
        ByteArrayInputStream bais = null;
            System.out.println("write a data chunk: " + data.length + "byte");
            bais = new ByteArrayInputStream(data);
            ReadableByteChannel  fromByteChannel = Channels.newChannel(bais);
            long position=toFileChannel.size();
            System.out.println(",position="+position);
            toFileChannel.transferFrom(fromByteChannel, position, data.length);
        toFileChannel.close();
        fromByteChannel.close();
    }
    
    
    public static void appendData(File f,String data,String charset) throws Exception{
        if(f.exists()){
            writeData(f, data,true,charset);
        }else{
            throw new FileNotFoundException(f.getPath()+" does not exist!");
        }
    }
    
    public static void writeData(File f,String data,String charset) throws Exception{
         writeData(f, data,true,charset);
    }
    
    public static void deleteFile(String fileName) throws Exception{
    	File f=new File(fileName);
    	if(f.exists())
    		f.delete();
    	else
    		throw new FileNotFoundException(fileName+" does not exist!");
    }
    
    public static void deleteFileIfExsits(String fileName){
    	File f=new File(fileName);
    	if(f.exists())
    		f.delete();
    }

    public static void createFileAndWriteData(File f,List<String[]> data,String charset) throws Exception{
        createFile(f);
        writeData(f, data, false,charset);
    }

    private static void checkFile(File f){
    	File pf=new File(f.getParent());
    	if(!pf.exists()){
    		log.info("f path"+f.getParent()+" dose not exist");
    		pf.mkdirs();
    		log.info("f path"+f.getParent()+" has created");
    	}
    }
    
    
    private static void createFile(File f){
        File pf=new File(f.getParent());
        if(!pf.exists())
            pf.mkdirs();
//        try {
//            f.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static void createFileAndWriteData(File f,List<String[]> data,List<String[]> titles,String charset) throws Exception{
        createFile(f);
        writeData(f, titles, false,charset);
        writeData(f, data, true,charset);
    }

    public static String[] lines(List<String[]> data){
        String[] buf=new String[data.size()];
        int j=0;
        for(String[] record:data){
            StringBuilder sb=new StringBuilder();

            for(int i=0;i<record.length;i++) {
                if(i>0)
                    sb.append(",");
                sb.append(record[i]);
            }
            buf[j++]=sb.toString();
        }
        return buf;
    }

    public static List<String[]> getTData(){
        List<String[]> l=new ArrayList<String[]>(10);

        try {
			for (int i = 0; i < 10; i++) {
				String[] ld = new String[] {
						"���","�̻�����","��Ʒ����","ƾ֤��","2015/08/0614:09:13"};
				l.add(ld);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return l;
    }

    public static List<String[]> getTitle(){
        List<String[]> l=new ArrayList<String[]>(10);

        for(int i=0;i<2;i++){
            String[] ld=new String[]{"","",i+"-tilte","",""};
            l.add(ld);
        }
        return l;
    }
    
    
    /**
     * ����JSch��ʵ��SFTP���ء��ϴ��ļ�
     * @param ip ����IP
     * @param user ������½�û���
     * @param psw  ������½����
     * @param port ����ssh2��½�˿ڣ����ȡĬ��ֵ����-1
     */
    public static void sshSftp(String ip, String user, String psw ,int port,String uploadPath,String sourceFile,String fileName) throws Exception{
        Session session = null;
        Channel channel = null;
     
         
        JSch jsch = new JSch();
         
         
        if(port <=0){
            //���ӷ�����������Ĭ�϶˿�
            session = jsch.getSession(user, ip);
        }else{
            //����ָ���Ķ˿����ӷ�����
            session = jsch.getSession(user, ip ,port);
        }
     
        //������������Ӳ��ϣ����׳��쳣
        if (session == null) {
            throw new Exception("session is null");
        }
         
        //���õ�½����������
        session.setPassword(psw);//��������   
        //���õ�һ�ε�½��ʱ����ʾ����ѡֵ��(ask | yes | no)
        Properties conf=new Properties();
        conf.put("StrictHostKeyChecking", "no");
        session.setConfig(conf);
        //���õ�½��ʱʱ��   
        session.connect(30000);
             
        try {
            //����sftpͨ��ͨ��
            channel = (Channel) session.openChannel("sftp");
            channel.connect(1000);
            ChannelSftp sftp = (ChannelSftp) channel;
             
             
            //���������ָ�����ļ���
            sftp.cd(uploadPath);
             
            //�г�������ָ�����ļ��б�
//            Vector v = sftp.ls("*.txt");
//            for(int i=0;i<v.size();i++){
//                System.out.println(v.get(i));
//            }
//             
            //���´���ʵ�ִӱ����ϴ�һ���ļ��������������Ҫʵ�����أ��Ի��������Ϳ�����
            OutputStream outstream = sftp.put(fileName);
            InputStream instream = new FileInputStream(new File(sourceFile));
             
            byte b[] = new byte[1024];
            int n;
            while ((n = instream.read(b)) != -1) {
                outstream.write(b, 0, n);
            }
             
            outstream.flush();
            outstream.close();
            instream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.disconnect();
            channel.disconnect();
        }
    }
    
    public static void w(String target,String source) throws FileNotFoundException{
    	InputStream in =null;
		OutputStream out =new FileOutputStream(target);
		try {
			in = new FileInputStream(source);
			byte[] buf = new byte[1024];
		
			int len = 0;
			while ((len = in.read(buf) )!= -1) {
				out.write(buf, 0, len);
			}
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (in != null)
					in.close();
				if (out != null) {
					out.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
				
		}
    }

    public static void main(String[] args){
    	for(int i=0;i<10;i++){
//	    	try {
//	    		
//				w("d:/w"+i+".csv");
//			} catch (FileNotFoundException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
    	}
    	String ip="10.48.171.203"; String user="dev"; String psw="dev" ;int port=22;
//    	try {
//			sshSftp( ip,  user,  psw , port,"");
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//        System.out.print(new Date().getTime()+" ,���룺"+Charset.defaultCharset());
//        try {
//			FileUtil.createFileAndWriteData(new File("d:/csvdata/3t.csv"),getTData(),getTitle());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
       checkFile(new File("E:\\20151015abc\\brand-CardEdenBaseInterface\\src\\1.txt"));
       String a="�̻����ƣ�,��Ѷnew,�˵�ʱ�䣺,2017-11-14 00:00:00~2017-11-15 00:00:00,,,,,,\r\n"+
		"�ɹ��ܽ�,88897,,,,,,,,\r\n"+
		"�ɹ�������,38,ʧ�ܱ�����,28,,,,,,\r\n";
       try {
    	   FileUtil.writeData(new File("E:\\20151015abc\\brand-CardEdenBaseInterface\\src\\abc.txt"), a, false,"GBK");
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
       try {
		RandomAccessFile raf = new RandomAccessFile("E:\\20151015abc\\brand-CardEdenBaseInterface\\src\\���˵���ʽ-2.csv", "rw");
		
		raf.seek(0);
		raf.write(a.getBytes());
		raf.close();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}  
       
//       try {
//		FileUtil.deleteFile("E:\\20151015abc\\brand-CardEdenBaseInterface\\src\\1.txt");
//	} catch (Exception e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//        try {
//            FileUtil.appendData(new File("d:/1t.csv"),getTData());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }
}
