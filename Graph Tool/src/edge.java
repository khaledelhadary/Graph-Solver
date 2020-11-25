
public class edge {
	public Integer from , to , cost;
	edge( Integer _from , Integer _to , Integer _cost ){
		from = _from;
		to 	 = _to;
		cost = _cost;
	}
	edge( Integer _from , Integer _to ){
		from = _from;
		to 	 = _to;
		cost = 0;
	}
}
