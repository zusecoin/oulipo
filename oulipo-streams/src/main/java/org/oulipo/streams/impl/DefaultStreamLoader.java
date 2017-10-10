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

import java.io.File;
import java.io.IOException;

import org.oulipo.net.MalformedSpanException;
import org.oulipo.net.TumblerAddress;
import org.oulipo.streams.Spans;
import org.oulipo.streams.InvariantStream;
import org.oulipo.streams.StreamLoader;
import org.oulipo.streams.VariantStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * An implementation of StreamLoader that is backed by the file system for the
 * variant and invariant streams.
 */
public final class DefaultStreamLoader implements StreamLoader {

	/**
	 * Base directory where the streams are stored
	 */
	private File baseDir;

	/**
	 * Variant stream in-memory cache
	 */
	private final LoadingCache<TumblerAddress, VariantStream> cache;

	/**
	 * Maps invariant spans to/from JSON format
	 */
	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * Constructs a <code>StreamLoader</code> instance backed by the file system and
	 * in-memory cache.
	 * 
	 * @param baseDir
	 *            the base directory where the streams are stored
	 * @param spec
	 *            the spec for the cache. Contains parameters like the expiry time
	 *            and max size of the cache.
	 */
	public DefaultStreamLoader(File baseDir, String spec) {
		this.baseDir = baseDir;
		baseDir.mkdirs();
		cache = CacheBuilder.from(spec).removalListener(new RemovalListener<TumblerAddress, VariantStream>() {

			@Override
			public void onRemoval(RemovalNotification<TumblerAddress, VariantStream> notification) {
				try {
					persistVariant(notification.getKey(), notification.getValue());
				} catch (IOException | MalformedSpanException e) {
					e.printStackTrace();
				}
			}
		}).build(new CacheLoader<TumblerAddress, VariantStream>() {
			@Override
			public VariantStream load(TumblerAddress key) throws IOException, MalformedSpanException {
				return openVariantStream(key);
			}
		});
	}

	@Override
	public void flushVariantCache() {
		// cache.
		cache.invalidateAll();
	}

	@Override
	public InvariantStream openInvariantStream(TumblerAddress tumbler) throws IOException {
		return new FileInvariantStream(
				new File(baseDir, tumbler.userVal() + ".0." + tumbler.documentVal() + "-invariant.txt"), tumbler);
	}

	@Override
	public VariantStream openVariantStream(TumblerAddress tumbler) throws IOException, MalformedSpanException {
		tumbler = tumbler.getDocumentAddress();
		VariantStream stream = cache.getIfPresent(tumbler);
		if (stream != null) {
			return stream;
		}

		File file = new File(baseDir, tumbler.userVal() + ".0." + tumbler.documentVal() + "-variant.json");
		stream = new RopeVariantStream(tumbler);
		if (file.exists()) {
			Spans spans = mapper.readValue(file, Spans.class);
			stream.load(spans);
		}
		cache.put(tumbler, stream);

		return stream;
	}

	private void persistVariant(TumblerAddress tumbler, VariantStream stream)
			throws IOException, MalformedSpanException {
		File file = new File(baseDir, tumbler.userVal() + ".0." + tumbler.documentVal() + "-variant.json");
		mapper.writeValue(file, stream.getSpans());
	}

	@Override
	public boolean writeOp(byte[] op) {
		// TODO Auto-generated method stub
		return true;

	}

}
