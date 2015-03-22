/**
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
Copyright (c) 2011, R2AD, LLC
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
   * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
   * Neither the name of the R2AD, LLC nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package com.r2ad.security.utils;

import java.math.BigInteger;
import java.security.MessageDigest;

import android.util.Base64;

/**
* This is a support class to support security encodings, specifically Basic Authentication.
* It may be needed in cases where the standard HTTP classes are not sufficient.
* @author JavaFX@r2ad.com
*/
public class Encoder {
   /**
    * Return a MD5 hash on a SHA1 signed string.
    * @param toSign the input cleartext string
    */
   public static String sign(String toSign) {
       MessageDigest digest = null;
       try {
           digest = MessageDigest.getInstance("SHA1");
       } catch (Exception e) {
           //System.out.println("Exception: " +e );
       }
       // Hopefully SHA1 exists.  If not just return unsigned string.
       if (digest != null) {
           digest.update(toSign.getBytes());
           byte[] md5sum = digest.digest();
           BigInteger bigInt = new BigInteger(1, md5sum);
           return bigInt.toString(16);
       } else {
           return toSign;
       }
   }
   
   // SEE: http://httpd.apache.org/docs/trunk/misc/password_encryptions.html
   
   /**
    * Return a MD5 hash on a SHA1 signed string.
    * @param toSign the input cleartext string
    */
   public static String B64SHA(String toSign) {
       MessageDigest digest = null;
       try {
           digest = MessageDigest.getInstance("SHA1");
       } catch (Exception e) {
           //System.out.println("Exception: " +e );
       }
       // Hopefully SHA1 exists.  If not just return unsigned string.
       if (digest != null) {
           digest.update(toSign.getBytes());
           byte[] hashed = digest.digest();
           // base64 encode:
           String base64 = Base64.encodeToString(hashed, Base64.DEFAULT);
           return base64;
       } else {
           return toSign;
       }
   }
	   
   
   public static String  Base64Encode(String source) {
	    final String base64chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	 
        // the result/encoded string, the padding string, and the pad count
        String r = "", p = "";
        int c = source.length() % 3;
 
        // add a right zero pad to make this string a multiple of 3 characters
        if (c > 0) {
            for (; c < 3; c++) {
                p += "=";
                source += "\0";
            }
        }
 
        // increment over the length of the string, three characters at a time
        for (c = 0; c < source.length(); c += 3) {
 
            // we add newlines after every 76 output characters, according to
            // the MIME specs
            if (c > 0 && (c / 3 * 4) % 76 == 0)
                r += "\r\n";
 
            // these three 8-bit (ASCII) characters become one 24-bit number
            int n = (source.charAt(c) << 16) + (source.charAt(c + 1) << 8)
                    + (source.charAt(c + 2));
 
            // this 24-bit number gets separated into four 6-bit numbers
            int n1 = (n >> 18) & 63, n2 = (n >> 12) & 63, n3 = (n >> 6) & 63, n4 = n & 63;
 
            // those four 6-bit numbers are used as indices into the base64
            // character list
            r += "" + base64chars.charAt(n1) + base64chars.charAt(n2)
                    + base64chars.charAt(n3) + base64chars.charAt(n4);
        }
 
        return r.substring(0, r.length() - p.length()) + p;
	}   
   
   
}