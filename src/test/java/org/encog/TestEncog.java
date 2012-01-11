/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog;

import java.io.FileNotFoundException;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

public class TestEncog extends TestCase {
	
	@Test
	public void testProperties() throws Throwable {
		Encog instance1 = Encog.getInstance();
		Encog instance2 = Encog.getInstance();
		Assert.assertSame(instance1, instance2);
		Map<String, String> map = instance1.getProperties();
		Assert.assertNotNull(map);
	}
	
	public void testConsole() throws Throwable {
		ConsoleStatusReportable console = new ConsoleStatusReportable();
		console.report(0, 1, "Test console");
		console.report(1, 1, "Test console");
		
		NullStatusReportable n = new NullStatusReportable();
		n.report(0, 1, "Test console");
		n.report(1, 1, "Test console");
	}
	
	public void testException() throws Throwable {
		FileNotFoundException ex = new FileNotFoundException();
		new EncogError("Error",ex);
	}
}
