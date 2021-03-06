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
package org.oulipo.streams.opcodes;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.oulipo.streams.MalformedSpanException;
import org.oulipo.streams.VariantSpan;

/**
 * Deletes a region of text or media.
 */
public final class DeleteVariantOp extends Op {

	/**
	 * The variant span within the document to delete
	 */
	public final VariantSpan variantSpan;

	/**
	 * Constructs a DeleteVariantOp from the specified <code>DataInputStream</code>
	 * 
	 * @param dis
	 *            the input to read the op code from
	 * @throws IOException
	 *             if I/O exception reading the stream
	 * @throws MalformedSpanException
	 *             if variant span read from stream is malformed
	 */
	public DeleteVariantOp(DataInputStream dis) throws MalformedSpanException, IOException {
		this(new VariantSpan(dis));
	}

	/**
	 * Constructs a <code>DeleteVariantOp</code> with the specified span
	 * 
	 * @param variantSpan
	 *            the variant span within the document to delete
	 * @throws IllegalArgumentException
	 *             if variantSpan is null
	 */
	public DeleteVariantOp(VariantSpan variantSpan) {
		super(Op.DELETE);
		if (variantSpan == null) {
			throw new IllegalArgumentException("null variant span");
		}

		this.variantSpan = variantSpan;
	}

	@Override
	public byte[] encode() throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeByte(Op.DELETE);
			dos.writeLong(variantSpan.start);
			dos.writeLong(variantSpan.width);
		}
		os.flush();
		return os.toByteArray();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeleteVariantOp other = (DeleteVariantOp) obj;
		if (!variantSpan.equals(other.variantSpan))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + variantSpan.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "DeleteVariantOp [variantSpan=" + variantSpan + "]";
	}
}
