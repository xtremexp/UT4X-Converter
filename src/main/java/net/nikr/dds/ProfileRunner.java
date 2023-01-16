/*
 * ProfileRunner.java - This file is part of Java DDS ImageIO Plugin
 *
 * Copyright (C) 2011 Niklas Kyster Rasmussen
 * 
 * COPYRIGHT NOTICE:
 * Java DDS ImageIO Plugin is based on code from the DDS GIMP plugin.
 * Copyright (C) 2004-2010 Shawn Kirst <skirst@insightbb.com>,
 * Copyright (C) 2003 Arne Reuter <homepage@arnereuter.de>
 *
 * Java DDS ImageIO Plugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * Java DDS ImageIO Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Java DDS ImageIO Plugin; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * FILE DESCRIPTION:
 * TODO Write File Description for ProfileRunner.java
 */

package net.nikr.dds;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import net.nikr.dds.Viewer.DdsFilter;


public class ProfileRunner {

	public ProfileRunner(String[] args) {
		List<String> argsList = Arrays.asList(args);
		if (argsList.contains("-24bit")) x100("src" + File.separator + "test" + File.separator + "resources" + File.separator + "net" + File.separator + "nikr" + File.separator + "dds" + File.separator + "gimp" + File.separator + "rgb8.dds");
		if (argsList.contains("-all")) all();
	}
	
	
	public static void main(String[] args) {
		ProfileRunner profileRunner = new ProfileRunner(args);
	}
	
	private void x100(String filename){
		File file = new File(filename);
		for (int i = 0; i < 100; i++){
			BufferedImage image = loadFile(file, 0);
			for (int imageIndex = 1; image != null; imageIndex++){
				image = loadFile(file, imageIndex);
			}
			System.out.println((i+1)+" of 100 done...");
		}
	}
	
	private void all(){
		List<File> files = new ArrayList<File>();
		files.addAll(getDirFiles("src" + File.separator + "test" + File.separator + "resources" + File.separator + "net" + File.separator + "nikr" + File.separator + "dds" + File.separator));
		files.addAll(getDirFiles("src" + File.separator + "test" + File.separator + "resources" + File.separator + "net" + File.separator + "nikr" + File.separator + "dds" + File.separator + "gimp" + File.separator));
		for (File file : files){
			System.out.println(file.getName());
			BufferedImage image = loadFile(file, 0);
			for (int imageIndex = 1; image != null; imageIndex++){
				image = loadFile(file, imageIndex);
			}
		}
	}
	
	public BufferedImage loadFile(File file, int imageIndex){
        Iterator<ImageReader> iterator = ImageIO.getImageReadersBySuffix("dds");
        if (iterator.hasNext()){
			try {
				ImageReader imageReader = iterator.next();
				imageReader.setInput(new FileImageInputStream(file));
				int max = imageReader.getNumImages(true);
				if (imageIndex >= 0 && imageIndex < max){
					return imageReader.read(imageIndex);
				}
			} catch (Exception ex) {
				System.out.println("loadFile fail...");
				ex.printStackTrace();
			}
        }
		return null;
	}
	
	private List<File> getDirFiles(String dir){
		File file = new File(dir);
		return Arrays.asList(file.listFiles(new DdsFilter(false)));
	}
}
