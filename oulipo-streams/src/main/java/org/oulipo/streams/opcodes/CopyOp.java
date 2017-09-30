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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.oulipo.streams.VariantSpan;

public final class CopyOp extends Op<CopyOp.Data> {

	public static class Data {

		public final long to;

		public final VariantSpan variantSpan;

		public Data(long to, VariantSpan variantSpan) {
			this.to = to;
			this.variantSpan = variantSpan;
		}
	}

	public CopyOp(Data data) {
		super(Op.COPY, data);
	}

	public CopyOp(long to, VariantSpan variantSpan) {
		this(new Data(to, variantSpan));
	}

	@Override
	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.COPY);
			dos.writeLong(getData().to);
			dos.writeLong(getData().variantSpan.start);
			dos.writeLong(getData().variantSpan.width);
		}

		os.flush();
		return os.toByteArray();
	}
}
