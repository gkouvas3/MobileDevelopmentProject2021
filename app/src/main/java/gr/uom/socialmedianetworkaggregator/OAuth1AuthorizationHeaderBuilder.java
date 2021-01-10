package gr.uom.socialmedianetworkaggregator;



import android.os.Build;
import android.util.Log;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.facebook.appevents.internal.AppEventUtility.bytesToHex;

/**
 * @author Daniel DeGroff
 */
public class OAuth1AuthorizationHeaderBuilder {
  // https://tools.ietf.org/html/rfc3986#section-2.3
  private static final HashSet<Character> UnreservedChars = new HashSet<>(Arrays.asList(
          'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
          'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
          '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
          '-', '_', '.', '~'));
  private static final String TAG = "Thanos";

  public String consumerSecret;

  public String method;

  public String parameterString;

  public Map<String, String> parameters = new LinkedHashMap<>();

  public String signature;

  public String signatureBaseString;

  public String signingKey;

  public String tokenSecret;

  public String url;

  /***
   * Replaces any character not specifically unreserved to an equivalent percent sequence.
   *
   * @param s the string to encode
   * @return and encoded string
   * @see <a href="https://stackoverflow.com/a/51754473/3892636">https://stackoverflow.com/a/51754473/3892636</a>}
   */
  public static String encodeURIComponent(String s) {
    StringBuilder o = new StringBuilder();
    for (char ch : s.toCharArray()) {
      if (isSafe(ch)) {
        o.append(ch);
      } else {
        o.append('%');
        o.append(toHex(ch / 16));
        o.append(toHex(ch % 16));
      }
    }
    return o.toString();
  }

  private static boolean isSafe(char ch) {
    return UnreservedChars.contains(ch);
  }

  private static char toHex(int ch) {
    return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
  }

  public String build() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
    // For testing purposes, only add the timestamp if it has not yet been added
    if (!parameters.containsKey("oauth_timestamp")) {

        parameters.put("oauth_timestamp",  ""+ System.currentTimeMillis()/1000); //Instant.now().getEpochSecond());

    }

    // Boiler plate parameters
    parameters.put("oauth_signature_method", "HMAC-SHA1");
    parameters.put("oauth_version", "1.0");

    // Build the parameter string after sorting the keys in lexicographic order per the OAuth v1 spec.
    parameterString = parameters.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(e -> encodeURIComponent(e.getKey()) + "=" + encodeURIComponent(e.getValue()))
            .collect(Collectors.joining("&"));

    // Build the signature base string
    signatureBaseString = method.toUpperCase() + "&" + encodeURIComponent(url) + "&" + encodeURIComponent(parameterString);

    Log.d(TAG,"BaseString: "+signatureBaseString);

    // If the signing key was not provided, build it by encoding the consumer secret + the token secret
    if (signingKey == null) {
      signingKey = encodeURIComponent(consumerSecret) + "&" + (tokenSecret == null ? "" : encodeURIComponent(tokenSecret));
    }

    // Sign the Signature Base String
    signature = generateSignature(signingKey, signatureBaseString);

    // Add the signature to be included in the header
    parameters.put("oauth_signature", signature);

    // Build the authorization header value using the order in which the parameters were added

    Log.d(TAG,"filtering oauth header parameters, parameters before filtering:"+parameters.entrySet().toString());
    List<String> nonOauthKeys = new ArrayList<>();
    for(Map.Entry<String,String> e : parameters.entrySet()){
      if(!(e.getKey().contains("oauth"))){
          nonOauthKeys.add(e.getKey());
      }
    }

    for(String key : nonOauthKeys){
      parameters.remove(key);
    }
    Log.d(TAG,"parameters after filtering: "+parameters.entrySet().toString());
    return "OAuth " + parameters.entrySet().stream()
            .map(e -> encodeURIComponent(e.getKey()) + "=\"" + encodeURIComponent(e.getValue()) + "\"")
            .collect(Collectors.joining(", "));
  }

  /**
   * Set the Consumer Secret
   *
   * @param consumerSecret the Consumer Secret
   * @return this
   */
  public OAuth1AuthorizationHeaderBuilder withConsumerSecret(String consumerSecret) {
    this.consumerSecret = consumerSecret;
    return this;
  }

  /**
   * Set the requested HTTP method
   *
   * @param method the HTTP method you are requesting
   * @return this
   */
  public OAuth1AuthorizationHeaderBuilder withMethod(String method) {
    this.method = method;
    return this;
  }

  /**
   * Add a parameter to the be included when building the signature.
   *
   * @param name  the parameter name
   * @param value the parameter value
   * @return this
   */
  public OAuth1AuthorizationHeaderBuilder withParameter(String name, String value) {
    parameters.put(name, value);
    return this;
  }
  public OAuth1AuthorizationHeaderBuilder withParameter(Map<String, String> params) {

    for(String key: params.keySet())
      parameters.put(key, params.get(key));
    return this;
  }

  /**
   * Set the OAuth Token Secret
   *
   * @param tokenSecret the OAuth Token Secret
   * @return this
   */
  public OAuth1AuthorizationHeaderBuilder withTokenSecret(String tokenSecret) {
    this.tokenSecret = tokenSecret;
    return this;
  }

  /**
   * Set the requested URL in the builder.
   *
   * @param url the URL you are requesting
   * @return this
   */
  public OAuth1AuthorizationHeaderBuilder withURL(String url) {
    this.url = url;
    return this;
  }

  private String generateSignature(String secret, String message) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
    try {
      byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
      Mac mac = Mac.getInstance("HmacSHA1");
      mac.init(new SecretKeySpec(bytes, "HmacSHA1"));
      byte[] result = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
      return android.util.Base64.encodeToString(result,android.util.Base64.DEFAULT);
    } catch (InvalidKeyException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }

  }

  /*
  private static String hmacSha1(String value, String key)
          throws UnsupportedEncodingException, NoSuchAlgorithmException,
          InvalidKeyException {
    String type = "HmacSHA1";
    SecretKeySpec secret = new SecretKeySpec(key.getBytes(), type);
    Mac mac = Mac.getInstance(type);
    mac.init(secret);
    byte[] bytes = mac.doFinal(value.getBytes());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      return Base64.getEncoder().encodeToString(bytes);
    }else return null;
  }

  private final static char[] hexArray = "0123456789abcdef".toCharArray();

  private static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    int v;
    for (int j = 0; j < bytes.length; j++) {
      v = bytes[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }*/
}