/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License.  
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

import java.util.Arrays;
import java.util.Collection;

import org.oulipo.net.IRI;
import org.oulipo.resources.model.Thing;

/**
 * Maps update and delete operations between Things and some underlying datasource format.
 * 
 * An implementation instance of this interface is meant to be used in the constructor of
 * the <code>AbstractThingRepository</code>
 */
public interface ThingMapper {

	void update(Thing thing);

	void add(Collection<Thing> things);

	void delete(Collection<Thing> things);

	Thing get(IRI address);
	
	default void add(Thing... things) {
		add(Arrays.asList(things));
	}
	
	default void delete(Thing... things) {
		delete(Arrays.asList(things));
	}
}