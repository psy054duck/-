package tools;

public class Tools {
	public static void printn(char ch, int n) {
		for (int i = 0; i < n; ++i) {
			System.out.print(ch);
		}
	}

	public static boolean inArray(String[] array, String s) {
		for (String e : array) {
			if (e.equals(s)) {
				return true;
			}
		}
		return false;
	}
}
