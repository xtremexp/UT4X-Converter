/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.test;

import org.xtx.ut4converter.UTGames;
import org.xtx.ut4converter.config.UserConfig;
import org.xtx.ut4converter.config.UserGameConfig;
import org.xtx.ut4converter.t3d.T3DActor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

/**
 * 
 * @author XtremeXp
 */
public class UserConfigTest {

	public static void test() {
		UserConfig user = new UserConfig();

		UserGameConfig gc = new UserGameConfig(UTGames.UTGame.UT99, new File("C:\\Program Files\\Steam"));
		UserGameConfig gc3 = new UserGameConfig(UTGames.UTGame.UT2004, new File("C:\\Program Files\\Steam\\UT2004"));
		gc3.setLastConverted(new File("Z:\\aaaa.t3d"));

		user.getGame().add(gc);
		user.getGame().add(gc3);

		try {

			File file = new File("Z:\\file.xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(UserConfig.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(user, file);
			jaxbMarshaller.marshal(user, System.out);

		} catch (JAXBException e) {
			e.printStackTrace();
		}

		T3DActor t;
	}

}
