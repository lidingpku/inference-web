package temp.test;

public class Test {
	public static void main(String[] argv){
		String sz_url ="http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/SNARK---20070805r043/combined3.owl";
		sz_url = "http://inference-web.org/proofs/tptp/Solutions/PUZ/PUZ001-1/SNARK---20070805r043/answer.owl";
		System.out.println(sz_url.matches(".*/answer.owl$"));
	}
}
