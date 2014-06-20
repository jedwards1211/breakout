package org.andork.func;

public class FloatBimapper implements Bimapper<Float, Object> {
	public static final FloatBimapper	instance	= new FloatBimapper();

	private FloatBimapper() {

	}


	@Override
	public Object map(Float in) {
		return in;
	}

	@Override
	public Float unmap(Object out) {
		if (out instanceof Float) {
			return (Float) out;
		}
		return out == null ? null : Float.valueOf(out.toString());
	}

}
