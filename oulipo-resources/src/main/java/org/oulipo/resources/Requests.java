/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License");  you may not use this file except in compliance with the License.  
 *
 * You may obtain a copy of the License at
 *   
 *       http://www.apache.org/licenses/LICENSE-2.0
 *    
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See the NOTICE file distributed with this work for 
 * additional information regarding copyright ownership. 
 *******************************************************************************/
package org.oulipo.resources;

import java.util.HashMap;
import java.util.Map;

public class Requests {

	public static Map<String, String> flattenMap(Map<String, String[]> params) {
		Map<String, String> queryParams2 = new HashMap<>();
		for (Map.Entry<String, String[]> entry : params.entrySet()) {
			queryParams2.put(entry.getKey(), entry.getValue()[0]);
		}
		return queryParams2;
	}

}
