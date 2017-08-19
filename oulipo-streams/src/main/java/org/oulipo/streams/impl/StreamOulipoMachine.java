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
package org.oulipo.streams.impl;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.AbstractOulipoMachine;
import org.oulipo.streams.InvariantSpan;
import org.oulipo.streams.InvariantSpans;
import org.oulipo.streams.InvariantStream;
import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.VariantSpan;
import org.oulipo.streams.VariantStream;
import org.oulipo.streams.opcodes.CopyOp;
import org.oulipo.streams.opcodes.InsertTextOp;
import org.oulipo.streams.opcodes.MoveOp;
import org.oulipo.streams.opcodes.Op;
import org.oulipo.streams.opcodes.PutOp;
import org.oulipo.streams.opcodes.SwapOp;

/**
 * An OulipoMachine that is backed by a <code>StreamLoader</code>. The StreamLoader can be 
 * remote or local.
 *
 */
public final class StreamOulipoMachine extends AbstractOulipoMachine {

	public static StreamOulipoMachine create(StreamLoader loader,
			TumblerAddress tumbler, boolean writeOpCodes) throws IOException, MalformedSpanException {
		return new StreamOulipoMachine(loader, tumbler, writeOpCodes);
	}

	private final InvariantStream iStream;

	private final TumblerAddress tumbler;

	private final VariantStream vStream;

	private final ExecutorService executor = Executors.newFixedThreadPool(5);

	protected final StreamLoader stream;

	private final boolean writeOpCodes;
	
	private StreamOulipoMachine(StreamLoader stream, TumblerAddress tumbler, boolean writeOpCodes)
			throws IOException, MalformedSpanException {
		super();
		if (stream == null) {
			throw new IllegalArgumentException("streamLoader is null");
		}
		this.stream = stream;
		this.writeOpCodes = writeOpCodes;
	
		this.iStream = stream.openInvariantStream(tumbler);
		this.vStream = stream.openVariantStream(tumbler);
		this.tumbler = tumbler;
	}

	@Override
	public InvariantSpan append(String text) throws IOException,
			MalformedSpanException {
		return iStream.append(text);
	}

	@Override
	public TumblerAddress getHomeDocument() {
		return tumbler;
	}

	@Override
	public InvariantSpans getInvariantSpans() throws MalformedSpanException {
		return vStream.getInvariantSpans();
	}

	@Override
	public String getText(InvariantSpan ispan) throws IOException {
		return iStream.getText(ispan);
	}

	@Override
	public void push(Op<?> op) throws MalformedSpanException, IOException {
		switch (op.getCode()) {
		case Op.COPY:
			CopyOp.Data copyOp = (CopyOp.Data) op.getData();
			vStream.copy(copyOp.to, copyOp.variantSpan);
			break;
		case Op.DELETE:
			vStream.delete((VariantSpan) op.getData());
			break;
		case Op.INSERT_TEXT:
			InsertTextOp.Data insertOp = (InsertTextOp.Data) op.getData();
			vStream.put(insertOp.region, iStream.append(insertOp.text));
			break;
		case Op.MOVE:
			MoveOp.Data moveOp = (MoveOp.Data) op.getData();
			vStream.move(moveOp.to, moveOp.variantSpan);
			break;
		case Op.PUT:
			PutOp.Data putOp = (PutOp.Data) op.getData();
			vStream.put(putOp.to, putOp.invariantSpan);
			break;
		case Op.SWAP:
			SwapOp.Data swapOp = (SwapOp.Data) op.getData();
			vStream.swap(swapOp.v1, swapOp.v2);
			break;
		}
	}

	@Override
	public InvariantSpans getInvariantSpans(VariantSpan variantSpan) throws MalformedSpanException {
		return vStream.getInvariantSpans(variantSpan);
	}

	@Override
	public InvariantSpan index(long characterPosition) {
		return vStream.index(characterPosition);
	}
	
	@Override
	public Op<?> writeOp(Op<?> op) {
		if (writeOpCodes) {
			executor.submit(() -> {
				try {
					stream.writeOp(op.toBytes());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
		return op;
	}
}
