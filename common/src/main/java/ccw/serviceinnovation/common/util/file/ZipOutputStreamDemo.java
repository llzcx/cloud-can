package ccw.serviceinnovation.common.util.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.util.zip.Deflater;

public class ZipOutputStreamDemo {
   private static String SOURCE_FILE = "D:\\test\\Hello.txt";
   private static String TARGET_FILE = "D:\\test\\Hello.zip";

   public static void main(String[] args) {
      try {
         createZipFile();
         readZipFile();
      } catch(IOException ioe) {
         System.out.println("IOException : " + ioe);
      }
   }

   private static void createZipFile() throws IOException{
      FileOutputStream fout = new FileOutputStream(TARGET_FILE);
      CheckedOutputStream checksum = new CheckedOutputStream(fout, new Adler32());
      ZipOutputStream zout = new ZipOutputStream(checksum);
      zout.setLevel(Deflater.DEFAULT_COMPRESSION);
      FileInputStream fin = new FileInputStream(SOURCE_FILE);
      ZipEntry zipEntry = new ZipEntry(SOURCE_FILE);
      zout.putNextEntry(zipEntry);
      int length;
      byte[] buffer = new byte[1024];
      while((length = fin.read(buffer)) > 0) {
         zout.write(buffer, 0, length);
      }

      zout.closeEntry();
      zout.finish();
      fin.close();
      zout.close();
   }

   private static void readZipFile() throws IOException{
      ZipInputStream zin = new ZipInputStream(new FileInputStream(TARGET_FILE)); 

      ZipEntry entry;
      while((entry = zin.getNextEntry())!=null){
         System.out.printf("File: %s Modified on %TD %n", 
         entry.getName(), new Date(entry.getTime()));
         extractFile(entry, zin); 
         System.out.printf("Zip file %s extracted successfully.", SOURCE_FILE);
         zin.closeEntry();
      }
      zin.close();
   }

   private static void extractFile(final ZipEntry entry, ZipInputStream is) 
      throws IOException {
      FileOutputStream fos = null; 
      try { 
         fos = new FileOutputStream(entry.getName()); 
         while(is.available() != 0){
            fos.write(is.read()); 
         }
      } catch (IOException ioex) { 
         fos.close(); 
      } 
   }
}