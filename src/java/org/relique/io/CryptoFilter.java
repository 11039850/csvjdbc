package org.relique.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * minimalistic approach to decrypting a file.
 * 
 * a class implementing this interface is required to return one character at a
 * time from the given InputStream. the InputStream is encrypted, the client
 * receives it clear-text.
 * 
 * the encryption used may require you to read more than one character from the
 * InputStream, but this is your business, as is all initialization required by
 * your cipher, the client will be offered one deciphered character at a time.
 * 
 * @author mfrasca@zonnet.nl
 * 
 */
public interface CryptoFilter {

	abstract public int read(InputStream in) throws IOException;

}
