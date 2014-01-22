/**
 * 
 */
package models;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.IOUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToTextContentHandler;
import org.xml.sax.SAXException;

import play.Logger;

/**
 * @author Andrew Jackson <Andrew.Jackson@bl.uk>
 *
 */
public class Inspection {
	
	private Metadata metadata;

	private String contentType;
	
	private String serverContentType;
	
	private String text;
	
	private long size;
	
	private String binary;
	private String binaryText;
	
	private String binaryHead;
	private String binaryHeadText;
	
	private String binaryTail;
	private String binaryTailText;
	
	

	/* --- */
	
	private static Tika tika = new Tika();
	
		/**
		 * TODO Currently opens the connection twice. In the future, this should probably shift to the same core as the warc-discovery indexer, as that would enhance the metadata and prevent multiple downloads.
		 * 
		 * @param url
		 * @return
		 * @throws IOException
		 * @throws SAXException
		 * @throws TikaException
		 */
		public Inspection( String url ) throws IOException, SAXException, TikaException {
			ParseContext context = new ParseContext();
		    StringWriter content = new StringWriter();
			
			metadata = new Metadata();
			metadata.add(Metadata.RESOURCE_NAME_KEY, url);
			URL urlResource = new URL(url);
			URLConnection urlConnection = urlResource.openConnection();
			serverContentType = urlConnection.getContentType();
			
			// Detect:
		    byte[] bytes = IOUtils.toByteArray(urlConnection.getInputStream());
			contentType = tika.detect(bytes, url);
			Logger.info("Dectected input type: "+contentType);
			int binaryMax = 256;
			if ( bytes.length < 2*binaryMax ) {
				this.binary = Base64.encodeBase64String(bytes);
				this.binaryText = "Hexadecimal representation of all "+bytes.length+" bytes.";
			} else {
				this.binaryHead = Base64.encodeBase64String(Arrays.copyOf(bytes,binaryMax));
				this.binaryHeadText = "Hexadecimal representation of the first [0.."+binaryMax+"] bytes.";
				this.binaryTail = Base64.encodeBase64String(Arrays.copyOfRange(bytes,bytes.length-binaryMax, bytes.length));
				this.binaryTailText = "Hexadecimal representation of the last ["+(bytes.length-binaryMax)+".."+(bytes.length)+"] bytes.";
			}
			// Also store the size
			this.size = bytes.length;
			
			// Parse it:
			tika.getParser().parse( new ByteArrayInputStream(bytes), new ToTextContentHandler(content), metadata, context );

			// Store the text:
			this.text = content.toString().trim();
			
			// Log the metadata for information purposes:
			for( String name : metadata.names() ) {
				Logger.info("Metadata: "+ name + " => "+ metadata.get(name));
			}
		}

		/**
		 * @return the metadata
		 */
		public Metadata getMetadata() {
			return metadata;
		}

		/**
		 * @return the contentType
		 */
		public String getContentType() {
			return contentType;
		}

		/**
		 * @return the serverContentType
		 */
		public String getServerContentType() {
			return serverContentType;
		}
		
		/**
		 * 
		 * @return size
		 */
		public long getSize() {
			return this.size;
		}

		/**
		 * @return the text
		 */
		public String getText() {
			return text;
		}

		/**
		 * @return the binary
		 */
		public String getBinary() {
			return binary;
		}

		/**
		 * @return the binaryText
		 */
		public String getBinaryText() {
			return binaryText;
		}

		/**
		 * @return the binaryHead
		 */
		public String getBinaryHead() {
			return binaryHead;
		}

		/**
		 * @return the binaryText
		 */
		public String getBinaryHeadText() {
			return binaryHeadText;
		}

		/**
		 * @return the binaryTail
		 */
		public String getBinaryTail() {
			return binaryTail;
		}

		/**
		 * @return the binaryText
		 */
		public String getBinaryTailText() {
			return binaryTailText;
		}
		
}
