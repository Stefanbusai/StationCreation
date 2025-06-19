package com.airedale.StationCreation.utils;

/**
 * This class contains string utility methods for use by Niagara 4 classes.
 * 
 * @author Phil Holden
 * @copyright Airedale International Air Conditioning Ltd.
 */
public class StringUtils
{
  
  /**
   * Convert a String to a fixed length.
   * 
   * If the original String is shorter than the specified fixed length, pad it with trailing spaces.
   * 
   * If the original String is longer than the specified fixed length, return the appropriate number
   * of left-most characters if "fromEnd" is false, or right-most characters if "fromEnd" is true.
   * 
   * If the original String is the same length as the specified fixed length, simply return the
   * original String.
   */
  public static String convertStringToFixedLength(String originalString, int fixedLength,
                                                  boolean fromEnd)
  {
    final int originalLength = originalString.length();

    String convertedString = null;

    if (originalLength == fixedLength)
    {
      return originalString;
    }

    if (originalLength < fixedLength)
    {
      convertedString = originalString;

      for (int i = 1; i <= fixedLength - originalLength; i++)
      {
        convertedString += " ";
      }

      return convertedString;
    }
    else
    {
      if (fromEnd)
      {
        convertedString = originalString.substring(originalLength - fixedLength, originalLength);
      }
      else
      {
        convertedString = originalString.substring(0, fixedLength);
      }

      return convertedString;
    }
  }
  
  /**
   * Replace special characters in the specified string.
   */
  public static String replaceSpecialCharacters(String str)
  {
    str = str.replace("$20", " ");
    str = str.replace("$2d", "-");
    str = str.replace("$2e", ".");
    str = str.replace("$2f", "/");
    str = str.replace("$3a", ":");
    str = str.replace("$7b", "{");
    str = str.replace("$7c", "|");
    str = str.replace("$7d", "}");
    str = str.replace("$28", "(");
    str = str.replace("$29", ")");
    
    return str;
  }
  
  /**
   * Insert special characters in the specified string. This method performs the opposite
   * replacement behaviour to "replaceSpecialCharacters(String str)".
   */
  public static String insertSpecialCharacters(String str)
  {
    str = str.replace(" ", "$20");
    str = str.replace("-", "$2d");
    str = str.replace(".", "$2e");
    str = str.replace("/", "$2f");
    str = str.replace(":", "$3a");
    str = str.replace("{", "$7b");
    str = str.replace("|", "$7c");
    str = str.replace("}", "$7d");
    str = str.replace("(", "$28");
    str = str.replace(")", "$29");

    return str;
  }

}
