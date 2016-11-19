package lamp;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;


import lamp.Color;

public class Main {

	public static void main(String[] args) throws IOException {
		String fileName = getFileName();
		BufferedImage image = getImage(fileName);
		image = monochromize(image);
		int[][] table = imageToTableSlow(image);
		int[][] raster = createRaster(table.length, table[0].length);
		prepareImage(table);
		cutInRaster(table, raster);
		pasteInRaster(table, raster);
		cleanRaster(raster);
		createImage(raster);
	}
	
	private static BufferedImage monochromize(BufferedImage image){
            BufferedImage blackWhite = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
            Graphics2D g2d = blackWhite.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();
            return blackWhite;
	}
	
	private static void prepareImage(int[][] image){
		int width = image.length;
		int height = image[0].length;
		for(int i = 0; i < width; i++) {
		    for(int j = 0; j < height; j++) {
		       if(image[i][j] == -16777216){
		    	   boolean only = true;
		    	   if(i-1 >= 0 && j-1 >= 0 && image[i-1][j-1] <= -16777216){
		    		   only = false;
		    	   }else if(i-1 >= 0 && image[i-1][j] <= -16777216){
		    		   only = false;
		    	   }else if(i-1 >= 0 && j+1 < height && image[i-1][j+1] <= -16777216){
		    		   only = false;
		    	   }else if(j-1 >= 0 && image[i][j-1] <= -16777216){
		    		   only = false;		    	   
		    	   }else if(j+1 < height && image[i][j+1] <= -16777216){
		    		   only = false;
		    	   } else if(i+1 < width && j-1 >= 0 && image[i+1][j-1] <= -16777216){
		    		   only = false;
		    	   }else if(i+1 < width && image[i+1][j] <= -16777216){
		    		   only = false;
		    	   }else if(i+1 < width && j+1 < height && image[i+1][j+1] <= -16777216){
		    		   only = false;
		    	   }
		    	   if(only){
		    		   image[i][j] = -1;
		    	   }
		       } else {
		    	   boolean withNearby = false;
		    	   if(i-1 >= 0 && j-1 >= 0 && image[i-1][j-1] <= -16777216){
		    		   withNearby = true;
		    	   }else if(i-1 >= 0 && image[i-1][j] <= -16777216){
		    		   withNearby = true;
		    	   }else if(i-1 >= 0 && j+1 < height && image[i-1][j+1] <= -16777216){
		    		   withNearby = true;
		    	   }else if(j-1 >= 0 && image[i][j-1] <= -16777216){
		    		   withNearby = true;		    	   
		    	   }else if(j+1 < height && image[i][j+1] <= -16777216){
		    		   withNearby = true;
		    	   } else if(i+1 < width && j-1 >= 0 && image[i+1][j-1] <= -16777216){
		    		   withNearby = true;
		    	   }else if(i+1 < width && image[i+1][j] <= -16777216){
		    		   withNearby = true;
		    	   }else if(i+1 < width && j+1 < height && image[i+1][j+1] <= -16777216){
		    		   withNearby = true;
		    	   }
		    	   if(withNearby){
		    		   image[i][j] = Color.red;
		    	   }
		       }		       
		    }
		}
	}
	
	private static void createImage(int[][] table) throws IOException{
		int width = table.length;
		int height = table[0].length;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for(int x = 0; x < width; x++) {
		    for(int y = 0; y < height; y++) {
		        image.setRGB(x, y, table[x][y]);
		    }
		}
		
		ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
		ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
		jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		jpgWriteParam.setCompressionQuality(1.0f);

		ImageOutputStream outputStream = new FileImageOutputStream(new File("output.jpg")); // For example implementations see below
		jpgWriter.setOutput(outputStream);
		IIOImage outputImage = new IIOImage(image, null, null);
		jpgWriter.write(null, outputImage, jpgWriteParam);
		jpgWriter.dispose();
	}
	
	private static void cutInRaster(int[][] image, int[][] raster){
		int width = image.length;
		int height = image[0].length;
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				if(image[i][j] != -1){
					raster[i][j] = Color.red;
				}
			}
		}
	}
	
	private static void pasteInRaster(int[][] image, int[][] raster){
		int width = image.length;
		int height = image[0].length;
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height-1; j++){
				if(image[i][j] ==  Color.red){
					raster[i][j] = Color.red;
				}
			}
		}
	}
	
	private static void cleanRaster(int[][] raster){
		int width = raster.length;
		int height = raster[0].length;
		int space = 4;
		int dikte = 10;
		int grens = 3;
		int i = 0;
		while(i < height){
			while(i < grens && i < height){
				System.out.println(i);
				for(int j = 0; j < width; j++){
					raster[j][i] = Color.red;
				}
				i++;
			}
			i = grens + dikte + 1;
			grens += dikte + space;
		}
	}
	
	/**
	* Creates 1 pixel red border around the image.
	*/
	private static int[][] maakKader(int[][] raster){
		int width = raster.length;
		int height = raster[0].length;
		for(int i = 0; i < width; i++){
			raster[i][0] = Color.red;
		}
		for(int i = 0; i < width; i++){
			raster[i][height-1] = Color.red;
		}
		for(int j = 0; j < height; j++){
			raster[0][j] = Color.red;
		}
		for(int j = 0; j < height; j++){
			raster[width-1][j] = Color.red;
		}
		return raster;
	}
	
	private static int[][] createRaster(int width, int height){
		int[][] table = new int[width][height];
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				table[i][j] = Color.white;
			}
		}
		int rowFatness = 1;
		int space = 4;
		int i = space-1;
		int dikte = 10;
		boolean toprow = true;
		int rowfatcounter = 1;
		while(i < height && (!toprow || width-i > rowFatness+space+dikte)){
			for(int j=0; j < width; j++){
				table[j][i] = Color.red;
			}
			//System.out.println(i);
			if(rowfatcounter < rowFatness){
				i++;
				rowfatcounter++;
			} else if(toprow){
				i += dikte;
				rowfatcounter = 1;
				toprow = false;
			} else {
				i += space;
				rowfatcounter = 1;
				toprow = true;
			}
		}
		maakKader(table);
		return table;
	}
	private static void swipeTable(int[][] table){
		int width = table.length;
		int height = table[0].length;
		int rowFatness = 1;
		int i = 0;
		boolean toprow = true;
		int rowfatcounter = 0;
		while(i < width){
			for(int j=0; j < height; j++){
				table[j][i] = Color.red;
			}
			if(rowfatcounter < rowFatness){
				i++;
				rowfatcounter++;
			} else if(toprow){
				i += 10;
				rowfatcounter = 0;
				toprow = false;
			} else {
				i += 4;
				rowfatcounter = 0;
				toprow = true;
			}
		}
	}

	private static BufferedImage getImage(String fileName) throws IOException {
		return ImageIO.read(new File(fileName));
	}
	
	private static String getFileName(){
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		System.out.println("File name: ");
		return reader.nextLine();
	}
	
	private static int[][] imageToTableSlow(BufferedImage image){
		HashSet<Integer> set = new HashSet<Integer>();
		int width = image.getWidth();
		int height = image.getHeight();
		int[][] table = new int[width][height];
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				table[i][j] = image.getRGB(i, j);
				set.add(image.getRGB(i, j));
				//System.out.println(image.getRGB(i, j));
			}
		}
		System.out.println(set.toString());
		return table;
	}
	
	private static int[][] imageToTableFast(BufferedImage image) {

	      final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	      final int width = image.getWidth();
	      final int height = image.getHeight();
	      final boolean hasAlphaChannel = image.getAlphaRaster() != null;

	      int[][] result = new int[height][width];
	      if (hasAlphaChannel) {
	         final int pixelLength = 4;
	         for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
	            int argb = 0;
	            argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
	            argb += ((int) pixels[pixel + 1] & 0xff); // blue
	            argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
	            argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
	            result[row][col] = argb;
	            col++;
	            if (col == width) {
	               col = 0;
	               row++;
	            }
	         }
	      } else {
	         final int pixelLength = 3;
	         for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
	            int argb = 0;
	            argb += -16777216; // 255 alpha
	            argb += ((int) pixels[pixel] & 0xff); // blue
	            argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
	            argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
	            result[row][col] = argb;
	            col++;
	            if (col == width) {
	               col = 0;
	               row++;
	            }
	         }
	      }

	      return result;
	   }

}
