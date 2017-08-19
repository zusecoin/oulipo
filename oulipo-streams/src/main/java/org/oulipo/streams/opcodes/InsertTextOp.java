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
package org.oulipo.streams.opcodes;



public final class InsertTextOp extends Op<InsertTextOp.Data> {

	public InsertTextOp(Data data) {
		super(Op.INSERT_TEXT, data);
	}

	public InsertTextOp(long region, String text) {
		this(new Data(region, text));
	}

	public static class Data {

		public final long region;

		public final String text;

		public Data(long region, String text) {
			this.region = region;
			this.text = text;
		}
	}


	@Override
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		return null;
	}

}
