/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brixcms.rmiserver;

import org.hibernate.exception.NestableRuntimeException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * {@link PasswordEncoder} that uses SHA1 to create a secure password hash. Before the hash is taken the password string
 * is combined with a 44 byte random salt value. This function produces a 128 character output that combines the hash
 * and the salt.
 *
 * @author igor.vaynberg
 */
public final class PasswordEncoder {
    private static final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
            'B', 'C', 'D', 'E', 'F'};

    /*
     * (non-Javadoc)
     * 
     * @see com.inertiabev.biggie.service.security.PasswordEncoder#matches(java.lang.String,
     *      java.lang.String)
     */
    public final boolean check(String password, String encodedPassword) {
        if (password == null) {
            throw new IllegalArgumentException("Argument `password` cannot be null");
        }
        if (encodedPassword == null) {
            throw new IllegalArgumentException("Argument `encodedPassword` cannot be null");
        }
        if (encodedPassword.length() != 128) {
            throw new IllegalArgumentException(
                    "Argument `encodedPassword` does not contain a string encrypted using: " +
                            getClass().getName());
        }

        String salt = encodedPassword.substring(0, 44) + encodedPassword.substring(84, 128);
        String hash = encodedPassword.substring(44, 84);

        return hash.equals(hash(password, salt));
    }

    /**
     * Creates the hash of the password and concatenated salt
     *
     * @param password password string
     * @param salt     salt string
     * @return SHA1 hash of concatenated password and salt
     */
    private String hash(String password, String salt) {
        String saltedPassword = salt + password;
        MessageDigest sha1;
        try {
            sha1 = MessageDigest.getInstance("SHA1");
            sha1.update(saltedPassword.getBytes());
            byte[] saltedHash = sha1.digest();

            return bytesToHexString(saltedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new NestableRuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.inertiabev.biggie.service.security.PasswordEncoder#encode(java.lang.String)
     */
    public final String encode(String password) {
        try {
            byte[] saltBytes = new byte[44];
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.nextBytes(saltBytes);
            String salt = bytesToHexString(saltBytes);
            String hash = hash(password, salt);
            return salt.substring(0, 44) + hash + salt.substring(44, 88);
        } catch (Exception e) {
            throw new NestableRuntimeException(e);
        }
    }

    /**
     * Converts bytes to their hexadecimal string representation
     *
     * @param bytes bytes to convert
     * @return hexadecimal string
     */
    private static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            final byte b = bytes[i];
            final char high = hexChars[(b & 0xF0) >> 4];
            final char low = hexChars[b & 0x0F];
            sb.append(high);
            sb.append(low);
        }
        return sb.toString();
    }
}
