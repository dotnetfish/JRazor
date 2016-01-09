package com.superstudio.commons.io;

import java.io.*;

public class File {

	public static void WriteAllText(String outputPath, String string) throws IOException {
		
		FileWriter fw = new FileWriter(outputPath);

		long begin3 = System.currentTimeMillis();

		fw.write(string);

		fw.close();

	}
	
	public static AutoCloseable OpenRead(String fileName) throws FileNotFoundException{
		return new FileInputStream(fileName);
	}
	
	public static AutoCloseable OpenRead(String[] fileNames) throws FileNotFoundException{
		FileInputStream[] readers=new FileInputStream[fileNames.length];
		int i=0;
		for(String str :fileNames){
			readers[i]=new FileInputStream(fileNames[i]);
		}
		
		return new AutoCloseable(){

			@Override
			public void close() throws Exception {
				// TODO Auto-generated method stub
				for(FileInputStream stream:readers){
					stream.close();
				}
			}
			
		};
	}

	public static FileStream Open(String file, FileMode open, FileAccess read, FileShare share) throws FileNotFoundException {
		// TODO Auto-generated method stub
		return new FileStream(file);
	}
    public  static  String readAll(String path,String encoding)throws  IOException{
        StringBuffer sbread = new StringBuffer();
        try(InputStreamReader isr = new InputStreamReader(new FileInputStream(path), encoding)){
            while (isr.ready()) {
                sbread.append((char) isr.read());
            }
            isr.close();
        }



        return  sbread.toString();
    }
    public  static  String readAll(String path)throws  IOException{
       return  readAll(path,"UTF-8");
    }
	public static byte[] ReadAllBytes(String path) throws IOException{
		StringBuffer sbread = new StringBuffer();
		try(InputStreamReader isr = new InputStreamReader(new FileInputStream(path), "UTF-8")){
			while (isr.ready()) {
				sbread.append((char) isr.read());
			}
			isr.close();
		}



return  sbread.toString().getBytes();
		/*try(FileInputStream stream=new FileInputStream(path)){
			byte[] bytes=new byte[stream.available()];
			
			stream.read(bytes);
			return bytes;
		}*/
		//return new byte[1];
			
		
	}

	public static byte[] ReadAllBytes(java.io.File file) throws IOException{

		try(FileInputStream stream=new FileInputStream(file)){
			byte[] bytes=new byte[stream.available()];

			stream.read(bytes);
			return bytes;
		}
		//return new byte[1];


	}

	//public  static byte
	public static boolean Exists(String text2) {
		// TODO Auto-generated method stub
		java.io.File  file=new java.io.File(text2);
		return file.exists();
	}

	public static void Delete(String outputAssembly) {
		// TODO Auto-generated method stub
		
	}

}
