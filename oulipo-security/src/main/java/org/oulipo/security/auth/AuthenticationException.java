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

import org.oulipo.net.TumblerAddress;

public class AuthenticationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9133436215687059071L;

	private int code;

	public AuthenticationException(int code, String message) {
		super(message);
		this.code = code;
	}

	public AuthenticationException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public AuthenticationException(int code, Throwable cause) {
		super(cause);
		this.code = code;
	}

	public AuthenticationException(int code, TumblerAddress address) {
		super();
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
