import java.util.Comparator;

public class edgeComparator implements Comparator<edge> {

	@Override
	public int compare(edge e1, edge e2) {
		if ( e1.cost <= e2.cost ) {
			return 1;
		}
		return -1;
	}

}
