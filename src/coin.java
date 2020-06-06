import java.io.Serializable;

public class coin implements Serializable {

	int id; //coin ID
	public coin(int id) {
		this.id = id;
	}

	public String stringify() {
		return "<Coin ID: " + this.id + ">";

	}
}
