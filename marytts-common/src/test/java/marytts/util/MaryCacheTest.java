/**
 * Copyright 2009 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of MARY TTS.
 *
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package marytts.util;

import java.io.File;
import java.sql.SQLException;

import org.testng.Assert;
import org.testng.annotations.*;

/**
 * @author marc
 *
 */
public class MaryCacheTest {

	private static MaryCache c;
	private static File maryCacheFile;
	private static String inputtype = "TEXT";
	private static String outputtype = "RAWMARYXML";
	private static String locale = "de";
	private static String voice = "de1";
	private static String inputtext = "Welcome to the world of speech synthesis";
	private static String targetValue = "<rawmaryxml/>";
	private static byte[] targetAudio = new byte[12345];
	private static String inputtext2 = "Some other input text";
	private static String targetValue2 = "Two\nlines";

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		maryCacheFile = File.createTempFile("temp-file-name", ".tmp");
		c = new MaryCache(maryCacheFile, true);
		c.insertText(inputtype, outputtype, locale, voice, inputtext, targetValue);
		c.insertAudio(inputtype, locale, voice, inputtext, targetAudio);
		c.insertText(inputtype, outputtype, locale, voice, inputtext2, targetValue2);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDown() throws Exception {
		c.shutdown();
	}

	@Test
	public void lookupText() throws Exception {
		String lookupValue = c.lookupText(inputtype, outputtype, locale, voice, inputtext);
		Assert.assertEquals(targetValue, lookupValue);
	}

	@Test
	public void lookupText2() throws Exception {
		String lookupValue = c.lookupText(inputtype, outputtype, locale, voice, inputtext2);
		Assert.assertEquals(targetValue2, lookupValue);
	}

	@Test
	public void lookupAudio() throws Exception {
		byte[] lookupAudio = c.lookupAudio(inputtype, locale, voice, inputtext);
		Assert.assertNotNull(lookupAudio);
		Assert.assertEquals(targetAudio, lookupAudio);
	}

	@Test
	public void canInsertAgain() throws Exception {
		int numExceptions = 0;
		try {
			c.insertText(inputtype, outputtype, locale, voice, inputtext, targetValue);
		} catch (SQLException e) {
			numExceptions++;
		}
		try {
			c.insertAudio(inputtype, locale, voice, inputtext, targetAudio);
		} catch (SQLException e) {
			numExceptions++;
		}
		Assert.assertEquals(0, numExceptions);
	}

	@Test
	public void isPersistent() throws Exception {
		c.shutdown();
		c = new MaryCache(maryCacheFile, false);
		lookupText();
		lookupAudio();
	}

	@Test
	public void zzz_isClearable() throws Exception {
		c.shutdown();
		c = new MaryCache(maryCacheFile, true);
		String lookupValue = c.lookupText(inputtype, outputtype, locale, voice, inputtext);
		Assert.assertNull(lookupValue);
		byte[] lookupAudio = c.lookupAudio(inputtype, locale, voice, inputtext);
		Assert.assertNull(lookupAudio);
	}

}
