public class HexToB64 {
	
	static final char[] B64INDEX = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
	
	static final char[] MOSTCOMMON = {' ', 'e', 't', 'a', 'o', 'i'};
	
	static final char[] LEASTCOMMON = {'v', 'k', 'j', 'x', 'q', 'z'};
	
	//turns a hex number into a byte
	public static byte hexNumToByte(char c){
		byte b = Character.digit(c, 16);
		return b;
	}
	
	// based off of the one from stackexchange
	// okay it's basically the same but I named my variables different things
	// https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
	public static byte[] hexStringToBytes(String s){
		int sLength = s.length();
		
		byte[] b = new byte[sLength / 2];
		for (int i = 0; i < sLength; i = i+2){
			System.out.println("char at " + i + " = " + Character.digit(s.charAt(i), 16));
			byte bb = (byte)(((Character.digit(s.charAt(i), 16)) << 4) | (Character.digit(s.charAt(i + 1), 16)));
			b[i / 2] = bb;
		}
		//System.out.println("in hexStringToBytes: b = " + b);
		for (int i = 0; i < b.length; i++){
			System.out.println("b[" + i + "] = " + b[i]);
		}
		return b;
	}
	
	public static byte[] addZeroesPadding(byte[] b){
		byte[] bResult;
		byte pad = 0;
		if (b.length % 3 == 1){
			bResult = new byte[b.length + 2];
			for (int i = 0; i < b.length; i++){
				bResult[i] = b[i];
			}
			bResult[b.length] = pad;
			bResult[b.length + 1] = pad;
		}
		else if (b.length % 3 == 2){
			bResult = new byte[b.length + 1];
			for (int i = 0; i < b.length; i++){
				bResult[i] = b[i];
			}
			bResult[b.length] = pad;
		}
		else{ //b.length % 3 == 0
			bResult = b;
		}
		return bResult;
	}
	
	public static char[] convertHexToB64(String s){
		byte[] b = hexStringToBytes(s);
		b = addZeroesPadding(b);
		char[] resultChars = new char[(b.length / 3) * 4];
		int j = 0; //index of the char array
		for (int i = 0; i < b.length; i = i+3){
			System.out.println("In convertHexToB64, i = " + i);
			//first char
			System.out.println("b[" + i + "] = " + b[i]);
			byte oneOfFour = (byte)((b[i] & 252) >> 2);
			System.out.println("oneOfFour = " + oneOfFour);
			resultChars[j] = B64INDEX[oneOfFour];
			j += 1;
			
			//second char
			byte twoOfFour = (byte)((b[i] & 3) << 4);
			twoOfFour = (byte)(twoOfFour |= ((b[i + 1] & 240) >> 4));
			System.out.println("twoOfFour = " + twoOfFour);
			resultChars[j] = B64INDEX[twoOfFour];
			j += 1;
			
			//third char
			byte threeOfFour = (byte)((b[i + 1] & 15) << 2);
			threeOfFour = (byte)(threeOfFour |= ((b[i + 2] & 192) >> 6));
			System.out.println("threeOfFour = " + threeOfFour);
			resultChars[j] = B64INDEX[threeOfFour];
			j += 1;
			
			//fourth char
			byte fourOfFour = (byte)(b[i + 2] & 63);
			System.out.println("fourOfFour = " + fourOfFour);
			resultChars[j] = B64INDEX[fourOfFour];
			j += 1;
		}
		return resultChars;
	}
	
	//challenge 2
	public static byte[] xorBuffers(String s1, String s2){
		byte[] b1 = hexStringToBytes(s1);
		byte[] b2 = hexStringToBytes(s2);
		/*
		byte[] result = new byte[b1.length];
		for (int i = 0; i < result.length; i++){
			byte b = (byte)(b1[i] ^ b2[i]);
			result[i] = b;
		}
		return result;*/
		
		byte[] result = xorByteArrays(b1, b2);
		resturn result;
	}
	
	//XOR the given byte arrays (of equal length) and return result
	public static byte[] xorByteArrays(byte[] b1, byte[] b2){
		byte[] result = new byte[b1.length];
		for (int i = 0; i < result.length; i++){
			byte b = (byte)(b1[i] ^ b2[i]);
			result[i] = b;
		}
		return result;
	}
	
	//challenge 3
	//big help: https://inventwithpython.com/hacking/chapter20.html
	public static void singleByteXORDecrypt(String s1){
		
		/* Go through each ascii english character and get the XOR result of 
		 * the input string and that character. Then go through the result and 
		 * score according to letter frequency. Highest score is the answer (I think?).
		 *
		 * Or: go through each hex number 0x00 through 0x7F (0-127)? I don't know
		 */
		
		//Step 1: XOR the string against each hex number and store
		
		Map<byte, byte[]> stringsXORed = new LinkedHashMap<byte, byte[]>();
		byte[] b1 = hexStringToBytes(s1);
		
		for (byte b = 0; b < 128; b^1){ //to add two numbers, need to do XOR, not ADD
			byte[] b2 = byteToRepeatingByteArray(b1.length, b);
			byte[] xorResult = xorByteArrays(b1, b2);
			stringsXORed.put(b, xorResult);
		}
		
		//Step 2: only keep those arrays that are valid sequences of english letters and
		//numbers and whitespaces
		//for iteration: https://stackoverflow.com/questions/12310914/how-to-iterate-through-linkedhashmap-with-lists-as-values
		for (Map.Entry<byte, byte[]> entry : stringsXORed.entrySet()){
			byte key = entry.getKey();
			byte[] value = entry.getValue();
			if (!isValidEnglish(value)){
				stringsXORed.remove(key);
			}
		}
		
		//Step 3: for each sequence: create a new sequence of each individual letter, ordered
		//from most common to least common
		
		//Step 3A: turn every uppercase letter into a lowercase letter
		Map<byte, byte[]> stringsXORedLowercase = new LinkedHashMap<byte, byte[]>(stringsXORed);
		
	}
	
	//create a byte array of length n of repeating byte b
	public static byte[] byteToRepeatingByteArray(int n, byte b){
		byte[] bArray = new byte[n];
		
		for (int i = 0; i < n; i++){
			bArray[i] = b;
		}
		
		return bArray;
	}
	
	//goes through a byte array of possible ascii chars and determines if it is a valid
	//sequence of english letters + spaces
	public static boolean isValidEnglish(byte[] b){
		for (int i = 0; i < b.length; i++){
			if (!(Character.isLetterOrDigit(b[i]) || Character.isSpaceChar(b[i]))){
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args) {
		/*
		int i = 132;
		byte b = (byte) i;
		System.out.println("b = " + b);
		
		int ii = 16;
		byte bb = (byte) ii;
		System.out.println("bb = " + bb);
		
		int iii = 128;
		byte bbb = (byte) iii;
		System.out.println("bbb = " + bbb);
		*/
		
		/*
		String hexString = args[0];
		System.out.println("args[0] = " + hexString);
		System.out.println(convertHexToB64(hexString));*/
		
		String s1 = args[0];
		String s2 = args[1];
		//System.out.println(xorBuffers(s1, s2));
		byte[] result = xorBuffers(s1, s2);
		
		for (int i = 0; i < result.length; i++){
			System.out.println(result[i]);
		}
		
		
	}
	
	
	
	
	//0x12
	//8 bits = 00000000 -> 128 64 32 16 8 4 2 1
	//128 + 64 + 32 + 16 + 8 + 4 + 2 + 1 = 255
	//0xff = 15*16 + 15*1 = 255
	
	//s1 = 1c0111001f010100061a024b53535009181c 686974207468652062756c6c277320657965
	//s2 = 686974207468652062756c6c277320657965
	
	
	
}