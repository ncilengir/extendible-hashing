import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		startHashing();

	}

	private static <K, V> void startHashing() {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

		ChainHashMap<K, V> starthashing = new ChainHashMap<K, V>();
		starthashing.readFile();
		System.out.println();

		try {
			while (true) {
				System.out.print("#Search: ");

				@SuppressWarnings("unchecked")
				V search = (V) scanner.next();

				starthashing.bucketGet(starthashing.functionHash(search), search);

				System.out.println();
			}
		} catch (Exception e) {
			System.out.println("There was a problem");
		}
	}
}
