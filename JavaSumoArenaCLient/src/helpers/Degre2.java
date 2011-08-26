package helpers;

public class Degre2 {
	float a, b, c;

	public Degre2(float a, float b, float c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public float[] resolve() {
		float delta = (b * b) - 4 * a * c;
		System.out.println("Degre2 > resolve delta=" + delta);
		if (delta < 0)
			return null;
		float[] ret = new float[2];
		ret[0] = (float)(-b - Math.sqrt(delta)) / (2 * a);
		ret[1] = (float)(-b + Math.sqrt(delta)) / (2 * a);
		return ret;
	}
}
