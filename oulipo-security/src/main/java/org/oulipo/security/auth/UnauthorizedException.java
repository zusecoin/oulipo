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
package org.oulipo.security.auth;

import org.oulipo.streams.IRI;
import org.oulipo.streams.IriResourceException;

public class UnauthorizedException extends IriResourceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 628874465598916086L;

	public UnauthorizedException(IRI iri) {
		super(iri);
	}

	public UnauthorizedException(IRI iri, String message) {
		super(iri, message);
	}

	public UnauthorizedException(IRI iri, String message, Throwable cause) {
		super(iri, message, cause);
	}

	public UnauthorizedException(IRI iri, Throwable cause) {
		super(iri, cause);
	}

}
